package io.jdbd;


import reactor.util.annotation.Nullable;

public class JdbdException extends RuntimeException {

    public JdbdException(String message) {
        super(message);
    }

    public JdbdException(String message, @Nullable Throwable cause) {
        super(message, cause);
    }

    public JdbdException(Throwable cause) {
        super(cause);
    }

    public JdbdException(String message, @Nullable Throwable cause
            , boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public JdbdException(String messageFormat, Object... args) {
        super(createMessage(messageFormat, args));
    }

    public JdbdException(@Nullable Throwable cause, String messageFormat, Object... args) {
        super(createMessage(messageFormat, args), cause);

    }

    public JdbdException(@Nullable Throwable cause, boolean enableSuppression
            , boolean writableStackTrace, String messageFormat, Object... args) {
        super(createMessage(messageFormat, args), cause, enableSuppression, writableStackTrace);
    }


    protected static String createMessage(@Nullable String messageFormat, @Nullable Object... args) {
        String msg;
        if (messageFormat != null && args != null && args.length > 0) {
            msg = String.format(messageFormat, args);
        } else {
            msg = messageFormat;
        }
        return msg;
    }


}