package io.jdbd.vendor.util;

import io.jdbd.JdbdException;
import io.jdbd.vendor.stmt.ParamValue;
import io.jdbd.vendor.stmt.Value;
import io.netty.buffer.ByteBuf;
import reactor.util.annotation.Nullable;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.time.*;
import java.time.temporal.TemporalAccessor;
import java.util.*;

public abstract class JdbdBinds {

    protected JdbdBinds() {
        throw new UnsupportedOperationException();
    }


//            case NULL:
//            case BOOLEAN:
//            case BIT:
//
//            case TINYINT:
//            case SMALLINT:
//            case MEDIUMINT:
//            case INTEGER:
//            case BIGINT:
//            case DECIMAL:
//            case NUMERIC:
//
//            case FLOAT:
//            case REAL:
//            case DOUBLE:
//
//            case TINYINT_UNSIGNED:
//            case SMALLINT_UNSIGNED:
//            case MEDIUMINT_UNSIGNED:
//            case INTEGER_UNSIGNED:
//            case BIGINT_UNSIGNED:
//            case DECIMAL_UNSIGNED:
//
//            case TIME:
//            case YEAR:
//            case YEAR_MONTH:
//            case MONTH_DAY:
//            case DATE:
//            case TIMESTAMP:
//            case TIME_WITH_TIMEZONE:
//            case TIMESTAMP_WITH_TIMEZONE:
//
//
//            case BINARY:
//            case VARBINARY:
//            case TINYBLOB:
//            case MEDIUMBLOB:
//            case BLOB:
//            case LONGBLOB:
//
//            case CHAR:
//            case VARCHAR:
//            case ENUM:
//            case TINYTEXT:
//            case MEDIUMTEXT:
//            case TEXT:
//            case LONGTEXT:
//
//            case JSON:
//            case JSONB:
//
//            case DURATION:
//            case PERIOD:
//            case INTERVAL:
//
//            case GEOMETRY:
//            case POINT:
//            case LINE_STRING:
//            case LINE:
//            case LINEAR_RING:
//            case MULTI_POINT:
//            case MULTI_POLYGON:
//            case MULTI_LINE_STRING:
//            case POLYGON:
//            case GEOMETRY_COLLECTION:
//
//            case REF:
//            case XML:
//            case ARRAY:
//            case ROWID:
//            case DATALINK:
//            case REF_CURSOR:
//            case DIALECT_TYPE:
//            case UNKNOWN:
//            default:


    @Deprecated
    public static void readFileAndWrite(final FileChannel channel, final ByteBuffer buffer, final ByteBuf packet,
                                        int restBytes, final Charset textCharset, final Charset clientCharset)
            throws IOException {

        final int capacityBytes = buffer.capacity();


        for (int len, position; restBytes > 0; restBytes -= len) {

            position = capacityBytes - Math.min(capacityBytes, restBytes);

            if (position > 0) {
                buffer.position(position);
            }

            len = channel.read(buffer);
            buffer.flip();

            if (position > 0) {
                buffer.position(position);
            }

            packet.writeBytes(clientCharset.encode(textCharset.decode(buffer)));
            buffer.clear();

        }


    }


    @Nullable
    public static JdbdException sortAndCheckParamGroup(final int groupIndex,
                                                       final List<? extends ParamValue> paramGroup) {

        paramGroup.sort(Comparator.comparingInt(ParamValue::getIndex));

        JdbdException error = null;
        final int size = paramGroup.size();
        for (int i = 0, index; i < size; i++) {
            index = paramGroup.get(i).getIndex();
            if (index == i) {
                continue;
            }

            if (index < i) {
                error = JdbdExceptions.duplicationParameter(groupIndex, index);
            } else {
                error = JdbdExceptions.noParameterValue(groupIndex, i);
            }
            break;
        }
        return error;
    }


    public static boolean bindToBoolean(final int batchIndex, final Value paramValue) {
        final Object nonNull = paramValue.get();
        final boolean value;
        if (nonNull instanceof Boolean) {
            value = (Boolean) nonNull;
        } else if (nonNull instanceof Integer
                || nonNull instanceof Short
                || nonNull instanceof Byte) {
            value = ((Number) nonNull).intValue() != 0;
        } else if (nonNull instanceof Long) {
            value = ((Number) nonNull).longValue() != 0;
        } else if (nonNull instanceof String) {
            final String v = (String) nonNull;
            switch (v.toUpperCase(Locale.ROOT)) {
                case "TRUE":
                case "T":
                case "ON":
                case "YES":
                    value = true;
                    break;
                case "FALSE":
                case "F":
                case "OFF":
                case "NO":
                    value = false;
                    break;
                default:
                    throw JdbdExceptions.outOfTypeRange(batchIndex, paramValue);
            }
        } else if (nonNull instanceof BigDecimal) {
            value = BigDecimal.ZERO.compareTo((BigDecimal) nonNull) != 0;
        } else if (nonNull instanceof BigInteger) {
            value = BigInteger.ZERO.compareTo((BigInteger) nonNull) != 0;
        } else if (nonNull instanceof Double
                || nonNull instanceof Float) {
            value = ((Number) nonNull).doubleValue() != 0.0;
        } else {
            throw JdbdExceptions.nonSupportBindSqlTypeError(batchIndex, paramValue);
        }
        return value;
    }

    public static String bindToBit(final int batchIndex, final Value paramValue, final int maxLength) {
        final Object value = paramValue.get();
        final String bitValue;

        if (value instanceof Long) {
            bitValue = Long.toBinaryString((Long) value);
        } else if (value instanceof Integer) {
            bitValue = Integer.toBinaryString((Integer) value);
        } else if (value instanceof Short) {
            bitValue = Integer.toBinaryString(((Short) value) & 0xFFFF);
        } else if (value instanceof Byte) {
            bitValue = Integer.toBinaryString(((Byte) value) & 0xFF);
        } else if (value instanceof BitSet) {
            final BitSet v = (BitSet) value;
            if (v.length() > maxLength) {
                throw JdbdExceptions.outOfTypeRange(batchIndex, paramValue);
            }
            bitValue = JdbdStrings.bitSetToBitString(v, true);
        } else if (value instanceof String) {
            final String v = (String) value;
            if (v.length() > maxLength || !JdbdStrings.isBinaryString(v)) {
                throw JdbdExceptions.outOfTypeRange(batchIndex, paramValue);
            }
            bitValue = v;
        } else {
            throw JdbdExceptions.nonSupportBindSqlTypeError(batchIndex, paramValue);
        }
        return bitValue;
    }

    public static int bindToIntUnsigned(final int batchIndex, final Value paramValue, final int maxvalue)
            throws JdbdException, IllegalArgumentException {
        final Object nonNull = paramValue.get();
        final int value;
        if (nonNull instanceof Integer) {
            value = (Integer) nonNull;
        } else if (nonNull instanceof Long) {
            final long v = (Long) nonNull;
            if ((v & (~0xFFFF_FFFFL)) != 0) {
                throw JdbdExceptions.outOfTypeRange(batchIndex, paramValue);
            }
            value = (int) v;
        } else if (nonNull instanceof String) {
            if (JdbdNumbers.isHexNumber((String) nonNull)) {
                value = Integer.decode((String) nonNull);
            } else {
                value = Integer.parseUnsignedInt((String) nonNull);
            }
        } else if (nonNull instanceof Short) {
            value = ((Short) nonNull) & 0xFFFF;
        } else if (nonNull instanceof Byte) {
            value = ((Byte) nonNull) & 0xFFFF;
        } else if (nonNull instanceof Boolean) {
            value = ((Boolean) nonNull ? 1 : 0);
        } else if (nonNull instanceof BigInteger) {
            final long v = ((BigInteger) nonNull).longValueExact();
            if ((v & (~0xFFFF_FFFFL)) != 0) {
                throw JdbdExceptions.outOfTypeRange(batchIndex, paramValue);
            }
            value = (int) v;
        } else if (nonNull instanceof BigDecimal) {
            final long v = ((BigDecimal) nonNull).longValueExact();
            if ((v & (~0xFFFF_FFFFL)) != 0) {
                throw JdbdExceptions.outOfTypeRange(batchIndex, paramValue);
            }
            value = (int) v;
        } else {
            throw JdbdExceptions.nonSupportBindSqlTypeError(batchIndex, paramValue);
        }

        if (maxvalue != -1 && (value & (~maxvalue)) != 0) {
            throw JdbdExceptions.outOfTypeRange(batchIndex, paramValue);
        }
        return value;
    }

    public static int bindToInt(final int batchIndex, final Value paramValue, final int minValue, int maxValue)
            throws JdbdException {
        final Object nonNull = paramValue.get();
        final int value;
        if (nonNull instanceof Integer
                || nonNull instanceof Short
                || nonNull instanceof Byte) {
            value = ((Number) nonNull).intValue();
        } else if (nonNull instanceof String) {
            value = Integer.parseInt((String) nonNull);
        } else if (nonNull instanceof Long) {
            final long v = (Long) nonNull;
            if (v < minValue || v > maxValue) {
                throw JdbdExceptions.outOfTypeRange(batchIndex, paramValue);
            }
            value = (int) v;
        } else if (nonNull instanceof Boolean) {
            value = ((Boolean) nonNull ? 1 : 0);
        } else if (nonNull instanceof BigInteger) {
            value = ((BigInteger) nonNull).intValueExact();
        } else if (nonNull instanceof BigDecimal) {
            value = ((BigDecimal) nonNull).intValueExact();
        } else {
            throw JdbdExceptions.nonSupportBindSqlTypeError(batchIndex, paramValue);
        }

        if (value < minValue || value > maxValue) {
            throw JdbdExceptions.outOfTypeRange(batchIndex, paramValue);
        }
        return value;
    }


    public static long bindToLong(final int batchIndex, final Value paramValue, final long minValue, final long maxValue)
            throws JdbdException {
        final Object nonNull = paramValue.get();
        final long value;
        if (nonNull instanceof Long
                || nonNull instanceof Integer
                || nonNull instanceof Short
                || nonNull instanceof Byte) {
            value = ((Number) nonNull).longValue();
        } else if (nonNull instanceof String) {
            value = Long.parseLong((String) nonNull);
        } else if (nonNull instanceof Boolean) {
            final boolean v = (Boolean) nonNull;
            value = (v ? 1 : 0);
        } else if (nonNull instanceof BigInteger) {
            value = ((BigInteger) nonNull).longValueExact();
        } else if (nonNull instanceof BigDecimal) {
            value = ((BigDecimal) nonNull).longValueExact();
        } else {
            throw JdbdExceptions.nonSupportBindSqlTypeError(batchIndex, paramValue);
        }

        if (value < minValue || value > maxValue) {
            throw JdbdExceptions.outOfTypeRange(batchIndex, paramValue);
        }
        return value;
    }

    public static long bindToLongUnsigned(final int batchIndex, final Value paramValue, final long maxValue)
            throws JdbdException, IllegalArgumentException {
        final Object nonNull = paramValue.get();
        final long value;
        if (nonNull instanceof Integer || nonNull instanceof Long) {
            value = ((Number) nonNull).longValue();
        } else if (nonNull instanceof String) {
            if (JdbdNumbers.isHexNumber((String) nonNull)) {
                value = Long.decode((String) nonNull);
            } else {
                value = Long.parseUnsignedLong((String) nonNull);
            }
        } else if (nonNull instanceof Short) {
            value = ((Short) nonNull) & 0xFFFFL;
        } else if (nonNull instanceof Byte) {
            value = ((Byte) nonNull) & 0xFFFFL;
        } else if (nonNull instanceof Boolean) {
            value = ((Boolean) nonNull ? 1 : 0);
        } else if (nonNull instanceof BigInteger) {
            value = Long.parseUnsignedLong(nonNull.toString());
        } else if (nonNull instanceof BigDecimal) {
            value = Long.parseUnsignedLong(((BigDecimal) nonNull).stripTrailingZeros().toPlainString());
        } else {
            throw JdbdExceptions.nonSupportBindSqlTypeError(batchIndex, paramValue);
        }

        if (maxValue != -1L && (value & (~maxValue)) != 0) {
            throw JdbdExceptions.outOfTypeRange(batchIndex, paramValue);
        }
        return value;
    }

    public static BigInteger bindToBigInteger(final int batchIndex, final Value paramValue) {
        final Object nonNull = paramValue.get();

        final BigInteger value;
        if (nonNull instanceof BigInteger) {
            value = (BigInteger) nonNull;
        } else if (nonNull instanceof String) {
            value = new BigInteger((String) nonNull);
        } else if (nonNull instanceof Long
                || nonNull instanceof Integer
                || nonNull instanceof Short
                || nonNull instanceof Byte) {
            value = BigInteger.valueOf(((Number) nonNull).longValue());
        } else if (nonNull instanceof Boolean) {
            final boolean v = (Boolean) nonNull;
            value = (v ? BigInteger.ONE : BigInteger.ZERO);
        } else if (nonNull instanceof BigDecimal) {
            value = ((BigDecimal) nonNull).toBigIntegerExact();
        } else {
            throw JdbdExceptions.nonSupportBindSqlTypeError(batchIndex, paramValue);
        }
        return value;
    }

    public static BigDecimal bindToDecimal(final int batchIndex, final Value paramValue) {
        final Object nonNull = paramValue.get();

        final BigDecimal value;
        if (nonNull instanceof BigDecimal) {
            value = (BigDecimal) nonNull;
        } else if (nonNull instanceof String) {
            value = new BigDecimal((String) nonNull);
        } else if (nonNull instanceof Long
                || nonNull instanceof Integer
                || nonNull instanceof Short
                || nonNull instanceof Byte) {
            value = BigDecimal.valueOf(((Number) nonNull).longValue());
        } else if (nonNull instanceof Boolean) {
            value = ((Boolean) nonNull ? BigDecimal.ONE : BigDecimal.ZERO);
        } else if (nonNull instanceof BigInteger) {
            value = new BigDecimal((BigInteger) nonNull);
        } else if (nonNull instanceof Double || nonNull instanceof Float) {
            value = new BigDecimal(nonNull.toString());
        } else {
            throw JdbdExceptions.nonSupportBindSqlTypeError(batchIndex, paramValue);
        }
        return value;
    }

    public static float bindToFloat(final int batchIndex, final Value paramValue) {
        final Object nonNull = paramValue.get();
        final float value;
        if (nonNull instanceof Float) {
            value = (Float) nonNull;
        } else if (nonNull instanceof Short || nonNull instanceof Byte) {
            value = ((Number) nonNull).floatValue();
        } else if (nonNull instanceof String) {
            value = Float.parseFloat((String) nonNull);
        } else if (nonNull instanceof Boolean) {
            value = ((Boolean) nonNull) ? 1.0F : 0.0F;
        } else {
            throw JdbdExceptions.nonSupportBindSqlTypeError(batchIndex, paramValue);
        }
        return value;
    }


    public static double bindToDouble(final int batchIndex, final Value paramValue) {
        final Object nonNull = paramValue.get();
        final double value;
        if (nonNull instanceof Float
                || nonNull instanceof Double
                || nonNull instanceof Integer
                || nonNull instanceof Short
                || nonNull instanceof Byte) {
            value = ((Number) nonNull).doubleValue();
        } else if (nonNull instanceof String) {
            value = Double.parseDouble((String) nonNull);
        } else if (nonNull instanceof Boolean) {
            value = ((Boolean) nonNull) ? 1.0D : 0.0D;
        } else {
            throw JdbdExceptions.nonSupportBindSqlTypeError(batchIndex, paramValue);
        }
        return value;
    }

    public static String bindToString(final int batchIndex, final Value paramValue) {
        final Object nonNull = paramValue.get();
        final String value;

        if (nonNull instanceof String) {
            value = (String) nonNull;
        } else if (nonNull instanceof Enum) {
            value = ((Enum<?>) nonNull).name();
        } else if (nonNull instanceof BigDecimal) {
            value = ((BigDecimal) nonNull).toPlainString();
        } else if (nonNull instanceof Number
                || nonNull instanceof Boolean
                || nonNull instanceof LocalDate
                || nonNull instanceof UUID
                || nonNull instanceof YearMonth
                || nonNull instanceof MonthDay) {
            value = nonNull.toString();
        } else if (nonNull instanceof LocalDateTime) {
            value = ((LocalDateTime) nonNull).format(JdbdTimes.DATETIME_FORMATTER_6);
        } else if (nonNull instanceof OffsetDateTime || nonNull instanceof ZonedDateTime) {
            value = JdbdTimes.OFFSET_DATETIME_FORMATTER_6.format((TemporalAccessor) nonNull);
        } else if (nonNull instanceof LocalTime) {
            value = JdbdTimes.TIME_FORMATTER_6.format((LocalTime) nonNull);
        } else if (nonNull instanceof OffsetTime) {
            value = JdbdTimes.OFFSET_TIME_FORMATTER_6.format((OffsetTime) nonNull);
        } else if (nonNull instanceof BitSet) {
            value = JdbdStrings.bitSetToBitString((BitSet) nonNull, true);
        } else {
            throw JdbdExceptions.nonSupportBindSqlTypeError(batchIndex, paramValue);
        }
        return value;
    }

    /**
     * @return {@link Number} or {@link String}
     */
    public static Object bindToJson(final int batchIndex, final Value paramValue) {
        final Object nonNull = paramValue.get();
        final Object value;

        if (nonNull instanceof String || nonNull instanceof Number) {
            value = nonNull;
        } else {
            value = bindToString(batchIndex, paramValue);
        }
        return value;
    }


    public static LocalDate bindToLocalDate(final int batchIndex, final Value paramValue) {
        final Object source = paramValue.get();
        final LocalDate value;
        if (source instanceof LocalDate) {
            value = (LocalDate) source;
        } else if (source instanceof String) {
            value = LocalDate.parse((String) source);
        } else if (source instanceof YearMonth) {
            final YearMonth v = (YearMonth) source;
            value = LocalDate.of(v.getYear(), v.getMonthValue(), 1);
        } else if (source instanceof MonthDay) {
            final MonthDay v = (MonthDay) source;
            value = LocalDate.of(1970, v.getMonthValue(), v.getDayOfMonth());
        } else if (source instanceof Year) {
            value = LocalDate.of(((Year) source).getValue(), 1, 1);
        } else {
            throw JdbdExceptions.nonSupportBindSqlTypeError(batchIndex, paramValue);
        }
        return value;
    }

    public static LocalTime bindToLocalTime(final int batchIndex, final Value paramValue) {
        final Object nonNull = paramValue.get();
        final LocalTime value;
        if (nonNull instanceof LocalTime) {
            value = (LocalTime) nonNull;
        } else if (nonNull instanceof String) {
            value = LocalTime.parse((String) nonNull, JdbdTimes.TIME_FORMATTER_6);
        } else {
            throw JdbdExceptions.nonSupportBindSqlTypeError(batchIndex, paramValue);
        }
        return value;
    }

    public static LocalDateTime bindToLocalDateTime(final int batchIndex, final Value paramValue) {
        final Object nonNull = paramValue.get();
        final LocalDateTime value;
        if (nonNull instanceof LocalDateTime) {
            value = (LocalDateTime) nonNull;
        } else if (nonNull instanceof String) {
            value = LocalDateTime.parse((String) nonNull, JdbdTimes.DATETIME_FORMATTER_6);
        } else {
            throw JdbdExceptions.nonSupportBindSqlTypeError(batchIndex, paramValue);
        }
        return value;
    }

    public static OffsetTime bindToOffsetTime(final int batchIndex, final Value paramValue) {
        final Object nonNull = paramValue.get();
        final OffsetTime value;
        if (nonNull instanceof OffsetTime) {
            value = (OffsetTime) nonNull;
        } else if (nonNull instanceof String) {
            value = OffsetTime.parse((String) nonNull, JdbdTimes.OFFSET_TIME_FORMATTER_6);
        } else {
            throw JdbdExceptions.nonSupportBindSqlTypeError(batchIndex, paramValue);
        }
        return value;
    }

    public static OffsetDateTime bindToOffsetDateTime(final int batchIndex, final Value paramValue) {
        final Object nonNull = paramValue.get();
        final OffsetDateTime value;
        if (nonNull instanceof OffsetDateTime) {
            value = (OffsetDateTime) nonNull;
        } else if (nonNull instanceof ZonedDateTime) {
            value = ((ZonedDateTime) nonNull).toOffsetDateTime();
        } else if (nonNull instanceof String) {
            value = OffsetDateTime.parse((String) nonNull, JdbdTimes.OFFSET_DATETIME_FORMATTER_6);
        } else {
            throw JdbdExceptions.nonSupportBindSqlTypeError(batchIndex, paramValue);
        }
        return value;
    }





    /*################################## blow private method ##################################*/


}
