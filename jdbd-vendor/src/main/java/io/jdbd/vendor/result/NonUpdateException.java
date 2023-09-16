package io.jdbd.vendor.result;

import io.jdbd.JdbdException;
import io.jdbd.statement.BindSingleStatement;

/**
 * <p>
 * emit(not throw) when subscribe executeUpdate() method but database server response not match.
 * For example : subscribe {@link BindSingleStatement#executeUpdate()} but database server response query result.
 * <br/>
 *
 * @since 1.0
 */
public final class NonUpdateException extends JdbdException {

    public NonUpdateException(String message) {
        super(message);
    }

}
