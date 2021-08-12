package io.jdbd.postgre.util;

import io.jdbd.vendor.util.JdbdTimeUtils;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Locale;

import static java.time.temporal.ChronoField.*;

public abstract class PgTimes extends JdbdTimeUtils {


    public static final DateTimeFormatter ISO_LOCAL_TIME_FORMATTER = new DateTimeFormatterBuilder()
            .appendValue(HOUR_OF_DAY, 2)
            .appendLiteral(':')
            .appendValue(MINUTE_OF_HOUR, 2)
            .optionalStart()
            .appendLiteral(':')
            .appendValue(SECOND_OF_MINUTE, 2)

            .optionalStart()
            .appendFraction(MICRO_OF_SECOND, 0, 6, true)
            .optionalEnd()
            .toFormatter(Locale.ENGLISH);

    public static final DateTimeFormatter ISO_LOCAL_DATETIME_FORMATTER = new DateTimeFormatterBuilder()
            .append(DateTimeFormatter.ISO_LOCAL_DATE)
            .appendLiteral(' ')
            .append(ISO_LOCAL_TIME_FORMATTER)
            .toFormatter(Locale.ENGLISH);

    public static final DateTimeFormatter ISO_OFFSET_DATETIME__FORMATTER = new DateTimeFormatterBuilder()
            .append(ISO_LOCAL_DATETIME_FORMATTER)
            .appendOffset("+HH:MM", "+00:00")
            .toFormatter(Locale.ENGLISH);

    public static final DateTimeFormatter ISO_OFFSET_TIME__FORMATTER = new DateTimeFormatterBuilder()
            .append(ISO_LOCAL_TIME_FORMATTER)
            .appendOffset("+HH:MM", "+00:00")
            .toFormatter(Locale.ENGLISH);


}