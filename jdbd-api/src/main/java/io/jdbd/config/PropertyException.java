package io.jdbd.config;

import io.jdbd.JdbdNonSQLException;
import io.jdbd.lang.Nullable;

public class PropertyException extends JdbdNonSQLException {

    private final String propertyName;

    public PropertyException(String propertyName, String message) {
        super(message);
        this.propertyName = propertyName;
    }

    public PropertyException(String propertyName, String message, @Nullable Throwable cause) {
        super(message, cause);
        this.propertyName = propertyName;
    }

    public final String getPropertyName() {
        return propertyName;
    }
}
