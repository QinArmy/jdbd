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

import io.jdbd.type.Point;
import io.jdbd.vendor.result.ColumnMeta;

import java.util.Locale;

public abstract class JdbdSpatials {

    protected JdbdSpatials() {
        throw new UnsupportedOperationException();
    }

    public static byte[] writePointToWkb(final boolean bigEndian, final Point point) {
        final byte[] wkbArray = new byte[21];

        int offset = 0;
        wkbArray[offset++] = (byte) (bigEndian ? 0 : 1);
        JdbdNumbers.writeInt(1, bigEndian, wkbArray, offset); // wkb type
        offset += 4;

        JdbdNumbers.writeLong(Double.doubleToLongBits(point.getX()), bigEndian, wkbArray, offset);
        offset += 8;
        JdbdNumbers.writeLong(Double.doubleToLongBits(point.getY()), bigEndian, wkbArray, offset);
        return wkbArray;
    }

    public static String writePointToWkt(final Point point) {
        return JdbdStrings.builder(18)
                .append("Point(")
                .append(point.getX())
                .append(' ')
                .append(point.getY())
                .append(')')
                .toString();
    }

    public static Point readPointWkb(final ColumnMeta meta, final byte[] wkb) {
        return readPointWkb(meta, wkb, 0);
    }

    public static Point readPointWkb(final ColumnMeta meta, final byte[] wkb, int offset) {
        if (offset < 0 || offset + 21 > wkb.length) {
            throw JdbdExceptions.cannotConvertColumnValue(meta, wkb, Point.class, null);
        }
        final boolean bigEndian;
        bigEndian = isBigEndian(meta, wkb, wkb[offset++], Point.class);

        if (JdbdNumbers.readInt(bigEndian, wkb, offset) != 1) { // wkbPoint=1
            throw JdbdExceptions.cannotConvertColumnValue(meta, wkb, Point.class, null);
        }
        offset += 4;
        final double x, y;
        x = Double.longBitsToDouble(JdbdNumbers.readLong(bigEndian, wkb, offset));

        offset += 8;

        y = Double.longBitsToDouble(JdbdNumbers.readLong(bigEndian, wkb, offset));
        return Point.from(x, y);
    }

    public static Point readPointWkt(final ColumnMeta meta, String text) {
        final String wkt;
        wkt = text.toUpperCase(Locale.ROOT).trim();
        if (!wkt.startsWith("POINT") || !wkt.endsWith(")")) {
            throw JdbdExceptions.cannotConvertColumnValue(meta, wkt, Point.class, null);
        }
        int offset = 5;
        final int length = wkt.length();

        char ch;
        for (; offset < length; offset++) {
            ch = wkt.charAt(offset);
            if (ch == ' ') {
                continue;
            }
            if (ch != '(') {
                throw JdbdExceptions.cannotConvertColumnValue(meta, wkt, Point.class, null);
            }
            offset++;
            break;
        }

        for (; offset < length; offset++) {
            ch = wkt.charAt(offset);
            if (ch == ' ') {
                continue;
            }
            break;
        }

        if (offset == length) {
            throw JdbdExceptions.cannotConvertColumnValue(meta, wkt, Point.class, null);
        }


        try {
            int spaceIndex;
            spaceIndex = wkt.indexOf(' ', offset);
            final double x, y;
            x = Double.parseDouble(wkt.substring(offset, spaceIndex));
            offset = spaceIndex + 1;
            for (; offset < length; offset++) {
                ch = wkt.charAt(offset);
                if (ch == ' ') {
                    continue;
                }
                break;
            }
            spaceIndex = wkt.indexOf(' ', offset);
            if (spaceIndex < 0) {
                spaceIndex = wkt.indexOf(')', offset);
            }
            y = Double.parseDouble(wkt.substring(offset, spaceIndex));

            return Point.from(x, y);
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            throw JdbdExceptions.cannotConvertColumnValue(meta, wkt, Point.class, e);
        }
    }


    private static boolean isBigEndian(final ColumnMeta meta, final byte[] wkb, byte byteOrder, Class<?> type) {
        final boolean bigEndian;
        switch (byteOrder) {
            case 0:
                bigEndian = true;
                break;
            case 1:
                bigEndian = false;
                break;
            default:
                throw JdbdExceptions.cannotConvertColumnValue(meta, wkb, type, null);
        }
        return bigEndian;
    }


}
