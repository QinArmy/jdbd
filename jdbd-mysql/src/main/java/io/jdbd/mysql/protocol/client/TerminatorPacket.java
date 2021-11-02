package io.jdbd.mysql.protocol.client;

import io.jdbd.mysql.protocol.MySQLPacket;

import java.util.Arrays;

/**
 * @see EofPacket
 * @see OkPacket
 */
abstract class TerminatorPacket implements MySQLPacket {

    public static final byte SERVER_STATUS_IN_TRANS = 1;
    public static final byte SERVER_STATUS_AUTOCOMMIT = 1 << 1; // Server in auto_commit mode
    public static final byte SERVER_MORE_QUERY_EXISTS = 1 << 2;
    public static final byte SERVER_MORE_RESULTS_EXISTS = 1 << 3; // Multi query - next query exists

    public static final byte SERVER_QUERY_NO_GOOD_INDEX_USED = 1 << 4;
    public static final byte SERVER_QUERY_NO_INDEX_USED = 1 << 5;
    public static final byte SERVER_STATUS_CURSOR_EXISTS = 1 << 6;
    public static final short SERVER_STATUS_LAST_ROW_SENT = 1 << 7; // The server status for 'last-row-sent'

    public static final short SERVER_STATUS_DB_DROPPED = 1 << 8;
    public static final short SERVER_STATUS_NO_BACKSLASH_ESCAPES = 1 << 9;
    public static final short SERVER_STATUS_METADATA_CHANGED = 1 << 10;
    public static final short SERVER_QUERY_WAS_SLOW = 1 << 11;

    public static final short SERVER_PS_OUT_PARAMS = 1 << 12;
    public static final short SERVER_STATUS_IN_TRANS_READONLY = 1 << 13;
    public static final short SERVER_SESSION_STATE_CHANGED = 1 << 14;


    final int warnings;

    final int statusFags;

    TerminatorPacket(int warnings, int statusFags) {
        this.warnings = warnings;
        this.statusFags = statusFags;
    }


    public final int getWarnings() {
        return this.warnings;
    }

    public final int getStatusFags() {
        return this.statusFags;
    }

    public final boolean hasMoreFetch() {
        return (this.statusFags & SERVER_STATUS_CURSOR_EXISTS) != 0
                && (this.statusFags & SERVER_STATUS_LAST_ROW_SENT) == 0;
    }

    public final boolean hasMoreResult() {
        return (this.statusFags & SERVER_MORE_RESULTS_EXISTS) != 0;
    }

    public final boolean isReadOnly() {
        return isReadOnly(this.statusFags);
    }


    final void appendServerStatus(final StringBuilder builder) {
        final int statusFags = this.statusFags;
        final char[] bitCharMap = new char[16];
        Arrays.fill(bitCharMap, '.');
        int index = bitCharMap.length - 1;

        bitCharMap[index] = (statusFags & SERVER_STATUS_IN_TRANS) == 0 ? '0' : '1';
        builder.append(bitCharMap);
        bitCharMap[index--] = '.';
        builder.append(" = In transaction\n");

        bitCharMap[index] = (statusFags & SERVER_STATUS_AUTOCOMMIT) == 0 ? '0' : '1';
        builder.append(bitCharMap);
        bitCharMap[index--] = '.';
        builder.append(" = AUTO_COMMIT\n");

        bitCharMap[index] = (statusFags & SERVER_MORE_QUERY_EXISTS) == 0 ? '0' : '1';
        builder.append(bitCharMap);
        bitCharMap[index--] = '.';
        builder.append(" = Multi query\n");

        bitCharMap[index] = (statusFags & SERVER_MORE_RESULTS_EXISTS) == 0 ? '0' : '1';
        builder.append(bitCharMap);
        bitCharMap[index--] = '.';
        builder.append(" = More results\n");


        bitCharMap[index] = (statusFags & SERVER_QUERY_NO_GOOD_INDEX_USED) == 0 ? '0' : '1';
        builder.append(bitCharMap);
        bitCharMap[index--] = '.';
        builder.append(" = Bad index used\n");

        bitCharMap[index] = (statusFags & SERVER_QUERY_NO_INDEX_USED) == 0 ? '0' : '1';
        builder.append(bitCharMap);
        bitCharMap[index--] = '.';
        builder.append(" = No index used\n");

        bitCharMap[index] = (statusFags & SERVER_STATUS_CURSOR_EXISTS) == 0 ? '0' : '1';
        builder.append(bitCharMap);
        bitCharMap[index--] = '.';
        builder.append(" = Cursor exists\n");

        bitCharMap[index] = (statusFags & SERVER_STATUS_LAST_ROW_SENT) == 0 ? '0' : '1';
        builder.append(bitCharMap);
        bitCharMap[index--] = '.';
        builder.append(" = Last row sent\n");


        bitCharMap[index] = (statusFags & SERVER_STATUS_DB_DROPPED) == 0 ? '0' : '1';
        builder.append(bitCharMap);
        bitCharMap[index--] = '.';
        builder.append(" = Database dropped\n");

        bitCharMap[index] = (statusFags & SERVER_STATUS_NO_BACKSLASH_ESCAPES) == 0 ? '0' : '1';
        builder.append(bitCharMap);
        bitCharMap[index--] = '.';
        builder.append(" = N backslash escapes\n");

        bitCharMap[index] = (statusFags & SERVER_STATUS_METADATA_CHANGED) == 0 ? '0' : '1';
        builder.append(bitCharMap);
        bitCharMap[index--] = '.';
        builder.append(" = Metadata changed\n");

        bitCharMap[index] = (statusFags & SERVER_QUERY_WAS_SLOW) == 0 ? '0' : '1';
        builder.append(bitCharMap);
        bitCharMap[index--] = '.';
        builder.append(" = Query was slow\n");


        bitCharMap[index] = (statusFags & SERVER_PS_OUT_PARAMS) == 0 ? '0' : '1';
        builder.append(bitCharMap);
        bitCharMap[index--] = '.';
        builder.append(" = PS Out Params\n");

        bitCharMap[index] = (statusFags & SERVER_STATUS_IN_TRANS_READONLY) == 0 ? '0' : '1';
        builder.append(bitCharMap);
        bitCharMap[index--] = '.';
        builder.append(" = In transaction ReadOnly\n");

        bitCharMap[index] = (statusFags & SERVER_SESSION_STATE_CHANGED) == 0 ? '0' : '1';
        builder.append(bitCharMap);
        bitCharMap[index] = '.';
        builder.append(" = Session state changed");

    }


    public static boolean inTransaction(final int statusFags) {
        return (statusFags & SERVER_STATUS_AUTOCOMMIT) == 0
                || (statusFags & SERVER_STATUS_IN_TRANS) != 0;
    }

    public static boolean startedTransaction(final int statusFags) {
        return (statusFags & SERVER_STATUS_IN_TRANS) != 0;
    }

    public static boolean isReadOnly(final int statusFags) {
        return (statusFags & SERVER_STATUS_IN_TRANS_READONLY) != 0;
    }

}
