package io.jdbd.vendor.util;

import io.jdbd.type.WkbType;
import org.qinarmy.util.BufferWrapper;
import org.qinarmy.util.Pair;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Stack;

public abstract class Geometries extends GenericGeometries {

    public static final byte WKB_POINT_BYTES = 21;


    public static byte[] pointToWkb(final String pointWkt, final boolean bigEndian) {
        String startMarker = "WkbType.POINT.wktType(", endMarker = ")";
        if (!pointWkt.startsWith(startMarker) || !pointWkt.endsWith(endMarker)) {
            throw createWktFormatError(WkbType.POINT.name());
        }
        String[] coordinateArray = pointWkt.substring(startMarker.length(), pointWkt.length() - 1).split(" ");
        if (coordinateArray.length != 2) {
            throw createWktFormatError(WkbType.POINT.name());
        }
        double x, y;
        x = Double.parseDouble(coordinateArray[0]);
        y = Double.parseDouble(coordinateArray[1]);

        byte[] wkbArray = new byte[WKB_POINT_BYTES];
        int offset = 0;
        if (bigEndian) {
            wkbArray[offset++] = 0;
            JdbdNumberUtils.intToBigEndian(WkbType.POINT.code, wkbArray, offset, 4);
            offset += 4;
            JdbdNumberUtils.doubleToEndian(true, x, wkbArray, offset);
            offset += 8;
            JdbdNumberUtils.doubleToEndian(true, y, wkbArray, offset);
        } else {
            wkbArray[offset++] = 1;
            JdbdNumberUtils.intToLittleEndian(WkbType.POINT.code, wkbArray, offset, 4);
            offset += 4;
            JdbdNumberUtils.doubleToEndian(false, x, wkbArray, offset);
            offset += 8;
            JdbdNumberUtils.doubleToEndian(false, y, wkbArray, offset);
        }
        return wkbArray;
    }


    public static void pointWkbReverse(final byte[] wkbArray) {
        if (wkbArray.length < WKB_POINT_BYTES) {
            throw createWkbLengthError(WkbType.POINT.wktType, wkbArray.length, WKB_POINT_BYTES);
        }

        checkByteOrder(wkbArray[0]);

        wkbArray[0] ^= 1;
        int offset = 1;
        JdbdArrayUtils.reverse(wkbArray, offset, 4, 1);
        offset += 4;
        JdbdArrayUtils.reverse(wkbArray, offset, 8, 2);

    }

    /**
     * @see #geometryWkbReverse(WkbType, byte[])
     */
    public static void lineStringWkbReverse(final byte[] wkbArray) {
        int elementCount;
        elementCount = checkAndReverseHeader(wkbArray, WkbType.LINE_STRING, count -> count << 4);

        JdbdArrayUtils.reverse(wkbArray, HEADER_LENGTH, 8, elementCount << 1);
    }

    /**
     * @see #geometryWkbReverse(WkbType, byte[])
     */
    public static void polygonWkbReverse(final byte[] wkbArray) {
        final int elementCount;
        elementCount = checkAndReverseHeader(wkbArray, WkbType.POLYGON, count -> (count << 2) + (count << 6));
        lineStringElementReverse(WkbType.POLYGON, elementCount, wkbArray, HEADER_LENGTH);
    }


    /**
     * @see #geometryWkbReverse(WkbType, byte[])
     */
    public static void multiPointWkbReverse(final byte[] wkbArray) {
        int elementCount;
        elementCount = checkAndReverseHeader(wkbArray, WkbType.MULTI_POINT, count -> count << 4);
        JdbdArrayUtils.reverse(wkbArray, HEADER_LENGTH, 8, elementCount << 1);
    }


    /**
     * @see #geometryWkbReverse(WkbType, byte[])
     */
    public static void multiLineStringWkbReverse(final byte[] wkbArray) {
        final int elementCount;
        elementCount = checkAndReverseHeader(wkbArray, WkbType.MULTI_LINE_STRING, count -> (count << 2) + (count << 5));
        lineStringElementReverse(WkbType.MULTI_LINE_STRING, elementCount, wkbArray, HEADER_LENGTH);
    }

    public static void multiPolygonWkbReverse(final byte[] wkbArray) {
        final int elementCount;
        elementCount = checkAndReverseHeader(wkbArray, WkbType.MULTI_POLYGON, count -> 80 * count);

        polygonElementWkbReverse(WkbType.MULTI_POLYGON, wkbArray, HEADER_LENGTH, elementCount);

    }

    protected static int polygonElementWkbReverse(final WkbType wkbType, final byte[] wkbArray, int offset
            , final int elementCount) {
        final boolean bigEndian = wkbArray[0] == 1;// reversed.

        for (int i = 0, linearCount; i < elementCount; i++) {
            linearCount = JdbdNumberUtils.readIntFromEndian(bigEndian, wkbArray, offset, 4);
            if (linearCount < 0) {
                throw createIllegalWkbLengthError(wkbArray.length, offset + Integer.toUnsignedLong(linearCount));
            }
            JdbdArrayUtils.reverse(wkbArray, offset, 4, 1); // reverse lineCount
            offset += 4;
            offset = lineStringElementReverse(wkbType, linearCount, wkbArray, offset);
        }
        return offset;
    }

    public static void geometryCollectionWkbReverse(final byte[] wkbArray) {
        final int collectionCount;
        collectionCount = checkAndReverseHeader(wkbArray, WkbType.GEOMETRY_COLLECTION, count -> count * 20);

        final boolean bigEndian = wkbArray[0] == 1;// reversed.
        final Stack<Pair<Integer, Integer>> pairStack = new Stack<>();
        pairStack.push(new Pair<>(collectionCount, 0));
        int elementCount, offset = HEADER_LENGTH;
        Pair<Integer, Integer> pair;
        long needBytes = offset;
        while (!pairStack.isEmpty()) {

            pair = pairStack.pop();
            elementCount = pair.getFirst();

            elementFor:
            for (int i = pair.getSecond(), itemCount, wkbTypeCode; i < elementCount; i++) {
                needBytes += 4L;
                if (wkbArray.length < needBytes) {
                    throw createIllegalWkbLengthError(wkbArray.length, needBytes);
                }
                wkbTypeCode = JdbdNumberUtils.readIntFromEndian(bigEndian, wkbArray, offset, 4);
                JdbdArrayUtils.reverse(wkbArray, offset, 4, 1);

                offset += 4;
                if (wkbTypeCode == WkbType.Constant.POINT) {
                    itemCount = 1;
                } else {
                    needBytes += 4;
                    if (wkbArray.length > needBytes) {
                        throw createIllegalWkbLengthError(wkbArray.length, needBytes);
                    }
                    itemCount = JdbdNumberUtils.readIntFromEndian(bigEndian, wkbArray, offset, 4);
                    JdbdArrayUtils.reverse(wkbArray, offset, 4, 1);
                    offset += 4;
                }

                switch (wkbTypeCode) {
                    case WkbType.Constant.POINT: {
                        needBytes += 16;
                        if (wkbArray.length < needBytes) {
                            throw createIllegalWkbLengthError(wkbArray.length, needBytes);
                        }
                        JdbdArrayUtils.reverse(wkbArray, offset, 8, 2);
                        offset += 16;
                    }
                    break;
                    case WkbType.Constant.MULTI_POINT:
                    case WkbType.Constant.LINE_STRING: {
                        needBytes += ((long) itemCount << 4);
                        if (wkbArray.length < needBytes) {
                            throw createIllegalWkbLengthError(wkbArray.length, needBytes);
                        }
                        JdbdArrayUtils.reverse(wkbArray, offset, 8, itemCount << 1);
                        offset += (itemCount << 4);
                    }
                    break;
                    case WkbType.Constant.MULTI_LINE_STRING:
                    case WkbType.Constant.POLYGON:
                        offset = lineStringElementReverse(WkbType.POLYGON, itemCount, wkbArray, offset);
                        break;
                    case WkbType.Constant.MULTI_POLYGON:
                        offset = polygonElementWkbReverse(WkbType.MULTI_POLYGON, wkbArray, offset, itemCount);
                        break;
                    case WkbType.Constant.GEOMETRY_COLLECTION:
                        pairStack.push(new Pair<>(elementCount, i + 1));
                        pairStack.push(new Pair<>(itemCount, 0));
                        break elementFor;
                    default:
                        throw createUnknownWkbTypeError(wkbTypeCode);
                }
            } // elementFor


        }


    }


    public static String pointToWkt(final byte[] wkbArray) {
        if (wkbArray.length != WKB_POINT_BYTES) {
            throw createWkbLengthError(WkbType.POINT.wktType, wkbArray.length, WKB_POINT_BYTES);
        }
        final byte byteOrder = checkByteOrder(wkbArray[0]);
        int offset = 1;
        final int wkbType;
        final double x, y;
        if (byteOrder == 0) {
            wkbType = JdbdNumberUtils.readIntFromBigEndian(wkbArray, offset, 4);
            if (wkbType != WkbType.POINT.code) {
                throw createWkbTypeNotMatchError(WkbType.POINT.wktType, wkbType);
            }
            offset += 4;
            x = Double.longBitsToDouble(JdbdNumberUtils.readLongFromBigEndian(wkbArray, offset, 8));
            offset += 8;
            y = Double.longBitsToDouble(JdbdNumberUtils.readLongFromBigEndian(wkbArray, offset, 8));
        } else {
            wkbType = JdbdNumberUtils.readIntFromLittleEndian(wkbArray, offset, 4);
            if (wkbType != WkbType.POINT.code) {
                throw createWkbTypeNotMatchError(WkbType.POINT.wktType, wkbType);
            }
            offset += 4;
            x = Double.longBitsToDouble(JdbdNumberUtils.readLongFromLittleEndian(wkbArray, offset, 8));
            offset += 8;
            y = Double.longBitsToDouble(JdbdNumberUtils.readLongFromLittleEndian(wkbArray, offset, 8));
        }
        return String.format("POINT(%s %s)", x, y);
    }

    public static byte[] lineStringToWkb(final String wkt, final boolean bigEndian) {

        final BufferWrapper inWrapper = new BufferWrapper(wkt.getBytes(StandardCharsets.US_ASCII));
        //1.read wkt type.
        if (!readWktType(inWrapper, WkbType.LINE_STRING.name())) {
            throw createWktFormatError(WkbType.LINE_STRING.name());
        }
        final ByteBuffer inBuffer = inWrapper.buffer;
        if (!inBuffer.hasRemaining()) {
            throw createWktFormatError(WkbType.LINE_STRING.name());
        }

        try (ByteArrayOutputStream out = new ByteArrayOutputStream(inWrapper.bufferArray.length)) {
            final BufferWrapper outWrapper = new BufferWrapper(inWrapper.bufferArray.length);
            writeWkbPrefix(bigEndian, WkbType.LINE_STRING.code, 0, outWrapper.bufferArray, 0);
            out.write(outWrapper.bufferArray, 0, 9);

            int elementCount = 0;
            while (inBuffer.hasRemaining()) {
                elementCount += readAndWritePoints(bigEndian, 2, false, inWrapper, outWrapper);
                outWrapper.buffer.flip();
                out.write(outWrapper.bufferArray, 0, outWrapper.buffer.limit());

                if (inBuffer.get(inBuffer.position() - 1) == ')') {
                    break;
                }
            }
            if (elementCount < 2) {
                throw new IllegalArgumentException("LineString elementCount must great than 2.l");
            }
            // write elementCount
            byte[] wkbArray = out.toByteArray();
            if (bigEndian) {
                JdbdNumberUtils.intToBigEndian(elementCount, wkbArray, 5, 4);
            } else {
                JdbdNumberUtils.intToLittleEndian(elementCount, wkbArray, 5, 4);
            }
            return wkbArray;
        } catch (IOException e) {
            // no bug ,never here.
            throw new IllegalStateException(e.getMessage(), e);
        }

    }

    public static String lineStringToWkt(final byte[] wkbArray) {
        final BufferWrapper inWrapper = new BufferWrapper(wkbArray), outWrapper = new BufferWrapper(wkbArray.length);
        final ByteBuffer inBuffer = inWrapper.buffer, outBuffer = outWrapper.buffer;
        final byte[] outArray = outWrapper.bufferArray;

        outBuffer.put(WkbType.LINE_STRING.wktType.getBytes(StandardCharsets.US_ASCII));
        outBuffer.put((byte) '(');

        final Pair<Boolean, Integer> pair;
        pair = readWkbHead(wkbArray, 0, WkbType.LINE_STRING.code);
        inBuffer.position(9);

        final int elementCount = pair.getSecond();
        if (elementCount < 2) {
            throw createWkbTypeNotMatchError(WkbType.LINE_STRING.wktType, WkbType.LINE_STRING.code);
        }
        if (wkbArray.length != (9 + (elementCount << 4))) {
            throw createIllegalWkbLengthError(wkbArray.length, (9L + ((long) elementCount << 4)));
        }

        try (ByteArrayOutputStream out = new ByteArrayOutputStream(wkbArray.length)) {

            while (inBuffer.hasRemaining()) {
                writePointsAsWkt(pair.getFirst(), false, inWrapper, outWrapper);

                outBuffer.flip();
                out.write(outArray, 0, outBuffer.limit());
                outBuffer.clear();
            }
            out.write((byte) ')');
            return new String(out.toByteArray(), StandardCharsets.US_ASCII);
        } catch (IOException e) {
            // no bug ,never here.
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    /**
     * @return wkb md5
     */
    public static byte[] lineStringToWktPath(final boolean bigEndian, final Path wkbPath, final Path wktPath)
            throws IOException {
        final boolean wktPathExists;
        wktPathExists = Files.exists(wktPath, LinkOption.NOFOLLOW_LINKS);

        try (FileChannel in = FileChannel.open(wkbPath, StandardOpenOption.READ)) {
            try (FileChannel out = FileChannel.open(wktPath, StandardOpenOption.READ)) {
                return lineStringToWktChannel(bigEndian, in, out);
            }
        } catch (Throwable e) {
            if (wktPathExists) {
                JdbdStreamUtils.truncateIfExists(wkbPath, 0L);
            } else {
                Files.deleteIfExists(wkbPath);
            }
            if (e instanceof Error) {
                throw (Error) e;
            } else if (e instanceof IOException) {
                throw (IOException) e;
            } else {
                throw new IOException(e.getMessage(), e);
            }

        }
    }

    /**
     * @return wkb md5
     */
    public static byte[] lineStringToWktChannel(final boolean bigEndian, final FileChannel in, final FileChannel out)
            throws IOException {

        final BufferWrapper inWrapper = new BufferWrapper(1024), outWrapper = new BufferWrapper(1024);
        final ByteBuffer inBuffer = inWrapper.buffer, outBuffer = outWrapper.buffer;
        final byte[] inArray = inWrapper.bufferArray, outArray = outWrapper.bufferArray;

        if (in.read(inBuffer) < 9) {
            throw createWkbTypeNotMatchError(WkbType.LINE_STRING.wktType, WkbType.LINE_STRING.code);
        }
        inBuffer.flip();

        long readBytes = inBuffer.remaining();

        final Pair<Boolean, Integer> pair;
        pair = readWkbHead(inArray, 0, WkbType.LINE_STRING.code);
        inBuffer.position(9);

        final long needBytes = 9 + (Integer.toUnsignedLong(pair.getSecond()) << 4);

        if (in.size() < needBytes) {
            throw createWkbTypeNotMatchError(WkbType.LINE_STRING.wktType, WkbType.LINE_STRING.code);
        }
        final MessageDigest digest = JdbdDigestUtils.createMd5Digest();
        // write LINESTRING(
        outBuffer.put(WkbType.LINE_STRING.wktType.getBytes(StandardCharsets.US_ASCII));
        outBuffer.put((byte) '(');
        writePointsAsWkt(bigEndian, false, inWrapper, outWrapper);


        outBuffer.flip();
        out.write(outBuffer);
        outBuffer.rewind();
        digest.update(outArray, 0, outBuffer.limit());

        outBuffer.clear();

        // cumulate for next read.
        JdbdBufferUtils.cumulate(inBuffer, false);
        boolean outputEndMarker = false;
        for (int readLength; readBytes < needBytes; ) {
            if ((readLength = in.read(inBuffer)) < 0) {
                throw createWkbTypeNotMatchError(WkbType.LINE_STRING.wktType, WkbType.LINE_STRING.code);
            }
            readBytes += readLength;
            inBuffer.flip();

            while (inBuffer.remaining() > 16) {
                writePointsAsWkt(bigEndian, false, inWrapper, outWrapper);

                if (readBytes == needBytes && !inBuffer.hasRemaining()) {
                    outBuffer.put((byte) ')');
                    outputEndMarker = true;
                }

                outBuffer.flip();
                out.write(outBuffer);
                outBuffer.rewind();
                digest.update(outArray, 0, outBuffer.limit());

                outBuffer.clear();
            }
            // cumulate for next read.
            JdbdBufferUtils.cumulate(inBuffer, false);

        }
        if (!outputEndMarker) {
            outBuffer.put((byte) ')');

            outBuffer.flip();
            out.write(outBuffer);
            outBuffer.rewind();
            digest.update(outArray, 0, outBuffer.limit());

        }
        return digest.digest();
    }

    /**
     * @see #polygonWkbReverse(byte[])
     * @see #multiLineStringWkbReverse(byte[])
     */
    protected static int lineStringElementReverse(final WkbType wkbType, final int elementCount
            , final byte[] wkbArray, int offset) {
        final boolean bigEndian = wkbArray[0] == 1;// reversed.
        long needBytes = offset, pointCount;
        for (int i = 0; i < elementCount; i++) {
            needBytes += 4;
            if (wkbArray.length < needBytes) {
                throw createWkbLengthError(wkbType.wktType, wkbArray.length, needBytes);
            }
            if (bigEndian) {
                pointCount = JdbdNumberUtils.readIntFromBigEndian(wkbArray, offset, 4)
                        & JdbdNumberUtils.MAX_UNSIGNED_INT;
            } else {
                pointCount = JdbdNumberUtils.readIntFromLittleEndian(wkbArray, offset, 4)
                        & JdbdNumberUtils.MAX_UNSIGNED_INT;
            }
            needBytes += (pointCount << 4);

            if (wkbArray.length < needBytes) {
                throw createWkbLengthError(wkbType.wktType, wkbArray.length, needBytes);
            }
            JdbdArrayUtils.reverse(wkbArray, offset, 4, 1);
            offset += 4;
            JdbdArrayUtils.reverse(wkbArray, offset, 8, (int) pointCount << 1);
            offset += (pointCount << 4);
        }
        return offset;
    }

    protected static void writePointsAsWkt(final boolean bigEndian, final boolean pointText
            , final BufferWrapper inWrapper, final BufferWrapper outWrapper) {

        final ByteBuffer inBuffer = inWrapper.buffer, outBuffer = outWrapper.buffer;
        final byte[] inArray = inWrapper.bufferArray, outArray = outWrapper.bufferArray;
        final int inLimit = inBuffer.limit(), outLimit = outBuffer.limit();

        final boolean firstBuffer = Character.isLetter(outArray[0]);
        int inPosition = inBuffer.position(), outPosition = outBuffer.position();
        byte[] xBytes, yBytes;
        double x, y;
        for (int writeNeedBytes, i = 0; ; i++) {
            if (inLimit - inPosition < 16) {
                break;
            }
            x = JdbdNumberUtils.readDoubleFromEndian(bigEndian, inArray, inPosition, 8);
            y = JdbdNumberUtils.readDoubleFromEndian(bigEndian, inArray, inPosition + 8, 8);
            xBytes = Double.toString(x).getBytes(StandardCharsets.US_ASCII);
            yBytes = Double.toString(y).getBytes(StandardCharsets.US_ASCII);

            writeNeedBytes = xBytes.length + 1 + yBytes.length;
            if (!firstBuffer || i > 0) {
                writeNeedBytes += 1;
            }
            if (pointText) {
                writeNeedBytes += 2;
            }

            if (outLimit - outPosition < writeNeedBytes) {
                break;
            }


            inPosition += 16;

            if (firstBuffer) {
                if (i > 0) {
                    outArray[outPosition++] = (byte) ',';
                }
            } else {
                outArray[outPosition++] = (byte) ',';
            }

            if (pointText) {
                outArray[outPosition++] = (byte) '(';
            }

            for (byte b : xBytes) {
                outArray[outPosition++] = b;
            }
            outArray[outPosition++] = (byte) ' ';
            for (byte b : yBytes) {
                outArray[outPosition++] = b;
            }
            if (pointText) {
                outArray[outPosition++] = (byte) ')';
            }


        }
        inBuffer.position(inPosition);
        outBuffer.position(outPosition);

    }


    public static byte[] lineStringPathToWkbFromPath(final Path wktPath, final long offset, final boolean bigEndian
            , final Path wkbPath) throws IOException {

        final boolean wkbPathExists;
        wkbPathExists = Files.exists(wkbPath, LinkOption.NOFOLLOW_LINKS);

        try (FileChannel in = FileChannel.open(wktPath, StandardOpenOption.READ)) {
            final long hasBytes;
            hasBytes = handleOffset(in, offset);
            final BufferWrapper inWrapper = new BufferWrapper((int) Math.min(hasBytes, 2048));
            final ByteBuffer inBuffer = inWrapper.buffer;
            //1.read wkt type.
            final int typeLength = WkbType.LINE_STRING.wktType.length();
            boolean validate = false;
            for (int readLength; (readLength = in.read(inBuffer)) > 0; ) {
                if (readLength < typeLength) {
                    throw createWktFormatError(WkbType.LINE_STRING.name());
                }
                inBuffer.flip();
                if (readWktType(inWrapper, WkbType.LINE_STRING.name())) {
                    validate = true;
                    break;
                }

            }
            if (!validate) {
                throw createWktFormatError(WkbType.LINE_STRING.name());
            }

            try (FileChannel out = FileChannel.open(wkbPath, StandardOpenOption.WRITE, StandardOpenOption.CREATE)) {
                // 2. parse and write wkb temp file.
                return writeLineStringWkbToPath(in, out, bigEndian, inWrapper);
            }
        } catch (Throwable e) {
            if (wkbPathExists) {
                JdbdStreamUtils.truncateIfExists(wkbPath, 0L);
            } else {
                Files.deleteIfExists(wkbPath);
            }
            if (e instanceof Error) {
                throw (Error) e;
            } else if (e instanceof IOException) {
                throw (IOException) e;
            } else {
                throw new IOException(e.getMessage(), e);
            }

        }

    }

    /*################################## blow protected method ##################################*/

    protected static void geometryWkbReverse(final WkbType wkbType, final byte[] wkbArray) {
        switch (wkbType) {
            case POINT:
                pointWkbReverse(wkbArray);
                break;
            case LINE_STRING:
                lineStringWkbReverse(wkbArray);
                break;
            case POLYGON:
                polygonWkbReverse(wkbArray);
                break;
            case MULTI_POINT:
                multiPointWkbReverse(wkbArray);
                break;
            case MULTI_LINE_STRING:
                multiLineStringWkbReverse(wkbArray);
                break;
            case MULTI_POLYGON:
                multiPolygonWkbReverse(wkbArray);
                break;
            case GEOMETRY_COLLECTION:
                geometryCollectionWkbReverse(wkbArray);
                break;
            case CIRCULAR_STRING:
            case COMPOUND_CURVE:
            case CURVE_POLYGON:
            case MULTI_CURVE:

            case MULTI_SURFACE:
            case CURVE:
            case SURFACE:
            case POLYHEDRAL_SURFACE:

            case TIN:
            case GEOMETRY:
                throw new IllegalArgumentException(String.format("Unsupported type[%s].", wkbType));
            default:
                throw JdbdExceptions.createUnknownEnumException(wkbType);
        }
    }

    /**
     * @return first:true big-endian,second : pointSize
     */
    protected static Pair<Boolean, Integer> readWkbHead(final byte[] wkbBytes, int offset, final int expectWkbType) {
        if (wkbBytes.length < 9) {
            throw new IllegalArgumentException(String.format("wkbBytes length[%s] less than 9.", wkbBytes.length));
        }
        if (wkbBytes.length - offset < 9) {
            throw new IllegalArgumentException(String.format("wkbBytes length[%s] - offset less than 9."
                    , wkbBytes.length));
        }
        // 1. below parse byteOrder,wkbType,pointSize
        final int wkbType, pointSize;
        final byte byteOrder = wkbBytes[offset++];
        if (byteOrder == 0) {
            // big-endian
            wkbType = JdbdNumberUtils.readIntFromBigEndian(wkbBytes, offset, 4);
            if (wkbType != expectWkbType) {
                throw new IllegalArgumentException(String.format("WKB-TYPE[%s] isn't [%s]"
                        , wkbType, expectWkbType));
            }
            offset += 4;
            pointSize = JdbdNumberUtils.readIntFromBigEndian(wkbBytes, offset, 4);
        } else if (byteOrder == 1) {
            // little-endian
            wkbType = JdbdNumberUtils.readIntFromLittleEndian(wkbBytes, offset, 4);
            if (wkbType != expectWkbType) {
                throw new IllegalArgumentException(String.format("WKB-TYPE[%s] isn't [%s]"
                        , wkbType, expectWkbType));
            }
            offset += 4;
            pointSize = JdbdNumberUtils.readIntFromLittleEndian(wkbBytes, offset, 4);
        } else {
            throw createIllegalByteOrderError(byteOrder);
        }
        return new Pair<>(byteOrder == 0, pointSize);
    }

    /**
     * @see #lineStringPathToWkbFromPath(Path, long, boolean, Path)
     */
    protected static boolean readWktType(final BufferWrapper inWrapper, final String wktType)
            throws IllegalArgumentException {

        final ByteBuffer buffer = inWrapper.buffer;
        final byte[] bufferArray = inWrapper.bufferArray;

        final int typeLength = wktType.length();
        int limit, position, headerIndex;

        limit = buffer.limit();
        for (position = buffer.position(); position < limit; position++) {
            if (!Character.isWhitespace(bufferArray[position])) {
                break;
            }
        }
        if (limit - position < typeLength) {
            buffer.position(position);
            JdbdBufferUtils.cumulate(buffer, false);
            return false;
        }
        headerIndex = position;
        for (int i = 0; i < typeLength; i++, position++) {
            if (bufferArray[position] != wktType.charAt(i)) {
                throw createWktFormatError(wktType);
            }
        }
        for (; position < limit; position++) {
            if (!Character.isWhitespace(bufferArray[position])) {
                break;
            }
        }
        if (limit - position < 1) {
            buffer.position(headerIndex);
            JdbdBufferUtils.cumulate(buffer, true);
            return false;
        }
        if (bufferArray[position] != '(') {
            throw createWktFormatError(wktType);
        }
        buffer.position(position + 1);
        JdbdBufferUtils.cumulate(buffer, true);
        return true;
    }

    /**
     * @see #lineStringPathToWkbFromPath(Path, long, boolean, Path)
     */
    protected static int readAndWritePoints(final boolean bigEndian, final int coordinates, final boolean pointText
            , final BufferWrapper inWrapper, final BufferWrapper outWrapper)
            throws IllegalArgumentException {

        if (coordinates < 2 || coordinates > 4) {
            throw new IllegalArgumentException(String.format("coordinates[%s] error.", coordinates));
        }
        final byte[] inArray = inWrapper.bufferArray, outArray = outWrapper.bufferArray;
        final ByteBuffer inBuffer = inWrapper.buffer, outBuffer = outWrapper.buffer;

        final int inLimit = inBuffer.limit(), outLimit = outBuffer.limit();

        int codePoint, pointCount = 0, inPosition = inBuffer.position(), outPosition = outBuffer.position();
        topFor:
        for (int tempOutPosition, tempInPosition, pointEndIndex; inPosition < inLimit; ) {
            codePoint = inArray[inPosition];
            if (Character.isWhitespace(codePoint)) {
                inPosition++;
                continue;
            }
            if (outLimit - outPosition < (coordinates << 3)) {
                break;
            }
            tempInPosition = inPosition;
            tempOutPosition = outPosition;
            if (pointText) {
                if (codePoint != '(') {
                    throw new IllegalArgumentException("Not found '(' for point text.");
                }
                tempInPosition++;
            }

            //parse coordinates and write to outArray.
            for (int i = 1, startIndex, endIndex = tempInPosition - 1; i <= coordinates; i++) {
                startIndex = -1;
                for (tempInPosition = endIndex + 1; tempInPosition < inLimit; tempInPosition++) {
                    if (!Character.isWhitespace(inArray[tempInPosition])) {
                        startIndex = tempInPosition;
                        break;
                    }
                }
                if (startIndex < 0) {
                    break topFor;
                }
                endIndex = -1;
                for (tempInPosition = startIndex + 1; tempInPosition < inLimit; tempInPosition++) {
                    if (i == coordinates) {
                        // last coordinate.
                        codePoint = inArray[tempInPosition];
                        if (pointText) {
                            if (codePoint == ')' || Character.isWhitespace(codePoint)) {
                                endIndex = tempInPosition;
                            }
                        } else if (codePoint == ',' || codePoint == ')' || Character.isWhitespace(codePoint)) {
                            endIndex = tempInPosition;
                        }
                    } else if (Character.isWhitespace(inArray[tempInPosition])) {
                        endIndex = tempInPosition;
                        break;
                    }
                }
                if (endIndex < 0) {
                    if (tempInPosition - startIndex > 24) {
                        // non-double number
                        byte[] nonDoubleBytes = Arrays.copyOfRange(inArray, startIndex, tempInPosition);
                        throw createNonDoubleError(new String(nonDoubleBytes));
                    }
                    break topFor;
                }
                double d = Double.parseDouble(new String(Arrays.copyOfRange(inArray, startIndex, endIndex)));
                JdbdNumberUtils.doubleToEndian(bigEndian, d, outArray, tempOutPosition);
                tempOutPosition += 8;
            }// parse coordinates and write to outArray.
            // below find point end index
            pointEndIndex = -1;
            for (int parenthesisCount = 0; tempInPosition < inLimit; tempInPosition++) {
                codePoint = inArray[tempInPosition];
                if (Character.isWhitespace(codePoint)) {
                    continue;
                }
                if (codePoint == ')') {
                    parenthesisCount++;
                    if (pointText) {
                        if (parenthesisCount == 2) {
                            pointEndIndex = tempInPosition;
                            break;
                        }
                    } else {
                        pointEndIndex = tempInPosition;
                        break;
                    }
                } else if (codePoint == ',') {
                    if (pointText && parenthesisCount == 0) {
                        throw new IllegalArgumentException("point text not close.");
                    }
                    pointEndIndex = tempInPosition;
                    break;
                } else {
                    throw new IllegalArgumentException(String.format("point end with %s.", (char) codePoint));
                }
            }
            if (pointEndIndex < 0) {
                break;
            }
            outPosition = tempOutPosition;
            inPosition = pointEndIndex + 1;
            pointCount++;
            if (inArray[pointEndIndex] == ')') {
                break;
            }
        }
        inBuffer.position(inPosition);
        outBuffer.position(outPosition);

        return pointCount;
    }

    /**
     * @return temp file md5.
     */
    protected static byte[] writeLineStringWkbToPath(final FileChannel in, final FileChannel out
            , final boolean bigEndian, final BufferWrapper inWrapper)
            throws IOException, IllegalArgumentException {

        final byte[] inArray = inWrapper.bufferArray;
        final ByteBuffer inBuffer = inWrapper.buffer;

        final BufferWrapper outWrapper = new BufferWrapper(inArray.length);
        final byte[] outArray = outWrapper.bufferArray;
        final ByteBuffer outBuffer = outWrapper.buffer;

        final MessageDigest digest = JdbdDigestUtils.createMd5Digest();
        //1. write wkb header
        // 0 is  placeholder of element count.
        writeWkbPrefix(bigEndian, WkbType.LINE_STRING.code, 0, outArray, 0);
        outBuffer.position(9);
        //2. read and write points.
        long elementCount = 0L;
        boolean lineStringEnd = false;
        for (int endIndex; in.read(inBuffer) > 0; ) {
            inBuffer.flip();
            elementCount += readAndWritePoints(bigEndian, 2, false, inWrapper, outWrapper);
            endIndex = inBuffer.position() - 1;
            if (endIndex > -1 && inBuffer.get(endIndex) == ')') {
                lineStringEnd = true;
                outBuffer.flip();
                digest.update(outArray, 0, outBuffer.limit());
                out.write(outBuffer);
                outBuffer.clear();
                break;
            }
            if (outBuffer.remaining() < 16) {
                outBuffer.flip();
                digest.update(outArray, 0, outBuffer.limit());
                out.write(outBuffer);
                outBuffer.clear();
            }
            JdbdBufferUtils.cumulate(inBuffer, true);

        }
        if (!lineStringEnd) {
            throw new IllegalArgumentException("LineString not end.");
        }
        if (elementCount < 2 || elementCount > JdbdNumberUtils.MAX_UNSIGNED_INT) {
            throw new IllegalArgumentException(String.format("LineString length[%s] not in[2,%s]"
                    , elementCount, JdbdNumberUtils.MAX_UNSIGNED_INT));
        }
        // 3. write element count.
        out.position(5L); // to element count position.
        JdbdNumberUtils.intToLittleEndian((int) elementCount, outArray, 0, 4);
        outBuffer.position(4);
        outBuffer.flip();
        digest.update(outArray, 0, outBuffer.limit());
        out.write(outBuffer);

        return digest.digest();
    }

    /**
     * @return byte count of {@link FileChannel} hold.
     */
    protected static long handleOffset(FileChannel in, final long offset) throws IOException {
        final long hasBytes;
        if (offset > 0L) {
            hasBytes = in.size() - offset;
            if (hasBytes > 0L) {
                in.position(offset);
            }
        } else {
            hasBytes = in.size();
        }
        if (hasBytes < 9L) {
            throw new IOException("Not found WKB.");
        }
        return hasBytes;
    }


    protected static void writeWkbPrefix(final boolean bigEndian, final int wkbType, final int elementCount
            , final byte[] wkbBytes, int offset) {
        if (wkbBytes.length < 9) {
            throw new IllegalArgumentException(String.format("wkbBytes length[%s] less than 9", wkbBytes.length));
        }
        if (wkbBytes.length - offset < 9) {
            throw new IllegalArgumentException(String.format("wkbBytes (length[%s]-offset[%s]) less than 9"
                    , wkbBytes.length, offset));
        }
        if (bigEndian) {
            wkbBytes[offset++] = 0;
            JdbdNumberUtils.intToBigEndian(wkbType, wkbBytes, offset, 4);
            offset += 4;
            JdbdNumberUtils.intToBigEndian(elementCount, wkbBytes, offset, 4);
        } else {
            wkbBytes[offset++] = 1;
            JdbdNumberUtils.intToLittleEndian(wkbType, wkbBytes, offset, 4);
            offset += 4;
            JdbdNumberUtils.intToLittleEndian(elementCount, wkbBytes, offset, 4);
        }

    }


    /*################################## blow private method ##################################*/


    private static IllegalArgumentException createWktFormatError(String wktType) {
        return new IllegalArgumentException(String.format("Not %s WKT format.", wktType));
    }


    protected static IllegalArgumentException createNonDoubleError(String nonDouble) {
        return new IllegalArgumentException(String.format("%s isn't double number.", nonDouble));
    }


}
