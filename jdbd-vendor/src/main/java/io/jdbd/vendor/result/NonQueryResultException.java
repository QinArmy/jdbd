package io.jdbd.vendor.result;

import io.jdbd.JdbdException;
import io.jdbd.statement.BindSingleStatement;

import java.util.function.Consumer;
import java.util.function.Function;


/**
 * <p>
 * emit(not throw) when subscribe executeQuery() method but database server response not match.
 * For example : subscribe {@link BindSingleStatement#executeQuery(Function, Consumer)} but database server response update result.
 * </p>
 *
 * @since 1.0
 */
public final class NonQueryResultException extends JdbdException {

    public NonQueryResultException(String message) {
        super(message);
    }

}
