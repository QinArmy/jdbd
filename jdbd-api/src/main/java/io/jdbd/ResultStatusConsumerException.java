package io.jdbd;

import io.jdbd.result.ResultStates;

import java.util.function.Consumer;

public final class ResultStatusConsumerException extends JdbdNonSQLException {

    public static ResultStatusConsumerException create(Consumer<ResultStates> consumer, Throwable cause) {
        String message = String.format("%s Consumer throw exception:%s", consumer, cause.getMessage());
        return new ResultStatusConsumerException(message, cause);
    }

    @Deprecated
    public ResultStatusConsumerException(Throwable cause, String messageFormat, Object... args) {
        super(cause, messageFormat, args);
    }

    public ResultStatusConsumerException(String message, Throwable cause) {
        super(message, cause);
    }

}
