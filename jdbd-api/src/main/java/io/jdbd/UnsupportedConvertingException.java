package io.jdbd;

import io.jdbd.meta.SQLType;

public class UnsupportedConvertingException extends JdbdNonSQLException {

    private final SQLType sourceType;

    private final Class<?> targetType;

    public UnsupportedConvertingException(String message, SQLType sourceType, Class<?> targetType) {
        super(message);
        this.sourceType = sourceType;
        this.targetType = targetType;
    }

    public UnsupportedConvertingException(String message, Throwable cause, SQLType sourceType, Class<?> targetType) {
        super(message, cause);
        this.sourceType = sourceType;
        this.targetType = targetType;
    }

    public final SQLType getSourceType() {
        return this.sourceType;
    }

    public final Class<?> getTargetType() {
        return this.targetType;
    }


}