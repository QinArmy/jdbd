package io.jdbd.vendor.util;

import io.jdbd.meta.DataType;
import io.jdbd.meta.JdbdType;
import io.jdbd.type.Point;
import io.jdbd.vendor.result.ColumnMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

public class JdbdSpatialsTests {

    private static final Logger LOG = LoggerFactory.getLogger(JdbdSpatialsTests.class);

    @Test
    public void readPointFromText() {
        final PseudoColumnMeta meta = PseudoColumnMeta.INSTANCE;
        String wkt;
        Point point;

        wkt = "Point ( 33.3 333.8)";
        point = JdbdSpatials.readPointWkt(meta, wkt);
        Assert.assertEquals(point.getX(), 33.3d);
        Assert.assertEquals(point.getY(), 333.8d);


        wkt = String.format("Point(%s %s)", Double.MAX_VALUE, Double.MIN_VALUE);
        point = JdbdSpatials.readPointWkt(meta, wkt);
        Assert.assertEquals(point.getX(), Double.MAX_VALUE);
        Assert.assertEquals(point.getY(), Double.MIN_VALUE);
    }


    private static final class PseudoColumnMeta implements ColumnMeta {

        static final PseudoColumnMeta INSTANCE = new PseudoColumnMeta();

        @Override
        public int getColumnIndex() {
            return 0;
        }

        @Override
        public DataType getDataType() {
            return JdbdType.GEOMETRY;
        }

        @Override
        public String getColumnLabel() {
            return "pseudo";
        }


        @Override
        public boolean isUnsigned() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isBit() {
            throw new UnsupportedOperationException();
        }

    }// PseudoColumnMeta


}
