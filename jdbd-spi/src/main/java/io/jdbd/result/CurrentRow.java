package io.jdbd.result;


import java.util.function.Consumer;
import java.util.function.Function;

/**
 * <p>
 * This interface representing the current row that result set reader have read from database client protocol.
 * This interface is designed for reducing the instance of {@link ResultRow} in following methods:
 * <ul>
 *     <li>{@link io.jdbd.statement.StaticStatementSpec#executeQuery(String, Function, Consumer)}</li>
 *     <li>{@link io.jdbd.statement.BindSingleStatement#executeQuery(Function, Consumer)}</li>
 *     <li>{@link MultiResult#nextQuery(Function, Consumer)}</li>
 *     <li>{@link QueryResults#nextQuery(Function, Consumer)}</li>
 * </ul>
 * </p>
 * <p>
 * The {@link #getResultNo()} of this interface always return same value with {@link ResultRowMeta} in same query result.
 * See {@link #getRowMeta()}
 * </p>
 *
 * @see ResultRow
 * @since 1.0
 */
public interface CurrentRow extends DataRow {

    /**
     * @return the row number of current row, based 1 . The first value is 1 .
     */
    long rowNumber();

    /**
     * <p>
     * Create one {@link ResultRow} with coping all column data.
     * </p>
     */
    ResultRow asResultRow();


}
