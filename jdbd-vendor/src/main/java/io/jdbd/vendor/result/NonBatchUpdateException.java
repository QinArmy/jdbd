package io.jdbd.vendor.result;

import io.jdbd.JdbdException;
import io.jdbd.statement.BindSingleStatement;

/**
 * <p>
 * emit(not throw) when subscribe executeBatchUpdate() method but database server response not match.
 * For example : subscribe {@link BindSingleStatement#executeBatchUpdate()} but database server response query result.
 * </p>
 *
 * @since 1.0
 */
public final class NonBatchUpdateException extends JdbdException {

    public NonBatchUpdateException(String message) {
        super(message);
    }

}
