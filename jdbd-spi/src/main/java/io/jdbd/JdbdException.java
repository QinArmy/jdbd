package io.jdbd;


import io.jdbd.lang.Nullable;

/**
 * <p>
 * Emit(or throw) when driver occur error.
 * <br/>
 *
 * @since 1.0
 */
public class JdbdException extends RuntimeException {

    private final String sqlState;

    private final int vendorCode;

    public JdbdException(String message) {
        super(message);
        this.sqlState = null;
        this.vendorCode = 0;
    }


    public JdbdException(String message, @Nullable String sqlState, int vendorCode) {
        super(message);
        this.sqlState = sqlState;
        this.vendorCode = vendorCode;
    }


    public JdbdException(String message, Throwable cause, @Nullable String sqlState, int vendorCode) {
        super(message, cause);
        this.sqlState = sqlState;
        this.vendorCode = vendorCode;
    }

    public JdbdException(String message, Throwable cause) {
        super(message, cause);
        this.sqlState = null;
        this.vendorCode = 0;
    }


    public JdbdException(String message, Throwable cause,
                         boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.sqlState = null;
        this.vendorCode = 0;
    }


    public JdbdException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace,
                         @Nullable String sqlState, int vendorCode) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.sqlState = sqlState;
        this.vendorCode = vendorCode;
    }


    @Nullable
    public final String getSqlState() {
        return this.sqlState;
    }

    public final int getVendorCode() {
        return vendorCode;
    }


}
