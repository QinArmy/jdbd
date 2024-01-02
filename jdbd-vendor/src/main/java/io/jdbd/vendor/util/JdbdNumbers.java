/*
 * Copyright 2023-2043 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.jdbd.vendor.util;

import java.math.BigDecimal;

public abstract class JdbdNumbers {

    protected JdbdNumbers() {
        throw new UnsupportedOperationException();
    }


    public static boolean isDecimal(String text) {
        boolean match;
        try {
            new BigDecimal(text);
            match = true;
        } catch (NumberFormatException e) {
            match = false;
        }
        return match;
    }


    public static void writeInt(final int bitSet, final boolean bigEndian, final byte[] wkbArray, final int offset) {
        final int end = offset + 4;
        if (offset < 0) {
            throw new IllegalArgumentException("offset error");
        } else if (wkbArray.length < end) {
            throw new IllegalArgumentException("overflow");
        } else if (bigEndian) {
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

    public static int readInt(final boolean bigEndian, final byte[] wkbArray, final int offset) {
        final int end = offset + 4;

        if (offset < 0) {
            throw new IllegalArgumentException("offset error");
        } else if (wkbArray.length < end) {
            throw new IllegalArgumentException("overflow");
        }
        int value = 0;
        if (bigEndian) {
            for (int i = offset, bitCount = 24; i < end; i++, bitCount -= 8) {
                value |= ((wkbArray[i] & 0xff) << bitCount);
            }
        } else {
            for (int i = offset, bitCount = 0; i < end; i++, bitCount += 8) {
                value |= ((wkbArray[i] & 0xff) << bitCount);
            }
        }
        return value;
    }

    public static long readLong(final boolean bigEndian, final byte[] wkbArray, final int offset) {
        final int end = offset + 8;

        if (offset < 0) {
            throw new IllegalArgumentException("offset error");
        } else if (wkbArray.length < end) {
            throw new IllegalArgumentException("overflow");
        }
        long value = 0;
        if (bigEndian) {
            for (int i = offset, bitCount = 56; i < end; i++, bitCount -= 8) {
                value |= ((wkbArray[i] & 0xffL) << bitCount);
            }
        } else {
            for (int i = offset, bitCount = 0; i < end; i++, bitCount += 8) {
                value |= ((wkbArray[i] & 0xffL) << bitCount);
            }
        }
        return value;
    }

    public static byte[] toBinaryBytes(final int value, final boolean bigEndian) {
        final byte[] bytes = new byte[4];
        if (bigEndian) {
            for (int i = 0, bits = 24; i < bytes.length; i++, bits -= 8) {
                bytes[i] = (byte) (value >> bits);
            }
        } else {
            for (int i = 0, bits = 0; i < bytes.length; i++, bits += 8) {
                bytes[i] = (byte) (value >> bits);
            }
        }
        return bytes;
    }

    public static byte[] toBinaryBytes(final long value, final boolean bigEndian) {
        final byte[] bytes = new byte[8];
        if (bigEndian) {
            for (int i = 0, bits = 56; i < bytes.length; i++, bits -= 8) {
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
