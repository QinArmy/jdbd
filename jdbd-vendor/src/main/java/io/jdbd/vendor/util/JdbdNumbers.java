package io.jdbd.vendor.util;

import io.qinarmy.util.NumberUtils;

public abstract class JdbdNumbers extends NumberUtils {

    protected JdbdNumbers() {
        throw new UnsupportedOperationException();
    }


    public static short mapToShort(Object nonNull) {
        short value;
        if (nonNull instanceof Number) {
            value = convertNumberToShort((Number) nonNull);
        } else if (nonNull instanceof String) {
            value = Short.parseShort((String) nonNull);
        } else {
            String m = String.format("Not support java type[%s] to short.", nonNull.getClass().getName());
            throw new IllegalArgumentException(m);
        }
        return value;
    }


    public static void writeInt(final int bitSet, final boolean bigEndian, final byte[] wkbArray, final int offset) {
        final int end = offset + 4;
        if (offset < 0) {
            throw new IllegalArgumentException("offset error");
        } else if (wkbArray.length < end) {
            throw new IllegalArgumentException("overflow");
        }
        if (bigEndian) {
            for (int i = offset, bitCount = 24; i < end; i++, bitCount -= 8) {
                wkbArray[i] = (byte) (bitSet >> bitCount);
            }
        } else {
            for (int i = offset, bitCount = 0; i < end; i++, bitCount += 8) {
                wkbArray[i] = (byte) (bitSet >> bitCount);
            }
        }


    }

    public static void writeLong(final long bitSet, final boolean bigEndian, final byte[] wkbArray, final int offset) {
        final int end = offset + 8;
        if (offset < 0) {
            throw new IllegalArgumentException("offset error");
        } else if (wkbArray.length < end) {
            throw new IllegalArgumentException("overflow");
        }
        if (bigEndian) {
            for (int i = offset, bitCount = 56; i < end; i++, bitCount -= 8) {
                wkbArray[i] = (byte) (bitSet >> bitCount);
            }
        } else {
            for (int i = offset, bitCount = 0; i < end; i++, bitCount += 8) {
                wkbArray[i] = (byte) (bitSet >> bitCount);
            }
        }


    }

    public static byte[] toBinaryBytes(final int value, final boolean bigEndian) {
        return toBinaryBytes(value & 0xFFFF_FFFFL, bigEndian);
    }

    public static byte[] toBinaryBytes(final long value, final boolean bigEndian) {
//        long bitSite = (0xFFL << 56);
//        int byteLength = 8;
//        while ((value & bitSite) == 0) {
//            bitSite >>>= 8;
//            byteLength--;
//            if (byteLength == 1) {
//                break;
//            }
//        }
        final byte[] bytes = new byte[8];
        if (bigEndian) {
            for (int i = 0, bits = (bytes.length - 1) << 3; i < bytes.length; i++, bits -= 8) {
                bytes[i] = (byte) (value >> bits);
            }
        } else {
            for (int i = 0, bits = 0; i < bytes.length; i++, bits += 8) {
                bytes[i] = (byte) (value >> bits);
            }
        }
        return bytes;
    }


    /**
     * Determine whether the given {@code value} String indicates a hex number,
     * i.e. needs to be passed into {@code Integer.decode} instead of
     * {@code Integer.valueOf}, etc.
     */
    public static boolean isHexNumber(final String value) {
        final int index = (value.startsWith("-") ? 1 : 0);
        return (value.startsWith("0x", index) || value.startsWith("0X", index) || value.startsWith("#", index));
    }
}
