package io.jdbd.postgre;


import io.jdbd.type.LongBinary;
import io.jdbd.type.geometry.Circle;
import io.jdbd.type.geometry.LineString;
import io.jdbd.type.geometry.LongString;
import io.jdbd.type.geometry.Point;

import java.math.BigDecimal;
import java.sql.JDBCType;
import java.time.*;
import java.time.temporal.TemporalAmount;
import java.util.*;

public enum PgType implements io.jdbd.meta.SQLType {

    UNSPECIFIED(PgConstant.TYPE_UNSPECIFIED, JDBCType.NULL, Object.class),
    SMALLINT(PgConstant.TYPE_INT2, JDBCType.SMALLINT, Short.class),
    INTEGER(PgConstant.TYPE_INT4, JDBCType.INTEGER, Integer.class),
    BIGINT(PgConstant.TYPE_INT8, JDBCType.BIGINT, Long.class),
    DECIMAL(PgConstant.TYPE_NUMERIC, JDBCType.DECIMAL, BigDecimal.class),
    REAL(PgConstant.TYPE_FLOAT4, JDBCType.FLOAT, Float.class),
    DOUBLE(PgConstant.TYPE_FLOAT8, JDBCType.DOUBLE, Double.class),
    BOOLEAN(PgConstant.TYPE_BOOLEAN, JDBCType.BOOLEAN, Boolean.class),
    BIT(PgConstant.TYPE_BIT, JDBCType.BIT, BitSet.class),
    VARBIT(PgConstant.TYPE_VARBIT, JDBCType.BIT, BitSet.class),
    TIMESTAMP(PgConstant.TYPE_TIMESTAMP, JDBCType.TIMESTAMP, LocalDateTime.class),
    DATE(PgConstant.TYPE_DATE, JDBCType.DATE, LocalDate.class),
    TIME(PgConstant.TYPE_TIME, JDBCType.TIME, LocalTime.class),
    TIMESTAMPTZ(PgConstant.TYPE_TIMESTAMPTZ, JDBCType.TIMESTAMP_WITH_TIMEZONE, OffsetDateTime.class),
    TIMETZ(PgConstant.TYPE_TIMETZ, JDBCType.TIME_WITH_TIMEZONE, OffsetTime.class),
    BYTEA(PgConstant.TYPE_BYTEA, JDBCType.LONGVARBINARY, LongBinary.class),
    CHAR(PgConstant.TYPE_CHAR, JDBCType.CHAR, String.class),
    VARCHAR(PgConstant.TYPE_VARCHAR, JDBCType.VARCHAR, String.class),
    MONEY(PgConstant.TYPE_MONEY, JDBCType.VARCHAR, String.class),//java.lang.String because format dependent on locale
    TEXT(PgConstant.TYPE_TEXT, JDBCType.LONGVARCHAR, LongString.class),
    TSVECTOR(PgConstant.TYPE_TSVECTOR, JDBCType.LONGVARCHAR, LongString.class),
    TSQUERY(PgConstant.TYPE_TSQUERY, JDBCType.LONGVARCHAR, LongString.class),
    OID(PgConstant.TYPE_OID, JDBCType.BIGINT, Long.class),
    INTERVAL(PgConstant.TYPE_INTERVAL, JDBCType.OTHER, TemporalAmount.class),
    INT4_RANGE(PgConstant.TYPE_INT4_RANGE, JDBCType.OTHER, String.class),
    UUID(PgConstant.TYPE_UUID, JDBCType.CHAR, UUID.class),
    XML(PgConstant.TYPE_XML, JDBCType.SQLXML, LongString.class),
    POINT(PgConstant.TYPE_POINT, JDBCType.OTHER, Point.class),
    BOX(PgConstant.TYPE_BOX, JDBCType.OTHER, String.class),
    LINE(PgConstant.TYPE_LINE, JDBCType.OTHER, String.class),
    LINE_SEGMENT(PgConstant.TYPE_LSEG, JDBCType.OTHER, String.class),
    PATH(PgConstant.TYPE_PATH, JDBCType.OTHER, LineString.class),
    POLYGON(PgConstant.TYPE_POLYGON, JDBCType.OTHER, LongString.class),
    CIRCLE(PgConstant.TYPE_CIRCLE, JDBCType.OTHER, Circle.class),
    JSON(PgConstant.TYPE_JSON, JDBCType.LONGVARCHAR, LongString.class),
    JSONB(PgConstant.TYPE_JSONB, JDBCType.LONGVARCHAR, LongString.class),
    MAC_ADDR(PgConstant.TYPE_MAC_ADDR, JDBCType.VARCHAR, String.class),
    MAC_ADDR8(PgConstant.TYPE_MAC_ADDR8, JDBCType.VARCHAR, String.class),
    INET(PgConstant.TYPE_INET, JDBCType.VARCHAR, String.class),
    CIDR(PgConstant.TYPE_CIDR, JDBCType.VARCHAR, String.class),
    REF_CURSOR(PgConstant.TYPE_REF_CURSOR, JDBCType.REF_CURSOR, Object.class),


    SMALLINT_ARRAY(PgConstant.TYPE_INT2_ARRAY, JDBCType.ARRAY, Object.class),
    INTEGER_ARRAY(PgConstant.TYPE_INT4_ARRAY, JDBCType.ARRAY, Object.class),
    BIGINT_ARRAY(PgConstant.TYPE_INT8_ARRAY, JDBCType.ARRAY, Object.class),
    TEXT_ARRAY(PgConstant.TYPE_TEXT_ARRAY, JDBCType.ARRAY, Object.class),
    DECIMAL_ARRAY(PgConstant.TYPE_NUMERIC_ARRAY, JDBCType.ARRAY, Object.class),
    REAL_ARRAY(PgConstant.TYPE_FLOAT4_ARRAY, JDBCType.ARRAY, Object.class),
    DOUBLE_ARRAY(PgConstant.TYPE_FLOAT8_ARRAY, JDBCType.ARRAY, Object.class),
    BOOLEAN_ARRAY(PgConstant.TYPE_BOOLEAN_ARRAY, JDBCType.ARRAY, Object.class),
    DATE_ARRAY(PgConstant.TYPE_DATE_ARRAY, JDBCType.ARRAY, Object.class),
    TIME_ARRAY(PgConstant.TYPE_TIME_ARRAY, JDBCType.ARRAY, Object.class),
    TIMETZ_ARRAY(PgConstant.TYPE_TIMETZ_ARRAY, JDBCType.ARRAY, Object.class),
    TIMESTAMP_ARRAY(PgConstant.TYPE_TIMESTAMP_ARRAY, JDBCType.ARRAY, Object.class),
    TIMESTAMPTZ_ARRAY(PgConstant.TYPE_TIMESTAMPTZ_ARRAY, JDBCType.ARRAY, Object.class),
    BYTEA_ARRAY(PgConstant.TYPE_BYTEA_ARRAY, JDBCType.ARRAY, Object.class),
    VARCHAR_ARRAY(PgConstant.TYPE_VARCHAR_ARRAY, JDBCType.ARRAY, Object.class),
    BOX_ARRAY(PgConstant.TYPE_BOX_ARRAY, JDBCType.ARRAY, Object.class),
    MONEY_ARRAY(PgConstant.TYPE_MONEY_ARRAY, JDBCType.ARRAY, Object.class),
    OID_ARRAY(PgConstant.TYPE_OID_ARRAY, JDBCType.ARRAY, Object.class),
    BIT_ARRAY(PgConstant.TYPE_BIT_ARRAY, JDBCType.ARRAY, Object.class),
    INTERVAL_ARRAY(PgConstant.TYPE_INTERVAL_ARRAY, JDBCType.ARRAY, Object.class),
    CHAR_ARRAY(PgConstant.TYPE_CHAR_ARRAY, JDBCType.ARRAY, Object.class),
    VARBIT_ARRAY(PgConstant.TYPE_VARBIT_ARRAY, JDBCType.ARRAY, Object.class),
    UUID_ARRAY(PgConstant.TYPE_UUID_ARRAY, JDBCType.ARRAY, Object.class),
    XML_ARRAY(PgConstant.TYPE_XML_ARRAY, JDBCType.ARRAY, Object.class),
    POINT_ARRAY(PgConstant.TYPE_POINT_ARRAY, JDBCType.ARRAY, Object.class),
    JSONB_ARRAY(PgConstant.TYPE_JSONB_ARRAY, JDBCType.ARRAY, Object.class),
    JSON_ARRAY(PgConstant.TYPE_JSON_ARRAY, JDBCType.ARRAY, Object.class),
    TSVECTOR_ARRAY(PgConstant.TYPE_TSVECTOR_ARRAY, JDBCType.ARRAY, Object.class),
    TSQUERY_ARRAY(PgConstant.TYPE_TSQUERY_ARRAY, JDBCType.ARRAY, Object.class),
    INT4_RANGE_ARRAY(PgConstant.TYPE_INT4_RANGE_ARRAY, JDBCType.ARRAY, Object.class),
    PATH_ARRAY(PgConstant.TYPE_PATH_ARRAY, JDBCType.ARRAY, Object.class),
    POLYGON_ARRAY(PgConstant.TYPE_POLYGON_ARRAY, JDBCType.ARRAY, Object.class),
    CIRCLES_ARRAY(PgConstant.TYPE_CIRCLES_ARRAY, JDBCType.ARRAY, Object.class),
    CIDR_ARRAY(PgConstant.TYPE_CIDR_ARRAY, JDBCType.ARRAY, Object.class),
    INET_ARRAY(PgConstant.TYPE_INET_ARRAY, JDBCType.ARRAY, Object.class),
    MACADDR_ARRAY(PgConstant.TYPE_MACADDR_ARRAY, JDBCType.ARRAY, Object.class),
    MACADDR8_ARRAY(PgConstant.TYPE_MACADDR8_ARRAY, JDBCType.ARRAY, Object.class),

    REF_CURSOR_ARRAY(PgConstant.TYPE_REF_CURSOR_ARRAY, JDBCType.ARRAY, Object.class);

    private static final Map<Integer, PgType> CODE_TO_TYPE_MAP = createCodeToTypeMap();

    private final int typeOid;

    private final JDBCType jdbcType;

    private final Class<?> javaType;

    PgType(int typeOid, JDBCType jdbcType, Class<?> javaType) {
        this.typeOid = typeOid;
        this.jdbcType = jdbcType;
        this.javaType = javaType;
    }

    @Override
    public final JDBCType jdbcType() {
        return this.jdbcType;
    }

    @Override
    public final Class<?> javaType() {
        return this.javaType;
    }

    @Override
    public final boolean isUnsigned() {
        return false;
    }

    @Override
    public final boolean isNumber() {
        return isIntegerType()
                || isFloatType()
                || isDecimal();
    }

    @Override
    public final boolean isIntegerType() {
        return this == SMALLINT
                || this == INTEGER
                || this == BIGINT;
    }

    @Override
    public final boolean isFloatType() {
        return this == REAL || this == DOUBLE;
    }

    @Override
    public final boolean isLongString() {
        return false;
    }

    @Override
    public boolean isLongBinary() {
        return false;
    }

    @Override
    public final boolean isString() {
        return false;
    }

    @Override
    public boolean isBinary() {
        return false;
    }

    @Override
    public boolean isTimeType() {
        return false;
    }

    @Override
    public boolean isDecimal() {
        return false;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public final String getVendor() {
        return PgType.class.getPackage().getName();
    }

    @Override
    public final Integer getVendorTypeNumber() {
        return this.typeOid;
    }


    public static PgType from(final int typeOid) {
        final PgType pgType;
        switch (typeOid) {
            case PgConstant.TYPE_BPCHAR:
                pgType = PgType.CHAR;
                break;
            case PgConstant.TYPE_BPCHAR_ARRAY:
                pgType = PgType.CHAR_ARRAY;
                break;
            default:
                pgType = CODE_TO_TYPE_MAP.getOrDefault(typeOid, PgType.UNSPECIFIED);
        }
        return pgType;
    }


    /**
     * @return a unmodified map.
     */
    private static Map<Integer, PgType> createCodeToTypeMap() {
        final PgType[] values = PgType.values();
        Map<Integer, PgType> map = new HashMap<>((int) (values.length / 0.75f));
        for (PgType type : PgType.values()) {
            if (map.containsKey(type.typeOid)) {
                throw new IllegalStateException(String.format("Type[%s] oid[%s] duplication.", type, type.typeOid));
            }
            map.put(type.typeOid, type);
        }
        return Collections.unmodifiableMap(map);
    }


}
