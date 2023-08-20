package io.jdbd.vendor.task;

import io.jdbd.JdbdException;

final class TaskStatusException extends JdbdException {


    TaskStatusException(String message) {
        super(message);
    }

    TaskStatusException(String message, Throwable cause) {
        super(message, cause);
    }


}
