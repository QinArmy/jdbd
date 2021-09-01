package io.jdbd.postgre;


import io.jdbd.type.LongBinary;
import io.jdbd.type.geometry.Circle;
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
    UUID(PgConstant.TYPE_UUID, JDBCType.CHAR, UUID.class),
    XML(PgConstant.TYPE_XML, JDBCType.SQLXML, LongString.class),
    POINT(PgConstant.TYPE_POINT, JDBCType.OTHER, Point.class),
    BOX(PgConstant.TYPE_BOX, JDBCType.OTHER, String.class),
    LINE(PgConstant.TYPE_LINE, JDBCType.OTHER, String.class),
    LINE_SEGMENT(PgConstant.TYPE_LSEG, JDBCType.OTHER, String.class),
    PATH(PgConstant.TYPE_PATH, JDBCType.OTHER, LongString.class),
    POLYGON(PgConstant.TYPE_POLYGON, JDBCType.OTHER, LongString.class),
    CIRCLE(PgConstant.TYPE_CIRCLE, JDBCType.OTHER, Circle.class),
    JSON(PgConstant.TYPE_JSON, JDBCType.LONGVARCHAR, LongString.class),
    JSONB(PgConstant.TYPE_JSONB, JDBCType.LONGVARCHAR, LongString.class),
    MACADDR(PgConstant.TYPE_MAC_ADDR, JDBCType.VARCHAR, String.class),
    MACADDR8(PgConstant.TYPE_MAC_ADDR8, JDBCType.VARCHAR, String.class),
    INET(PgConstant.TYPE_INET, JDBCType.VARCHAR, String.class),
    CIDR(PgConstant.TYPE_CIDR, JDBCType.VARCHAR, String.class),

    INT4RANGE(PgConstant.TYPE_INT4RANGE, JDBCType.VARCHAR, String.class),
    INT8RANGE(PgConstant.TYPE_INT8RANGE, JDBCType.VARCHAR, String.class),
    NUMRANGE(PgConstant.TYPE_NUMRANGE, JDBCType.VARCHAR, String.class),
    TSRANGE(PgConstant.TYPE_TSRANGE, JDBCType.VARCHAR, String.class),
    TSTZRANGE(PgConstant.TYPE_TSTZRANGE, JDBCType.VARCHAR, String.class),
    DATERANGE(PgConstant.TYPE_DATERANGE, JDBCType.VARCHAR, String.class),


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
    PATH_ARRAY(PgConstant.TYPE_PATH_ARRAY, JDBCType.ARRAY, Object.class),
    LINE_ARRAY(PgConstant.TYPE_LINE_ARRAY, JDBCType.ARRAY, Object.class),
    LINE_SEGMENT_ARRAY(PgConstant.TYPE_LINE_LSEG_ARRAY, JDBCType.ARRAY, Object.class),
    POLYGON_ARRAY(PgConstant.TYPE_POLYGON_ARRAY, JDBCType.ARRAY, Object.class),
    CIRCLES_ARRAY(PgConstant.TYPE_CIRCLES_ARRAY, JDBCType.ARRAY, Object.class),
    CIDR_ARRAY(PgConstant.TYPE_CIDR_ARRAY, JDBCType.ARRAY, Object.class),
    INET_ARRAY(PgConstant.TYPE_INET_ARRAY, JDBCType.ARRAY, Object.class),
    MACADDR_ARRAY(PgConstant.TYPE_MACADDR_ARRAY, JDBCType.ARRAY, Object.class),
    MACADDR8_ARRAY(PgConstant.TYPE_MACADDR8_ARRAY, JDBCType.ARRAY, Object.class),

    INT4RANGE_ARRAY(PgConstant.TYPE_INT4RANGE_ARRAY, JDBCType.ARRAY, Object.class),
    TSRANGE_ARRAY(PgConstant.TYPE_TSRANGE_ARRAY, JDBCType.ARRAY, Object.class),
    TSTZRANGE_ARRAY(PgConstant.TYPE_TSTZRANGE_ARRAY, JDBCType.ARRAY, Object.class),
    DATERANGE_ARRAY(PgConstant.TYPE_DATERANGE_ARRAY, JDBCType.ARRAY, Object.class),
    INT8RANGE_ARRAY(PgConstant.TYPE_INT8RANGE_ARRAY, JDBCType.ARRAY, Object.class),
    NUMRANGE_ARRAY(PgConstant.TYPE_NUMRANGE_ARRAY, JDBCType.ARRAY, Object.class),

    REF_CURSOR_ARRAY(PgConstant.TYPE_REF_CURSOR_ARRAY, JDBCType.ARRAY, Object.class);

    private static final Map<Short, PgType> CODE_TO_TYPE_MAP = createCodeToTypeMap();

    private final short typeOid;

    private final JDBCType jdbcType;

    private final Class<?> javaType;

    PgType(short typeOid, JDBCType jdbcType, Class<?> javaType) {
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
    public final boolean isCaseSensitive() {
        final boolean sensitive;
        switch (this) {
            case OID:
            case SMALLINT:
            case SMALLINT_ARRAY:
            case INTEGER:
            case INTEGER_ARRAY:
            case BIGINT:
            case BIGINT_ARRAY:
            case REAL:
            case REAL_ARRAY:
            case DOUBLE:
            case DOUBLE_ARRAY:
            case DECIMAL:
            case DECIMAL_ARRAY:
            case BOOLEAN:
            case BOOLEAN_ARRAY:
            case BIT:
            case BIT_ARRAY:
            case VARBIT:
            case VARBIT_ARRAY:
            case TIMESTAMP:
            case TIMESTAMP_ARRAY:
            case TIME:
            case TIME_ARRAY:
            case DATE:
            case DATE_ARRAY:
            case TIMESTAMPTZ:
            case TIMESTAMPTZ_ARRAY:
            case TIMETZ:
            case TIMETZ_ARRAY:
            case INTERVAL:
            case INTERVAL_ARRAY:
            case POINT:
            case POINT_ARRAY:
            case BOX:
            case BOX_ARRAY:
            case LINE:
            case LINE_ARRAY:
            case LINE_SEGMENT:
            case LINE_SEGMENT_ARRAY:
            case PATH:
            case PATH_ARRAY:
            case POLYGON:
            case POLYGON_ARRAY:
            case CIRCLE:
            case CIRCLES_ARRAY:
            case UUID:
            case UUID_ARRAY:
            case CIDR:
            case CIDR_ARRAY:
            case INET:
            case INET_ARRAY:
            case INT4RANGE:
            case INT4RANGE_ARRAY:
            case INT8RANGE:
            case INT8RANGE_ARRAY:
            case DATERANGE:
            case DATERANGE_ARRAY:
            case NUMRANGE:
            case NUMRANGE_ARRAY:
            case MACADDR:
            case MACADDR_ARRAY:
            case MACADDR8:
            case MACADDR8_ARRAY:
            case TSRANGE:
            case TSRANGE_ARRAY:
            case TSTZRANGE:
            case TSTZRANGE_ARRAY:
                sensitive = false;
                break;
            default:
                sensitive = true;
        }
        return sensitive;
    }

    @Override
    public final String getName() {
        final String name;
        if (this.jdbcType == JDBCType.ARRAY) {
            name = "[L" + toActualTypeName();
        } else {
            name = getNonArrayTypeName();
        }
        return name;
    }


    @Override
    public final String getVendor() {
        return PgType.class.getPackage().getName();
    }

    @Override
    public final Integer getVendorTypeNumber() {
        return (int) this.typeOid;
    }

    public final int getTypeOid() {
        return this.typeOid;
    }


    /**
     * @see #getName()
     */
    private String getNonArrayTypeName() {
        final String name;
        switch (this) {
            case TIMESTAMPTZ:
                name = "TIMESTAMP WITH TIME ZONE";
                break;
            case TIMETZ:
                name = "TIME WITH TIME ZONE";
                break;
            default:
                name = toActualTypeName();
        }
        return name;
    }

    /**
     * @see #getNonArrayTypeName()
     */
    private String toActualTypeName() {
        final String name = this.name();
        final char[] array = name.toCharArray();
        boolean replace = false;
        for (int i = 0; i < array.length; i++) {
            if (array[i] == '_') {
                array[i] = ' ';
                replace = true;
            }
        }
        return replace ? new String(array) : name;
    }


    public static PgType from(final int oid) {
        if (oid > Short.MAX_VALUE) {
            return PgType.UNSPECIFIED;
        }
        final short typeOid = (short) oid;
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
    private static Map<Short, PgType> createCodeToTypeMap() {
        final PgType[] values = PgType.values();
        Map<Short, PgType> map = new HashMap<>((int) (values.length / 0.75f));
        for (PgType type : PgType.values()) {
            if (map.containsKey(type.typeOid)) {
                throw new IllegalStateException(String.format("Type[%s] oid[%s] duplication.", type, type.typeOid));
            }
            map.put(type.typeOid, type);
        }
        return Collections.unmodifiableMap(map);
    }


}
