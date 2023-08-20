package io.jdbd.vendor.util;

import io.jdbd.type.Point;

public abstract class JdbdSpatials {

    protected JdbdSpatials() {
        throw new UnsupportedOperationException();
    }

    public static byte[] writePointToWkb(final boolean bigEndian, final Point point) {
        final byte[] wkbArray = new byte[21];

        int offset = 0;
        wkbArray[offset++] = (byte) (bigEndian ? 0 : 1);
        JdbdNumbers.writeInt(1, bigEndian, wkbArray, offset);
        offset += 4;

        JdbdNumbers.writeLong(Double.doubleToLongBits(point.getX()), bigEndian, wkbArray, offset);
        offset += 8;
        JdbdNumbers.writeLong(Double.doubleToLongBits(point.getY()), bigEndian, wkbArray, offset);
        return wkbArray;
    }



}
