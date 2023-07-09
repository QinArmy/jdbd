package io.jdbd.statement;

import io.jdbd.JdbdNonSQLException;

@Deprecated
public final class UnsupportedBindJavaTypeException extends JdbdNonSQLException {

    private final Class<?> notSupportType;

    public UnsupportedBindJavaTypeException(Class<?> notSupportType) {
        super(String.format("Not supported bind java type[%s]", notSupportType.getName()));
        this.notSupportType = notSupportType;
    }


    public Class<?> getNotSupportType() {
        return this.notSupportType;
    }


}
