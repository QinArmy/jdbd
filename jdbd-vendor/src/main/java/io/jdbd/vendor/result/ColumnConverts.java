package io.jdbd.vendor.result;

import io.jdbd.JdbdException;
import io.jdbd.lang.Nullable;
import io.jdbd.meta.BooleanMode;
import io.jdbd.type.BlobPath;
import io.jdbd.type.Point;
import io.jdbd.type.TextPath;
import io.jdbd.vendor.util.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Files;
import java.time.*;
import java.time.temporal.TemporalAccessor;
import java.util.BitSet;
import java.util.Locale;

public abstract class ColumnConverts {

    private ColumnConverts() {
        throw new UnsupportedOperationException();
    }


    @SuppressWarnings("unchecked")
    public static <T> T convertToTarget(final ColumnMeta meta, final Object source, final Class<T> targetClass,
                                        final @Nullable ZoneOffset serverZone) {
        final Object value;
        if (targetClass == String.class) {
            value = convertToString(meta, source);
        } else if (targetClass == byte[].class) {
            value = convertToByteArray(meta, source);
        } else if (targetClass == Boolean.class) {
            value = convertToBoolean(meta, source);
        } else if (Number.class.isAssignableFrom(targetClass)) {
            if (targetClass == Integer.class) {
                value = convertToInt(meta, source);
            } else if (targetClass == Long.class) {
                value = convertToLong(meta, source);
            } else if (targetClass == BigDecimal.class) {
                value = convertToBigDecimal(meta, source);
            } else if (targetClass == BigInteger.class) {
                value = convertToBigInteger(meta, source);
            } else if (targetClass == Double.class) {
                value = convertToDouble(meta, source);
            } else if (targetClass == Float.class) {
                value = convertToFloat(meta, source);
            } else if (targetClass == Short.class) {
                value = convertToShort(meta, source);
            } else if (targetClass == Byte.class) {
                value = convertToByte(meta, source);
            } else {
                throw JdbdExceptions.cannotConvertColumnValue(meta, source, targetClass, null);
            }
        } else if (TemporalAccessor.class.isAssignableFrom(targetClass)) {
            if (targetClass == LocalDateTime.class) {
                value = convertToLocalDateTime(meta, source);
            } else if (targetClass == LocalDate.class) {
                value = convertToLocalDate(meta, source);
            } else if (targetClass == OffsetDateTime.class) {
                value = convertToOffsetDateTime(meta, source, serverZone);
            } else if (targetClass == ZonedDateTime.class) {
                value = convertToZonedDateTime(meta, source, serverZone);
            } else if (targetClass == LocalTime.class) {
                value = convertToLocalTime(meta, source);
            } else if (targetClass == OffsetTime.class) {
                value = convertToOffsetTime(meta, source, serverZone);
            } else if (targetClass == YearMonth.class) {
                value = convertToYearMonth(meta, source);
            } else if (targetClass == MonthDay.class) {
                value = convertToMonthDay(meta, source);
            } else if (targetClass == Month.class) {
                value = convertToMonth(meta, source);
            } else if (targetClass == DayOfWeek.class) {
                value = convertToDayOfWeek(meta, source);
            } else {
                throw JdbdExceptions.cannotConvertColumnValue(meta, source, targetClass, null);
            }
        } else if (Enum.class.isAssignableFrom(targetClass)) {
            if (targetClass == BooleanMode.class) {
                value = convertToBoolean(meta, source) ? BooleanMode.TRUE : BooleanMode.FALSE;
            } else if (!(source instanceof String)) {
                throw JdbdExceptions.cannotConvertColumnValue(meta, source, targetClass, null);
            } else if (targetClass.isAnonymousClass()) {
                value = convertToEnum(targetClass.getSuperclass(), (String) source);
            } else {
                value = convertToEnum(targetClass, (String) source);
            }
        } else if (targetClass == BitSet.class) {
            value = convertToBitSet(meta, source);
        } else if (targetClass == Point.class) {
            value = convertToPoint(meta, source);
        } else {
            throw JdbdExceptions.cannotConvertColumnValue(meta, source, targetClass, null);
        }
        return (T) value;
    }


    @SuppressWarnings("unchecked")
    public static <T extends Enum<T>> T convertToEnum(Class<?> enumClass, String source) {
        return Enum.valueOf((Class<T>) enumClass, source);
    }


    public static boolean convertToBoolean(final ColumnMeta meta, final Object source) throws JdbdException {
        final boolean value;

        if (source instanceof Boolean) {
            value = (Boolean) source;
        } else if (source instanceof Integer
                || source instanceof Short
                || source instanceof Byte) {
            value = ((Number) source).intValue() != 0;
        } else if (source instanceof Long) {
            value = ((Number) source).longValue() != 0;
        } else if (source instanceof String) {
            switch (((String) source).toUpperCase(Locale.ROOT)) {
                case "TRUE":
                case "T":
                case "YES":
                case "ON":
                case "1":
                    value = true;
                    break;
                case "FALSE":
                case "F":
                case "NO":
                case "OFF":
                case "0":
                    value = false;
                    break;
                default:
                    throw JdbdExceptions.cannotConvertColumnValue(meta, source, Boolean.class, null);

            }
        } else if (source instanceof BigDecimal) {
            value = BigDecimal.ZERO.compareTo((BigDecimal) source) != 0;
        } else if (source instanceof BigInteger) {
            value = BigInteger.ZERO.compareTo((BigInteger) source) != 0;
        } else if (source instanceof Double
                || source instanceof Float) {
            value = ((Number) source).doubleValue() != 0.0;
        } else {
            throw JdbdExceptions.cannotConvertColumnValue(meta, source, Boolean.class, null);
        }
        return value;
    }

    /**
     * @throws NumberFormatException throw when source is error number {@link String}
     * @throws ArithmeticException   throw when source overflow to target
     * @throws JdbdException         throw when source couldn't convert or overflow.
     */
    public static byte convertToByte(final ColumnMeta meta, final Object source) throws JdbdException {
        final int value;
        if (meta.isUnsigned() || meta.isBit()) {
            value = convertToIntUnsignedValue(meta, source, Byte.class, 0xFF);
        } else {
            value = convertToIntValue(meta, source, Byte.class, Byte.MIN_VALUE, Byte.MAX_VALUE);
        }
        return (byte) value;
    }

    /**
     * @throws NumberFormatException throw when source is error number {@link String}
     * @throws ArithmeticException   throw when source overflow to target
     * @throws JdbdException         throw when source couldn't convert or overflow.
     */
    public static short convertToShort(final ColumnMeta meta, final Object source) throws JdbdException {
        final int value;
        if (meta.isUnsigned() || meta.isBit()) {
            value = convertToIntUnsignedValue(meta, source, Short.class, 0xFFFF);
        } else {
            value = convertToIntValue(meta, source, Short.class, Short.MIN_VALUE, Short.MAX_VALUE);
        }
        return (short) value;
    }

    /**
     * @throws NumberFormatException throw when source is error number {@link String}
     * @throws ArithmeticException   throw when source overflow to target
     * @throws JdbdException         throw when source couldn't convert or overflow.
     */
    public static int convertToInt(final ColumnMeta meta, final Object source) throws JdbdException {
        final int value;
        if (meta.isUnsigned() || meta.isBit()) {
            value = convertToIntUnsignedValue(meta, source, Integer.class, -1);
        } else {
            value = convertToIntValue(meta, source, Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE);
        }
        return value;
    }

    /**
     * @throws NumberFormatException throw when source is error number {@link String}
     * @throws ArithmeticException   throw when source overflow to target
     * @throws JdbdException         throw when source couldn't convert or overflow.
     */
    public static long convertToLong(final ColumnMeta meta, final Object source) throws JdbdException {
        final long value;
        if (meta.isUnsigned() || meta.isBit()) {
            value = convertToLongUnsignedValue(meta, source, Long.class, -1L);
        } else {
            value = convertToLongValue(meta, source, Long.class, Long.MIN_VALUE, Long.MAX_VALUE);
        }
        return value;
    }

    public static float convertToFloat(final ColumnMeta meta, final Object source) throws JdbdException {
        final float value;
        if (source instanceof Float) {
            value = (Float) source;
        } else if (source instanceof Short || source instanceof Byte) {
            value = ((Number) source).floatValue();
        } else if (source instanceof String) {
            value = Float.parseFloat((String) source);
        } else if (source instanceof Boolean) {
            value = ((Boolean) source) ? 1.0F : 0.0F;
        } else {
            throw JdbdExceptions.cannotConvertColumnValue(meta, source, Float.class, null);
        }
        if (value < 0.0f && meta.isUnsigned()) {
            throw JdbdExceptions.columnValueOverflow(meta, source, Float.class, null);
        }
        return value;
    }

    public static double convertToDouble(final ColumnMeta meta, final Object source) throws JdbdException {
        final double value;
        if (source instanceof Double) {
            value = (Double) source;
        } else if (source instanceof Integer
                || source instanceof Short
                || source instanceof Byte
                || source instanceof Float) {
            value = ((Number) source).doubleValue();
        } else if (source instanceof String) {
            value = Double.parseDouble((String) source);
        } else if (source instanceof Boolean) {
            value = ((Boolean) source) ? 1.0 : 0.0;
        } else {
            throw JdbdExceptions.cannotConvertColumnValue(meta, source, Double.class, null);
        }
        if (value < 0.0 && meta.isUnsigned()) {
            throw JdbdExceptions.columnValueOverflow(meta, source, Double.class, null);
        }
        return value;
    }

    /**
     * @throws NumberFormatException throw when source is error number {@link String}
     * @throws ArithmeticException   throw when source overflow to target
     * @throws JdbdException         throw when source couldn't convert or overflow.
     */
    public static BigInteger convertToBigInteger(final ColumnMeta meta, final Object source) throws JdbdException {
        final BigInteger value;

        if (source instanceof BigInteger) {
            value = (BigInteger) source;
        } else if (source instanceof String) {
            value = new BigInteger((String) source);
        } else if (source instanceof Long
                || source instanceof Integer
                || source instanceof Short
                || source instanceof Byte) {
            value = BigInteger.valueOf(((Number) source).longValue());
        } else if (source instanceof Boolean) {
            final boolean v = (Boolean) source;
            value = (v ? BigInteger.ONE : BigInteger.ZERO);
        } else if (source instanceof BigDecimal) {
            value = ((BigDecimal) source).toBigIntegerExact();
        } else {
            throw JdbdExceptions.cannotConvertColumnValue(meta, source, BigInteger.class, null);
        }

        if (meta.isUnsigned() && value.compareTo(BigInteger.ZERO) < 0) {
            throw JdbdExceptions.columnValueOverflow(meta, source, BigInteger.class, null);
        }
        return value;
    }

    /**
     * @throws NumberFormatException throw when source is error number {@link String}
     * @throws ArithmeticException   throw when source overflow to target
     * @throws JdbdException         throw when source couldn't convert or overflow.
     */
    public static BigDecimal convertToBigDecimal(final ColumnMeta meta, final Object source) throws JdbdException {
        final BigDecimal value;

        if (source instanceof BigDecimal) {
            value = (BigDecimal) source;
        } else if (source instanceof String) {
            value = new BigDecimal((String) source);
        } else if (source instanceof Long
                || source instanceof Integer
                || source instanceof Short
                || source instanceof Byte) {
            value = BigDecimal.valueOf(((Number) source).longValue());
        } else if (source instanceof Boolean) {
            final boolean v = (Boolean) source;
            value = (v ? BigDecimal.ONE : BigDecimal.ZERO);
        } else if (source instanceof Double || source instanceof Float) {
            value = new BigDecimal(source.toString());
        } else if (source instanceof BigInteger) {
            value = new BigDecimal((BigInteger) source);
        } else {
            throw JdbdExceptions.cannotConvertColumnValue(meta, source, BigDecimal.class, null);
        }

        if (meta.isUnsigned() && value.compareTo(BigDecimal.ZERO) < 0) {
            throw JdbdExceptions.columnValueOverflow(meta, source, BigDecimal.class, null);
        }
        return value;
    }


    /**
     * @throws NumberFormatException throw when source is error number {@link String}
     * @throws ArithmeticException   throw when source overflow to target
     * @throws JdbdException         throw when source couldn't convert or overflow.
     */
    public static String convertToString(final ColumnMeta meta, final Object source) throws JdbdException {
        final String value;
        if (source instanceof String) {
            value = (String) source;
        } else if (source instanceof BigDecimal) {
            value = ((BigDecimal) source).toPlainString();
        } else if (source instanceof Number) {
            if (!meta.isBit()) {
                value = source.toString();
            } else if (source instanceof Integer) {
                value = Integer.toBinaryString((Integer) source);
            } else if (source instanceof Long) {
                value = Long.toBinaryString((Long) source);
            } else if (source instanceof BigInteger) {
                value = ((BigInteger) source).toString(2);
            } else if (source instanceof Short) {
                value = Integer.toBinaryString(((Short) source) & 0xFF_FF);
            } else if (source instanceof Byte) {
                value = Integer.toBinaryString(((Byte) source) & 0xFF);
            } else {
                throw JdbdExceptions.cannotConvertColumnValue(meta, source, String.class, null);
            }
        } else if (source instanceof TemporalAccessor) {
            if (source instanceof LocalDateTime) {
                value = JdbdTimes.DATETIME_FORMATTER_6.format((LocalDateTime) source);
            } else if (source instanceof OffsetDateTime || source instanceof ZonedDateTime) {
                value = JdbdTimes.OFFSET_DATETIME_FORMATTER_6.format((TemporalAccessor) source);
            } else if (source instanceof LocalDate
                    || source instanceof YearMonth
                    || source instanceof MonthDay) {
                value = source.toString();
            } else if (source instanceof LocalTime) {
                value = JdbdTimes.TIME_FORMATTER_6.format((LocalTime) source);
            } else if (source instanceof OffsetTime) {
                value = JdbdTimes.OFFSET_TIME_FORMATTER_6.format((OffsetTime) source);
            } else {
                throw JdbdExceptions.cannotConvertColumnValue(meta, source, String.class, null);
            }
        } else if (source instanceof byte[]) {
            value = JdbdBuffers.hexEscapesText(true, (byte[]) source);
        } else if (source instanceof BitSet) {
            value = JdbdStrings.bitSetToBitString((BitSet) source, true);
        } else if (source instanceof TextPath) {
            try {
                if (Files.size(((TextPath) source).value()) > (Integer.MAX_VALUE - 128)) {
                    throw JdbdExceptions.cannotConvertColumnValue(meta, source, String.class, null);
                }
                final byte[] bytes;
                bytes = Files.readAllBytes(((TextPath) source).value());
                value = new String(bytes, ((TextPath) source).charset());
            } catch (Throwable e) {
                throw JdbdExceptions.cannotConvertColumnValue(meta, source, String.class, e);
            }
        } else if (source instanceof BlobPath) {
            try {
                if (Files.size(((BlobPath) source).value()) > (Integer.MAX_VALUE - 128)) {
                    throw JdbdExceptions.cannotConvertColumnValue(meta, source, String.class, null);
                }
                final byte[] bytes;
                bytes = Files.readAllBytes(((BlobPath) source).value());
                value = JdbdBuffers.hexEscapesText(true, bytes);
            } catch (Throwable e) {
                throw JdbdExceptions.cannotConvertColumnValue(meta, source, String.class, e);
            }
        } else {
            throw JdbdExceptions.cannotConvertColumnValue(meta, source, String.class, null);
        }
        return value;
    }

    public static byte[] convertToByteArray(final ColumnMeta meta, final Object source) {
        final byte[] value;
        if (source instanceof byte[]) {
            value = (byte[]) source;
        } else if (source instanceof BlobPath) {
            try {
                if (Files.size(((BlobPath) source).value()) > (Integer.MAX_VALUE - 128)) {
                    throw JdbdExceptions.cannotConvertColumnValue(meta, source, byte[].class, null);
                }
                value = Files.readAllBytes(((BlobPath) source).value());
            } catch (Throwable e) {
                throw JdbdExceptions.cannotConvertColumnValue(meta, source, byte[].class, e);
            }
        } else {
            throw JdbdExceptions.cannotConvertColumnValue(meta, source, byte[].class, null);
        }
        return value;
    }

    /**
     * @throws DateTimeException throw when source is error date time {@link String}
     * @throws JdbdException     throw when source couldn't convert or overflow.
     */
    public static LocalDateTime convertToLocalDateTime(final ColumnMeta meta, final Object source)
            throws JdbdException {

        final LocalDateTime value;
        if (source instanceof LocalDateTime) {
            value = (LocalDateTime) source;
        } else if (source instanceof String) {
            value = LocalDateTime.parse((String) source, JdbdTimes.DATETIME_FORMATTER_6);
        } else if (source instanceof LocalDate) {
            value = LocalDateTime.of((LocalDate) source, LocalTime.MIDNIGHT);
        } else {
            throw JdbdExceptions.cannotConvertColumnValue(meta, source, LocalDateTime.class, null);
        }
        return value;
    }

    /**
     * @throws DateTimeException throw when source is error date time {@link String}
     * @throws JdbdException     throw when source couldn't convert or overflow.
     */
    public static LocalDate convertToLocalDate(final ColumnMeta meta, final Object source) {
        final LocalDate value;
        if (source instanceof LocalDate) {
            value = (LocalDate) source;
        } else if (source instanceof String) {
            value = LocalDate.parse((String) source);
        } else if (source instanceof LocalDateTime) {
            value = ((LocalDateTime) source).toLocalDate();
        } else {
            throw JdbdExceptions.cannotConvertColumnValue(meta, source, LocalDate.class, null);
        }
        return value;
    }

    /**
     * @throws DateTimeException throw when source is error date time {@link String}
     * @throws JdbdException     throw when source couldn't convert or overflow.
     */
    public static LocalTime convertToLocalTime(final ColumnMeta meta, final Object source)
            throws JdbdException {

        final LocalTime value;
        if (source instanceof LocalTime) {
            value = (LocalTime) source;
        } else if (source instanceof String) {
            value = LocalTime.parse((String) source, JdbdTimes.TIME_FORMATTER_6);
        } else {
            throw JdbdExceptions.cannotConvertColumnValue(meta, source, LocalTime.class, null);
        }
        return value;
    }

    /**
     * @throws DateTimeException throw when source is error date time {@link String}
     * @throws JdbdException     throw when source couldn't convert or overflow.
     */
    public static YearMonth convertToYearMonth(final ColumnMeta meta, final Object source) throws JdbdException {
        final YearMonth value;
        if (source instanceof YearMonth) {
            value = (YearMonth) source;
        } else if (source instanceof String) {
            value = YearMonth.parse((String) source);
        } else if (source instanceof LocalDate || source instanceof LocalDateTime) {
            value = YearMonth.from((TemporalAccessor) source);
        } else {
            throw JdbdExceptions.cannotConvertColumnValue(meta, source, YearMonth.class, null);
        }
        return value;
    }

    /**
     * @throws DateTimeException throw when source is error date time {@link String}
     * @throws JdbdException     throw when source couldn't convert or overflow.
     */
    public static MonthDay convertToMonthDay(final ColumnMeta meta, final Object source) throws JdbdException {
        final MonthDay value;
        if (source instanceof MonthDay) {
            value = (MonthDay) source;
        } else if (source instanceof String) {
            value = MonthDay.parse((String) source);
        } else if (source instanceof LocalDate || source instanceof LocalDateTime) {
            value = MonthDay.from((TemporalAccessor) source);
        } else {
            throw JdbdExceptions.cannotConvertColumnValue(meta, source, MonthDay.class, null);
        }
        return value;
    }

    /**
     * @throws DateTimeException throw when source is error date time {@link String}
     * @throws JdbdException     throw when source couldn't convert or overflow.
     */
    public static Month convertToMonth(final ColumnMeta meta, final Object source) throws JdbdException {
        final Month value;
        if (source instanceof Month) {
            value = (Month) source;
        } else if (source instanceof String) {
            value = Month.valueOf((String) source);
        } else if (source instanceof LocalDate
                || source instanceof LocalDateTime
                || source instanceof YearMonth
                || source instanceof MonthDay) {
            value = Month.from((TemporalAccessor) source);
        } else {
            throw JdbdExceptions.cannotConvertColumnValue(meta, source, Month.class, null);
        }
        return value;
    }

    /**
     * @throws DateTimeException throw when source is error date time {@link String}
     * @throws JdbdException     throw when source couldn't convert or overflow.
     */
    public static DayOfWeek convertToDayOfWeek(final ColumnMeta meta, final Object source) throws JdbdException {
        final DayOfWeek value;
        if (source instanceof DayOfWeek) {
            value = (DayOfWeek) source;
        } else if (source instanceof String) {
            value = DayOfWeek.valueOf((String) source);
        } else if (source instanceof LocalDate
                || source instanceof LocalDateTime) {
            value = DayOfWeek.from((TemporalAccessor) source);
        } else {
            throw JdbdExceptions.cannotConvertColumnValue(meta, source, DayOfWeek.class, null);
        }
        return value;
    }

    /**
     * @throws JdbdException throw when source couldn't convert or overflow.
     */
    public static BitSet convertToBitSet(final ColumnMeta meta, final Object source) throws JdbdException {
        final BitSet value;
        if (source instanceof BitSet) {
            value = (BitSet) source;
        } else if (source instanceof String) {
            if (!(JdbdStrings.isBinaryString((String) source))) {
                throw JdbdExceptions.cannotConvertColumnValue(meta, source, BitSet.class, null);
            }
            value = JdbdStrings.bitStringToBitSet((String) source, true);
        } else if (source instanceof Long) {
            value = BitSet.valueOf(new long[]{(Long) source});
        } else if (source instanceof Integer) {
            value = BitSet.valueOf(new long[]{((Integer) source) & 0xFFFF_FFFFL});
        } else if (source instanceof Short) {
            value = BitSet.valueOf(new long[]{((Short) source) & 0xFFFFL});
        } else if (source instanceof Byte) {
            value = BitSet.valueOf(new long[]{((Byte) source) & 0xFFL});
        } else if (source instanceof BigInteger) {
            value = JdbdStrings.bitStringToBitSet(((BigInteger) source).toString(2), true);
        } else if (source instanceof Boolean) {
            value = new BitSet(1);
            if ((Boolean) source) {
                value.set(0);
            }
        } else {
            throw JdbdExceptions.cannotConvertColumnValue(meta, source, BitSet.class, null);
        }
        return value;
    }


    /**
     * @throws NumberFormatException throw when source is error number {@link String}
     * @throws ArithmeticException   throw when source overflow to target
     * @throws JdbdException         throw when source couldn't convert or overflow.
     */
    public static int convertToIntValue(final ColumnMeta meta, final Object source, final Class<?> targetClass,
                                        final int minValue, final int maxValue) {
        final int value;
        if (source instanceof Integer
                || source instanceof Short
                || source instanceof Byte) {
            value = ((Number) source).intValue();
        } else if (source instanceof String) {
            value = Integer.parseInt((String) source);
        } else if (source instanceof Long) {
            final long v = (Long) source;
            if (v < minValue || v > maxValue) {
                throw JdbdExceptions.cannotConvertColumnValue(meta, source, targetClass, null);
            }
            value = (int) v;
        } else if (source instanceof Boolean) {
            value = ((Boolean) source ? 1 : 0);
        } else if (source instanceof BigInteger) {
            value = ((BigInteger) source).intValueExact();
        } else if (source instanceof BigDecimal) {
            value = ((BigDecimal) source).intValueExact();
        } else {
            throw JdbdExceptions.cannotConvertColumnValue(meta, source, targetClass, null);
        }
        if (value < minValue || value > maxValue) {
            throw JdbdExceptions.columnValueOverflow(meta, source, targetClass, null);
        }
        return value;
    }


    /**
     * @throws NumberFormatException throw when source is error number {@link String}
     * @throws ArithmeticException   throw when source overflow to target
     * @throws JdbdException         throw when source couldn't convert or overflow.
     */
    public static int convertToIntUnsignedValue(final ColumnMeta meta, final Object source, final Class<?> targetClass,
                                                final int maxValue) {

        final int value;
        if (source instanceof Integer) {
            value = (Integer) source;
        } else if (source instanceof Long) {
            final long v = (Long) source;
            if ((v & (~0xFFFF_FFFFL)) != 0) {
                throw JdbdExceptions.cannotConvertColumnValue(meta, source, targetClass, null);
            }
            value = (int) v;
        } else if (source instanceof String) {
            if (JdbdNumbers.isHexNumber((String) source)) {
                value = Integer.decode((String) source);
            } else {
                value = Integer.parseUnsignedInt((String) source);
            }
        } else if (source instanceof Short) {
            value = ((Short) source) & 0xFFFF;
        } else if (source instanceof Byte) {
            value = ((Byte) source) & 0xFFFF;
        } else if (source instanceof Boolean) {
            value = ((Boolean) source ? 1 : 0);
        } else if (source instanceof BigInteger) {
            final long v = ((BigInteger) source).longValueExact();
            if ((v & (~0xFFFF_FFFFL)) != 0) {
                throw JdbdExceptions.columnValueOverflow(meta, source, targetClass, null);
            }
            value = (int) v;
        } else if (source instanceof BigDecimal) {
            final long v = ((BigDecimal) source).longValueExact();
            if ((v & (~0xFFFF_FFFFL)) != 0) {
                throw JdbdExceptions.columnValueOverflow(meta, source, targetClass, null);
            }
            value = (int) v;
        } else if (source instanceof BitSet) {
            final BitSet v = (BitSet) source;
            if (v.length() > 32) {
                throw JdbdExceptions.cannotConvertColumnValue(meta, source, targetClass, null);
            }
            int bitSet = 0;
            for (int i = 0; i < 32; i++) {
                if (v.get(i)) {
                    bitSet |= (1 << i);
                }
            }
            value = bitSet;
        } else {
            throw JdbdExceptions.cannotConvertColumnValue(meta, source, targetClass, null);
        }

        if (maxValue != -1 && (value & (~maxValue)) != 0) {
            throw JdbdExceptions.columnValueOverflow(meta, source, targetClass, null);
        }
        return value;

    }


    /**
     * @throws NumberFormatException throw when source is error number {@link String}
     * @throws ArithmeticException   throw when source overflow to target
     * @throws JdbdException         throw when source couldn't convert or overflow.
     */
    public static long convertToLongValue(final ColumnMeta meta, final Object source, final Class<?> targetClass,
                                          final long minValue, final long maxValue) {
        final long value;
        if (source instanceof Long
                || source instanceof Integer
                || source instanceof Short
                || source instanceof Byte) {
            value = ((Number) source).longValue();
        } else if (source instanceof String) {
            value = Long.parseLong((String) source);
        } else if (source instanceof Boolean) {
            final boolean v = (Boolean) source;
            value = (v ? 1 : 0);
        } else if (source instanceof BigInteger) {
            value = ((BigInteger) source).longValueExact();
        } else if (source instanceof BigDecimal) {
            value = ((BigDecimal) source).longValueExact();
        } else {
            throw JdbdExceptions.cannotConvertColumnValue(meta, source, targetClass, null);
        }

        if (value < minValue || value > maxValue) {
            throw JdbdExceptions.columnValueOverflow(meta, source, targetClass, null);
        }
        return value;
    }

    /**
     * @throws NumberFormatException throw when source is error number {@link String}
     * @throws ArithmeticException   throw when source overflow to target
     * @throws JdbdException         throw when source couldn't convert or overflow.
     */
    public static long convertToLongUnsignedValue(final ColumnMeta meta, final Object source, final Class<?> targetClass,
                                                  final long maxValue) {
        final long value;
        if (source instanceof Integer || source instanceof Long) {
            value = ((Number) source).longValue();
        } else if (source instanceof String) {
            if (JdbdNumbers.isHexNumber((String) source)) {
                value = Long.decode((String) source);
            } else {
                value = Long.parseUnsignedLong((String) source);
            }
        } else if (source instanceof Short) {
            value = ((Short) source) & 0xFFFFL;
        } else if (source instanceof Byte) {
            value = ((Byte) source) & 0xFFFFL;
        } else if (source instanceof Boolean) {
            value = ((Boolean) source ? 1 : 0);
        } else if (source instanceof BigInteger) {
            value = Long.parseUnsignedLong(source.toString());
        } else if (source instanceof BigDecimal) {
            value = Long.parseUnsignedLong(((BigDecimal) source).stripTrailingZeros().toPlainString());
        } else if (source instanceof BitSet) {
            final BitSet v = (BitSet) source;
            if (v.length() > 64) {
                throw JdbdExceptions.cannotConvertColumnValue(meta, source, targetClass, null);
            }
            long bitSet = 0;
            for (int i = 0; i < 64; i++) {
                if (v.get(i)) {
                    bitSet |= (1L << i);
                }
            }
            value = bitSet;
        } else {
            throw JdbdExceptions.cannotConvertColumnValue(meta, source, targetClass, null);
        }

        if (maxValue != -1L && (value & (~maxValue)) != 0) {
            throw JdbdExceptions.columnValueOverflow(meta, source, targetClass, null);
        }
        return value;
    }


    public static OffsetDateTime convertToOffsetDateTime(final ColumnMeta meta, final Object source,
                                                         final @Nullable ZoneOffset serverZone) {
        final OffsetDateTime value;
        if (source instanceof OffsetDateTime) {
            value = (OffsetDateTime) source;
        } else if (source instanceof ZonedDateTime) {
            value = ((ZonedDateTime) source).toOffsetDateTime();
        } else if (source instanceof String) {
            value = OffsetDateTime.parse((String) source, JdbdTimes.OFFSET_DATETIME_FORMATTER_6);
        } else if (source instanceof LocalDateTime && serverZone != null) {
            value = OffsetDateTime.of((LocalDateTime) source, serverZone);
        } else {
            throw JdbdExceptions.cannotConvertColumnValue(meta, source, OffsetDateTime.class, null);
        }
        return value;
    }

    public static ZonedDateTime convertToZonedDateTime(final ColumnMeta meta, final Object source,
                                                       final @Nullable ZoneOffset serverZone) {
        final ZonedDateTime value;
        if (source instanceof ZonedDateTime) {
            value = (ZonedDateTime) source;
        } else if (source instanceof OffsetDateTime) {
            value = ((OffsetDateTime) source).toZonedDateTime();
        } else if (source instanceof String) {
            value = ZonedDateTime.parse((String) source, JdbdTimes.OFFSET_DATETIME_FORMATTER_6);
        } else if (source instanceof LocalDateTime && serverZone != null) {
            value = ZonedDateTime.of((LocalDateTime) source, serverZone);
        } else {
            throw JdbdExceptions.cannotConvertColumnValue(meta, source, ZonedDateTime.class, null);
        }
        return value;
    }

    public static OffsetTime convertToOffsetTime(final ColumnMeta meta, final Object source,
                                                 final @Nullable ZoneOffset serverZone) {
        final OffsetTime value;
        if (source instanceof OffsetTime) {
            value = (OffsetTime) source;
        } else if (source instanceof String) {
            value = OffsetTime.parse((String) source, JdbdTimes.OFFSET_TIME_FORMATTER_6);
        } else if (source instanceof LocalTime && serverZone != null) {
            value = OffsetTime.of((LocalTime) source, serverZone);
        } else {
            throw JdbdExceptions.cannotConvertColumnValue(meta, source, OffsetTime.class, null);
        }
        return value;
    }


    public static Point convertToPoint(final ColumnMeta meta, final Object source) {
        final Point value;
        if (source instanceof Point) {
            value = (Point) source;
        } else if (source instanceof byte[]) {
            value = JdbdSpatials.readPointWkb(meta, (byte[]) source);
        } else if (source instanceof String) {
            value = JdbdSpatials.readPointWkt(meta, (String) source);
        } else {
            throw JdbdExceptions.cannotConvertColumnValue(meta, source, Point.class, null);
        }
        return value;
    }


}
