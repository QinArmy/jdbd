package io.jdbd.result;

import io.jdbd.JdbdException;

/**
 * Emit when {@link MultiResultSpec} have no more result.
 *
 * @see MultiResult
 */
public final class NoMoreResultException extends JdbdException {

    public NoMoreResultException(String message) {
        super(message);
    }


}
