package io.jdbd.result;

import io.jdbd.JdbdException;

import java.util.function.Consumer;

public final class ResultStatusConsumerException extends JdbdException {

    public static ResultStatusConsumerException create(Consumer<ResultStates> consumer, Throwable cause) {
        String message = String.format("%s Consumer throw exception:%s", consumer, cause.getMessage());
        return new ResultStatusConsumerException(message, cause);
    }


    public ResultStatusConsumerException(String message, Throwable cause) {
        super(message, cause);
    }

}
