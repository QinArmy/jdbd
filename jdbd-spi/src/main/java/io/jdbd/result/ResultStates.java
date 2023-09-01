package io.jdbd.result;


import io.jdbd.lang.Nullable;
import io.jdbd.session.Option;
import io.jdbd.session.OptionSpec;
import io.jdbd.statement.Statement;

import java.util.function.Consumer;

/**
 * <p>
 * The interface representing the states of the result of sql statement (eg: SELECT/INSERT/UPDATE/DELETE).
 *     <ul>
 *         <li>If {@link #hasColumn()} is true ,then this instance representing the terminator of query result (eg: SELECT command)</li>
 *         <li>Else this instance representing the update result (eg: INSERT/UPDATE/DELETE command)</li>
 *     </ul>
 * </p>
 * <p>
 *  The instance of this interface always is the last item of same query result in the {@link OrderedFlux}.
 * </p>
 * <p>
 * The {@link #getResultNo()} of this interface always return same value with {@link ResultRowMeta} in same query result.
 * </p>
 *
 * @see ResultRowMeta
 * @see ResultRow
 * @since 1.0
 */
public interface ResultStates extends ResultItem, OptionSpec {

    Consumer<ResultStates> IGNORE_STATES = states -> {
    };


    boolean isSupportInsertId();

    boolean inTransaction();

    long affectedRows();

    long lastInsertedId();

    /**
     * @return empty or  success info(maybe contain warning info)
     */
    String message();

    boolean hasMoreResult();


    /**
     * @return true representing exists server cursor and the last row don't send.
     */
    boolean hasMoreFetch();

    /**
     * @return <ul>
     * <li>true : this instance representing the terminator of query result (eg: SELECT command)</li>
     * <li>false : this instance representing the update result (eg: INSERT/UPDATE/DELETE command)</li>
     * </ul>
     */
    boolean hasColumn();

    /**
     * @return the row count.<ul>
     * <li>If use fetch (eg: {@link Statement#setFetchSize(int)} , {@link RefCursor}) , then the row count representing only the row count of current fetch result.</li>
     * <li>Else then the row count representing the total row count of query result.</li>
     * </ul>
     */
    long rowCount();


    @Nullable
    Warning warning();

    /**
     * <p>
     * This the implementation <strong>perhaps</strong> support some of  following :
     *     <ul>
     *         <li>{@link Option#IN_TRANSACTION}</li>
     *         <li>{@link Option#READ_ONLY} </li>
     *         <li>{@link Option#AUTO_COMMIT}</li>
     *         <li>{@link Option#CURSOR}</li>
     *     </ul>
     * </p>
     */
    @Nullable
    @Override
    <T> T valueOf(Option<T> option);

}
