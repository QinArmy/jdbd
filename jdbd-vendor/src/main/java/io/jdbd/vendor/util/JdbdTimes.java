package io.jdbd.vendor.util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalUnit;
import java.util.Locale;

import static java.time.temporal.ChronoField.*;

public abstract class JdbdTimes {


    protected JdbdTimes() {
        throw new UnsupportedOperationException();
    }



    private static final String PATTERN = "+HH:MM:ss";

    private static final String NO_OFFSET_TEXT = "+00:00";


    public static final DateTimeFormatter ZONE_FORMATTER = new DateTimeFormatterBuilder()
            .appendOffset(PATTERN, NO_OFFSET_TEXT)
            .toFormatter(Locale.ENGLISH);


    public static final DateTimeFormatter TIME_FORMATTER_0 = new DateTimeFormatterBuilder()
            .appendValue(HOUR_OF_DAY, 2)
            .appendLiteral(':')
            .appendValue(MINUTE_OF_HOUR, 2)
            .optionalStart()
            .appendLiteral(':')
            .appendValue(SECOND_OF_MINUTE, 2)
            .toFormatter(Locale.ENGLISH);
    public static final DateTimeFormatter OFFSET_TIME_FORMATTER_0 = new DateTimeFormatterBuilder()
            .append(TIME_FORMATTER_0)
            .appendOffset(PATTERN, NO_OFFSET_TEXT)
            .toFormatter(Locale.ENGLISH);

    public static final DateTimeFormatter TIME_FORMATTER_6 = new DateTimeFormatterBuilder()
            .append(TIME_FORMATTER_0)
            .optionalStart()
            .appendFraction(MICRO_OF_SECOND, 0, 6, true)
            .optionalEnd()
            .toFormatter(Locale.ENGLISH);
    public static final DateTimeFormatter OFFSET_TIME_FORMATTER_6 = new DateTimeFormatterBuilder()
            .append(TIME_FORMATTER_6)
            .appendOffset(PATTERN, NO_OFFSET_TEXT)
            .toFormatter(Locale.ENGLISH);

    public static final DateTimeFormatter DATETIME_FORMATTER_0 = new DateTimeFormatterBuilder()
            .append(DateTimeFormatter.ISO_LOCAL_DATE)
            .appendLiteral(' ')
            .append(TIME_FORMATTER_0)
            .toFormatter(Locale.ENGLISH);
    public static final DateTimeFormatter OFFSET_DATETIME_FORMATTER_6 = new DateTimeFormatterBuilder()
            .append(DATETIME_FORMATTER_0)
            .optionalStart()
            .appendFraction(MICRO_OF_SECOND, 0, 6, true)
            .optionalEnd()
            .appendOffset(PATTERN, NO_OFFSET_TEXT)
            .toFormatter(Locale.ENGLISH);
    public static final DateTimeFormatter OFFSET_DATETIME_FORMATTER_0 = new DateTimeFormatterBuilder()
            .append(DATETIME_FORMATTER_0)
            .appendOffset(PATTERN, NO_OFFSET_TEXT)
            .toFormatter(Locale.ENGLISH);


    public static final DateTimeFormatter DATETIME_FORMATTER_6 = new DateTimeFormatterBuilder()
            .append(DATETIME_FORMATTER_0)
            .optionalStart()
            .appendFraction(MICRO_OF_SECOND, 0, 6, true)
            .optionalEnd()
            .toFormatter(Locale.ENGLISH);


    public static ZoneOffset systemZoneOffset() {
        return ZoneId.systemDefault().getRules().getOffset(Instant.EPOCH);
    }


    public static String format(final LocalTime time, final int scale) {
        final String text;
        switch (scale) {
            case 0:
                text = time.format(TIME_FORMATTER_0);
                break;
            case 1:
                text = time.format(TimeFormatterHolder.TIME_FORMATTER_1);
                break;
            case 2:
                text = time.format(TimeFormatterHolder.TIME_FORMATTER_2);
                break;
            case 3:
                text = time.format(TimeFormatterHolder.TIME_FORMATTER_3);
                break;
            case 4:
                text = time.format(TimeFormatterHolder.TIME_FORMATTER_4);
                break;
            case 5:
                text = time.format(TimeFormatterHolder.TIME_FORMATTER_5);
                break;
            default:
                text = time.format(TIME_FORMATTER_6);

        }
        return text;

    }


    public static String format(final LocalDateTime dateTime, final int scale) {
        final String text;
        switch (scale) {
            case 0:
                text = dateTime.format(DATETIME_FORMATTER_0);
                break;
            case 1:
                text = dateTime.format(DateTimeFormatterHolder.DATETIME_FORMATTER_1);
                break;
            case 2:
                text = dateTime.format(DateTimeFormatterHolder.DATETIME_FORMATTER_2);
                break;
            case 3:
                text = dateTime.format(DateTimeFormatterHolder.DATETIME_FORMATTER_3);
                break;
            case 4:
                text = dateTime.format(DateTimeFormatterHolder.DATETIME_FORMATTER_4);
                break;
            case 5:
                text = dateTime.format(DateTimeFormatterHolder.DATETIME_FORMATTER_5);
                break;
            default:
                text = dateTime.format(DATETIME_FORMATTER_6);

        }
        return text;

    }

    public static String format(final OffsetTime time, final int scale) {
        final String text;
        switch (scale) {
            case 0:
                text = time.format(OFFSET_TIME_FORMATTER_0);
                break;
            case 1:
                text = time.format(OffsetTimeFormatterExtensionHolder.OFFSET_TIME_FORMATTER_1);
                break;
            case 2:
                text = time.format(OffsetTimeFormatterExtensionHolder.OFFSET_TIME_FORMATTER_2);
                break;
            case 3:
                text = time.format(OffsetTimeFormatterExtensionHolder.OFFSET_TIME_FORMATTER_3);
                break;
            case 4:
                text = time.format(OffsetTimeFormatterExtensionHolder.OFFSET_TIME_FORMATTER_4);
                break;
            case 5:
                text = time.format(OffsetTimeFormatterExtensionHolder.OFFSET_TIME_FORMATTER_5);
                break;
            default:
                text = time.format(OFFSET_TIME_FORMATTER_6);

        }
        return text;

    }


    public static String format(final OffsetDateTime dateTime, final int scale) {
        final String text;
        switch (scale) {
            case 0:
                text = dateTime.format(OFFSET_DATETIME_FORMATTER_0);
                break;
            case 1:
                text = dateTime.format(OffsetDataTimeFormatterExtensionHolder.OFFSET_DATETIME_FORMATTER_1);
                break;
            case 2:
                text = dateTime.format(OffsetDataTimeFormatterExtensionHolder.OFFSET_DATETIME_FORMATTER_2);
                break;
            case 3:
                text = dateTime.format(OffsetDataTimeFormatterExtensionHolder.OFFSET_DATETIME_FORMATTER_3);
                break;
            case 4:
                text = dateTime.format(OffsetDataTimeFormatterExtensionHolder.OFFSET_DATETIME_FORMATTER_4);
                break;
            case 5:
                text = dateTime.format(OffsetDataTimeFormatterExtensionHolder.OFFSET_DATETIME_FORMATTER_5);
                break;
            default:
                text = dateTime.format(OFFSET_DATETIME_FORMATTER_6);

        }
        return text;

    }


    @SuppressWarnings("unchecked")
    public static <T extends Temporal> T truncatedIfNeed(final int scale, final T temporal) {
        final TemporalUnit unit;
        switch (scale) {
            case 0: {
                if (temporal.get(MICRO_OF_SECOND) == 0) {
                    unit = null;
                } else {
                    unit = ChronoUnit.SECONDS;
                }
            }
            break;
            case 1: {
                if (temporal.get(MICRO_OF_SECOND) % 100_000 == 0) {
                    unit = null;
                } else {
                    unit = TruncatedUnit.MILLIS_100;
                }
            }
            break;
            case 2: {
                if (temporal.get(MICRO_OF_SECOND) % 10_000 == 0) {
                    unit = null;
                } else {
                    unit = TruncatedUnit.MILLIS_10;
                }
            }
            break;
            case 3: {
                if (temporal.get(MICRO_OF_SECOND) % 1000 == 0) {
                    unit = null;
                } else {
                    unit = ChronoUnit.MILLIS;
                }
            }
            break;
            case 4: {
                if (temporal.get(MICRO_OF_SECOND) % 100 == 0) {
                    unit = null;
                } else {
                    unit = TruncatedUnit.MICROS_100;
                }
            }
            break;
            case 5: {
                if (temporal.get(MICRO_OF_SECOND) % 10 == 0) {
                    unit = null;
                } else {
                    unit = TruncatedUnit.MICROS_10;
                }
            }
            break;
            case 6: {
                if (temporal.get(NANO_OF_SECOND) % 100 == 0) {
                    unit = null;
                } else {
                    unit = ChronoUnit.MICROS;
                }
            }
            break;
            default:
                unit = null;
        }

        final Temporal value;
        if (unit == null) {
            value = temporal;
        } else if (temporal instanceof LocalDateTime) {
            value = ((LocalDateTime) temporal).truncatedTo(unit);
        } else if (temporal instanceof OffsetDateTime) {
            value = ((OffsetDateTime) temporal).truncatedTo(unit);
        } else if (temporal instanceof ZonedDateTime) {
            value = ((ZonedDateTime) temporal).truncatedTo(unit);
        } else if (temporal instanceof LocalTime) {
            value = ((LocalTime) temporal).truncatedTo(unit);
        } else if (temporal instanceof OffsetTime) {
            value = ((OffsetTime) temporal).truncatedTo(unit);
        } else {
            // unknown
            value = temporal;
        }
        return (T) value;
    }


    private static abstract class DateTimeFormatterHolder {

        private DateTimeFormatterHolder() {
            throw new UnsupportedOperationException();
        }

        private static final DateTimeFormatter DATETIME_FORMATTER_1 = new DateTimeFormatterBuilder()
                .append(DATETIME_FORMATTER_0)
                .optionalStart()
                .appendFraction(MICRO_OF_SECOND, 0, 1, true)
                .optionalEnd()
                .toFormatter(Locale.ENGLISH);

        private static final DateTimeFormatter DATETIME_FORMATTER_2 = new DateTimeFormatterBuilder()
                .append(DATETIME_FORMATTER_0)
                .optionalStart()
                .appendFraction(MICRO_OF_SECOND, 0, 2, true)
                .optionalEnd()
                .toFormatter(Locale.ENGLISH);

        private static final DateTimeFormatter DATETIME_FORMATTER_3 = new DateTimeFormatterBuilder()
                .append(DATETIME_FORMATTER_0)
                .optionalStart()
                .appendFraction(MICRO_OF_SECOND, 0, 3, true)
                .optionalEnd()
                .toFormatter(Locale.ENGLISH);

        private static final DateTimeFormatter DATETIME_FORMATTER_4 = new DateTimeFormatterBuilder()
                .append(DATETIME_FORMATTER_0)
                .optionalStart()
                .appendFraction(MICRO_OF_SECOND, 0, 4, true)
                .optionalEnd()
                .toFormatter(Locale.ENGLISH);

        private static final DateTimeFormatter DATETIME_FORMATTER_5 = new DateTimeFormatterBuilder()
                .append(DATETIME_FORMATTER_0)
                .optionalStart()
                .appendFraction(MICRO_OF_SECOND, 0, 5, true)
                .optionalEnd()
                .toFormatter(Locale.ENGLISH);

    }//v


    private static abstract class OffsetDataTimeFormatterExtensionHolder {

        private OffsetDataTimeFormatterExtensionHolder() {
            throw new UnsupportedOperationException();
        }

        private static final DateTimeFormatter OFFSET_DATETIME_FORMATTER_1 = new DateTimeFormatterBuilder()
                .append(DATETIME_FORMATTER_0)
                .optionalStart()
                .appendFraction(MICRO_OF_SECOND, 0, 1, true)
                .optionalEnd()
                .appendOffset(PATTERN, NO_OFFSET_TEXT)
                .toFormatter(Locale.ENGLISH);

        private static final DateTimeFormatter OFFSET_DATETIME_FORMATTER_2 = new DateTimeFormatterBuilder()
                .append(DATETIME_FORMATTER_0)
                .optionalStart()
                .appendFraction(MICRO_OF_SECOND, 0, 2, true)
                .optionalEnd()
                .appendOffset(PATTERN, NO_OFFSET_TEXT)
                .toFormatter(Locale.ENGLISH);

        private static final DateTimeFormatter OFFSET_DATETIME_FORMATTER_3 = new DateTimeFormatterBuilder()
                .append(DATETIME_FORMATTER_0)
                .optionalStart()
                .appendFraction(MICRO_OF_SECOND, 0, 3, true)
                .optionalEnd()
                .appendOffset(PATTERN, NO_OFFSET_TEXT)
                .toFormatter(Locale.ENGLISH);

        private static final DateTimeFormatter OFFSET_DATETIME_FORMATTER_4 = new DateTimeFormatterBuilder()
                .append(DATETIME_FORMATTER_0)
                .optionalStart()
                .appendFraction(MICRO_OF_SECOND, 0, 4, true)
                .optionalEnd()
                .appendOffset(PATTERN, NO_OFFSET_TEXT)
                .toFormatter(Locale.ENGLISH);

        private static final DateTimeFormatter OFFSET_DATETIME_FORMATTER_5 = new DateTimeFormatterBuilder()
                .append(DATETIME_FORMATTER_0)
                .optionalStart()
                .appendFraction(MICRO_OF_SECOND, 0, 5, true)
                .optionalEnd()
                .appendOffset(PATTERN, NO_OFFSET_TEXT)
                .toFormatter(Locale.ENGLISH);


    }//OffsetDataTimeFormatterExtensionHolder


    private static abstract class TimeFormatterHolder {

        private TimeFormatterHolder() {
            throw new UnsupportedOperationException();
        }

        private static final DateTimeFormatter TIME_FORMATTER_1 = new DateTimeFormatterBuilder()
                .append(TIME_FORMATTER_0)
                .optionalStart()
                .appendFraction(MICRO_OF_SECOND, 0, 1, true)
                .optionalEnd()
                .toFormatter(Locale.ENGLISH);

        private static final DateTimeFormatter TIME_FORMATTER_2 = new DateTimeFormatterBuilder()
                .append(TIME_FORMATTER_0)
                .optionalStart()
                .appendFraction(MICRO_OF_SECOND, 0, 2, true)
                .optionalEnd()
                .toFormatter(Locale.ENGLISH);

        private static final DateTimeFormatter TIME_FORMATTER_3 = new DateTimeFormatterBuilder()
                .append(TIME_FORMATTER_0)
                .optionalStart()
                .appendFraction(MICRO_OF_SECOND, 0, 3, true)
                .optionalEnd()
                .toFormatter(Locale.ENGLISH);

        private static final DateTimeFormatter TIME_FORMATTER_4 = new DateTimeFormatterBuilder()
                .append(TIME_FORMATTER_0)
                .optionalStart()
                .appendFraction(MICRO_OF_SECOND, 0, 4, true)
                .optionalEnd()
                .toFormatter(Locale.ENGLISH);

        private static final DateTimeFormatter TIME_FORMATTER_5 = new DateTimeFormatterBuilder()
                .append(TIME_FORMATTER_0)
                .optionalStart()
                .appendFraction(MICRO_OF_SECOND, 0, 5, true)
                .optionalEnd()
                .toFormatter(Locale.ENGLISH);


    }//TimeFormatterHolder


    private static abstract class OffsetTimeFormatterExtensionHolder {

        private OffsetTimeFormatterExtensionHolder() {
            throw new UnsupportedOperationException();
        }

        private static final DateTimeFormatter OFFSET_TIME_FORMATTER_1 = new DateTimeFormatterBuilder()
                .append(TimeFormatterHolder.TIME_FORMATTER_1)
                .appendOffset(PATTERN, NO_OFFSET_TEXT)
                .toFormatter(Locale.ENGLISH);

        private static final DateTimeFormatter OFFSET_TIME_FORMATTER_2 = new DateTimeFormatterBuilder()
                .append(TimeFormatterHolder.TIME_FORMATTER_2)
                .appendOffset(PATTERN, NO_OFFSET_TEXT)
                .toFormatter(Locale.ENGLISH);

        private static final DateTimeFormatter OFFSET_TIME_FORMATTER_3 = new DateTimeFormatterBuilder()
                .append(TimeFormatterHolder.TIME_FORMATTER_3)
                .appendOffset(PATTERN, NO_OFFSET_TEXT)
                .toFormatter(Locale.ENGLISH);

        private static final DateTimeFormatter OFFSET_TIME_FORMATTER_4 = new DateTimeFormatterBuilder()
                .append(TimeFormatterHolder.TIME_FORMATTER_4)
                .appendOffset(PATTERN, NO_OFFSET_TEXT)
                .toFormatter(Locale.ENGLISH);


        private static final DateTimeFormatter OFFSET_TIME_FORMATTER_5 = new DateTimeFormatterBuilder()
                .append(TimeFormatterHolder.TIME_FORMATTER_5)
                .appendOffset(PATTERN, NO_OFFSET_TEXT)
                .toFormatter(Locale.ENGLISH);


    }//OffsetTimeFormatterExtensionHolder


    /**
     * This TemporalUnit is designed for following :
     * <ul>
     *     <li>{@link LocalDateTime#truncatedTo(TemporalUnit)}</li>
     *     <li>{@link OffsetDateTime#truncatedTo(TemporalUnit)}</li>
     *     <li>{@link ZonedDateTime#truncatedTo(TemporalUnit)}</li>
     *     <li>{@link LocalTime#truncatedTo(TemporalUnit)}</li>
     *     <li>{@link OffsetTime#truncatedTo(TemporalUnit)}</li>
     * </ul>
     *
     * @since 1.0
     */
    private enum TruncatedUnit implements TemporalUnit {

        /**
         * @see ChronoUnit#MICROS
         */
        MICROS_10(Duration.ofNanos(10_000)),
        MICROS_100(Duration.ofNanos(100_000)),

        /**
         * @see ChronoUnit#MILLIS
         */
        MILLIS_10(Duration.ofMillis(10)),
        MILLIS_100(Duration.ofMillis(100));


        private final Duration duration;

        TruncatedUnit(Duration duration) {
            this.duration = duration;
        }

        @Override
        public final Duration getDuration() {
            return this.duration;
        }

        @Override
        public final boolean isDurationEstimated() {
            return false;
        }

        @Override
        public final boolean isDateBased() {
            return false;
        }

        @Override
        public final boolean isTimeBased() {
            return true;
        }

        @SuppressWarnings("unchecked")
        @Override
        public final <R extends Temporal> R addTo(R temporal, long amount) {
            return (R) temporal.plus(amount, this);
        }

        @Override
        public final long between(Temporal temporal1Inclusive, Temporal temporal2Exclusive) {
            return temporal1Inclusive.until(temporal2Exclusive, this);
        }


    }


}
