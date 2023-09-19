package io.jdbd.util;

import io.jdbd.JdbdException;
import io.jdbd.lang.Nullable;
import io.jdbd.type.PathParameter;
import io.jdbd.type.TextPath;

import java.io.*;
import java.nio.channels.Channels;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public abstract class JdbdUtils {

    private JdbdUtils() {
        throw new UnsupportedOperationException();
    }


    private static final byte[] UPPER_CASE_HEX_DIGITS = new byte[]{
            (byte) '0', (byte) '1', (byte) '2', (byte) '3'
            , (byte) '4', (byte) '5', (byte) '6', (byte) '7'
            , (byte) '8', (byte) '9', (byte) 'A', (byte) 'B'
            , (byte) 'C', (byte) 'D', (byte) 'E', (byte) 'F'};

    private static final byte[] LOWER_CASE_HEX_DIGITS = new byte[]{
            (byte) '0', (byte) '1', (byte) '2', (byte) '3'
            , (byte) '4', (byte) '5', (byte) '6', (byte) '7'
            , (byte) '8', (byte) '9', (byte) 'a', (byte) 'b'
            , (byte) 'c', (byte) 'd', (byte) 'e', (byte) 'f'};


    public static boolean hasNoText(final @Nullable String str) {
        final int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return true;
        }
        boolean match = true;
        for (int i = 0; i < strLen; i++) {
            if (Character.isWhitespace(str.charAt(i))) {
                continue;
            }
            match = false;
            break;
        }
        return match;
    }

    /**
     * load properties from path
     *
     * @param path properties file path
     * @return properties map
     * @throws JdbdException throw when occur error
     * @see io.jdbd.Driver#forDeveloper(String, Map)
     */
    public static Map<String, Object> loadProperties(final Path path) throws JdbdException {

        try (InputStream in = Files.newInputStream(path, StandardOpenOption.READ)) {
            final Properties properties = new Properties();
            properties.load(in);

            final Map<String, Object> map = new HashMap<>((int) (properties.size() / 0.75f));
            for (Map.Entry<Object, Object> entry : properties.entrySet()) {
                map.put(entry.getKey().toString(), entry.getValue());
            }
            return map;
        } catch (Exception e) {
            String m = String.format("load properties %s occur error", path);
            throw new JdbdException(m, e);
        }
    }

    /**
     * @return a unmodified set.
     */
    public static Set<OpenOption> openOptionSet(final PathParameter parameter) {
        final Set<OpenOption> optionSet;
        if (parameter.isDeleteOnClose()) {
            final Set<OpenOption> temp = new HashSet<>(4);
            temp.add(StandardOpenOption.READ);
            temp.add(StandardOpenOption.DELETE_ON_CLOSE);
            optionSet = Collections.unmodifiableSet(temp);
        } else {
            optionSet = Collections.singleton(StandardOpenOption.READ);
        }
        return optionSet;
    }

    public static BufferedReader newBufferedReader(final TextPath textPath) throws IOException {
        return newBufferedReader(textPath, 4096);
    }

    public static BufferedReader newBufferedReader(final TextPath textPath, final int bufferSize) throws IOException {
        final SeekableByteChannel channel;
        channel = Files.newByteChannel(textPath.value(), openOptionSet(textPath));

        final InputStream inputStream;
        inputStream = Channels.newInputStream(channel);

        final InputStreamReader inReader;
        inReader = new InputStreamReader(inputStream, textPath.charset().newDecoder());

        return new BufferedReader(inReader, bufferSize);
    }


    public static String hexEscapesText(boolean upperCase, byte[] bufferArray) {
        return new String(hexEscapes(upperCase, bufferArray, 0, bufferArray.length), StandardCharsets.UTF_8);
    }

    public static String hexEscapesText(boolean upperCase, byte[] bufferArray, final int length) {
        return new String(hexEscapes(upperCase, bufferArray, 0, length), StandardCharsets.UTF_8);
    }

    public static String hexEscapesText(boolean upperCase, byte[] bufferArray, final int offset, final int end) {
        return new String(hexEscapes(upperCase, bufferArray, offset, end), StandardCharsets.UTF_8);
    }

    public static byte[] hexEscapes(boolean upperCase, byte[] bufferArray) {
        return hexEscapes(upperCase, bufferArray, 0, bufferArray.length);
    }

    public static byte[] hexEscapes(boolean upperCase, byte[] bufferArray, final int end) {
        return hexEscapes(upperCase, bufferArray, 0, end);
    }

    public static byte[] hexEscapes(boolean upperCase, final byte[] bufferArray, final int offset, final int end) {
        if (offset < 0 || end > bufferArray.length || offset > end) {
            throw offsetAndEndError(bufferArray, offset, end);
        }
        final byte[] hexDigits = upperCase ? UPPER_CASE_HEX_DIGITS : LOWER_CASE_HEX_DIGITS;
        final byte[] hexDigitArray = new byte[(end - offset) << 1];
        byte b;
        for (int i = offset, j = 0; i < end; i++, j += 2) {
            b = bufferArray[i];
            hexDigitArray[j] = hexDigits[(b >> 4) & 0xF]; // write highBits
            hexDigitArray[j + 1] = hexDigits[b & 0xF]; // write lowBits
        }
        return hexDigitArray;
    }

    public static byte[] decodeHex(byte[] hexBytes) {
        return decodeHex(hexBytes, 0, hexBytes.length);
    }

    public static byte[] decodeHex(byte[] hexBytes, final int end) {
        return decodeHex(hexBytes, 0, end);
    }

    public static byte[] decodeHex(final byte[] hexBytes, final int offset, final int end) {
        if (offset < 0 || end > hexBytes.length || offset > end) {
            throw offsetAndEndError(hexBytes, offset, end);
        }
        final byte[] digitArray = new byte[(end - offset) >> 1];
        final int num0 = '0', num9 = '9', a = 'a', f = 'f', A = 'A', F = 'F';
        final int intervalOfa = a - 10, intervalOfA = A - 10;
        for (int i = 0, j = offset; i < digitArray.length; i++, j += 2) {

            for (int k = j, b; k < j + 2; k++) {
                b = hexBytes[k];
                if (b >= num0 && b <= num9) {
                    b -= num0;
                } else if (b >= a && b <= f) {
                    b -= intervalOfa;
                } else if (b >= A && b <= F) {
                    b -= intervalOfA;
                } else {
                    throw new IllegalArgumentException("non-hex");
                }
                digitArray[i] = (byte) ((digitArray[i] << 4) | b);
            }

        }
        return digitArray;
    }

    public static String decodeHexAsString(final byte[] hexBytes, final int offset, final int end) {
        return new String(decodeHex(hexBytes, offset, end), StandardCharsets.UTF_8);
    }

    public static String decodeHexAsString(final String hexStr) {
        final byte[] hexBytes = hexStr.getBytes(StandardCharsets.UTF_8);
        return new String(decodeHex(hexBytes, 0, hexBytes.length), StandardCharsets.UTF_8);
    }

    public static <K, V> ConcurrentHashMap<K, V> concurrentHashMap() {
        return new FinalConcurrentHashMap<>();
    }

    public static <K, V> ConcurrentHashMap<K, V> concurrentHashMap(int capacity) {
        return new FinalConcurrentHashMap<>(capacity);
    }


    public static IllegalArgumentException requiredText(String paramName) {
        return new IllegalArgumentException(String.format("%s must have text", paramName));
    }

    private static IllegalArgumentException offsetAndEndError(byte[] array, final int offset, final int end) {
        return new IllegalArgumentException(
                String.format("offset[%s],end[%s],length[%s] not match", offset, end, array.length));
    }


    private static final class FinalConcurrentHashMap<K, V> extends ConcurrentHashMap<K, V> {

        private FinalConcurrentHashMap() {
        }

        private FinalConcurrentHashMap(int initialCapacity) {
            super(initialCapacity);
        }


    }//FinalConcurrentHashMap


    private void readObject(ObjectInputStream in) throws IOException {
        throw new InvalidObjectException("can't deserialize JdbdUtils");
    }


    private void readObjectNoData() throws ObjectStreamException {
        throw new InvalidObjectException("can't deserialize JdbdUtils");
    }

}
