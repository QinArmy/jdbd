package io.jdbd.statement;

import io.jdbd.JdbdException;
import io.jdbd.lang.Nullable;
import io.jdbd.result.ServerException;

/**
 * <p>
 * Throw when statement timeout
 * </p>
 *
 * @since 1.0
 */
public final class TimeoutException extends JdbdException {


    public TimeoutException(String message) {
        super(message);
    }

    public TimeoutException(String message, Throwable cause) {
        super(message, cause, getServerSqlState(cause), getServerVendorCode(cause));
    }


    @Nullable
    private static String getServerSqlState(Throwable cause) {
        if (cause instanceof ServerException) {
            return ((ServerException) cause).getSqlState();
        }
        return null;
    }

    private static int getServerVendorCode(Throwable cause) {
        if (cause instanceof ServerException) {
            return ((ServerException) cause).getVendorCode();
        }
        return 0;
    }

}
