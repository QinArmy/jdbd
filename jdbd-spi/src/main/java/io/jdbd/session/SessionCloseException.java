package io.jdbd.session;

import io.jdbd.JdbdException;

/**
 * <p>
 * Emit(or throw) when session have closed.
 * </p>
 *
 * @since 1.0
 */
public final class SessionCloseException extends JdbdException {


    public SessionCloseException(String message) {
        super(message);
    }


}
