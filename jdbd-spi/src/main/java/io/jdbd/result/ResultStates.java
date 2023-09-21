package io.jdbd.result;


import io.jdbd.JdbdException;
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
 * <br/>
 * <p>
 *  The instance of this interface always is the last item of same query result in the {@link OrderedFlux}.
 * <br/>
 * <p>
 * The {@link #getResultNo()} of this interface always return same value with {@link ResultRowMeta} in same query result.
 * <br/>
 *
 * @see ResultRowMeta
 * @see ResultRow
 * @since 1.0
 */
public interface ResultStates extends ResultItem, OptionSpec {

    Consumer<ResultStates> IGNORE_STATES = states -> {
    };

    /**
     * Whether support {@link #lastInsertedId()} method or not.
     * <p>If false ,then database usually support RETURNING clause,it better than lastInsertedId ,for example : PostgreSQL
     *
     * @return true : support
     */
    boolean isSupportInsertId();


    /**
     * Session whether in transaction block when statement end.
     *
     * @return true : session in transaction block when statement end.
     * @throws JdbdException throw when {@link #valueOf(Option)} with {@link Option#IN_TRANSACTION} return null.
     */
    boolean inTransaction() throws JdbdException;

    /**
     * statement affected rows
     *
     * @return statement affected rows
     */
    long affectedRows();

    /**
     * the last inserted id
     * <strong>NOTE</strong>:
     * <ul>
     *     <li>when {@link #isSupportInsertId()} is false,throw {@link JdbdException} . now database usually support RETURNING clause,it better than lastInsertedId ,for example : PostgreSQL</li>
     *     <li>when use multi-row insert syntax ,the last inserted id is the first row id.</li>
     *     <li>If you use multi-row insert syntax and exists conflict clause(e.g : MySQL ON DUPLICATE KEY UPDATE),then database never return correct lastInsertedId</li>
     * </ul>
     *
     * @return the last inserted id
     * @throws JdbdException throw when {@link #isSupportInsertId()} return false
     */
    long lastInsertedId() throws JdbdException;

    /**
     * the info about statement execution.
     *
     * @return empty or  success info(maybe contain warning info)
     */
    String message();

    /**
     * Whether exists more result after this result or not .
     *
     * @return true : exists more result after this result
     */
    boolean hasMoreResult();


    /**
     * exists more fetch
     *
     * @return true representing exists server cursor and the last row don't send.
     * @see Statement#setFetchSize(int)
     */
    boolean hasMoreFetch();

    /**
     * <p>
     * The result whether exists column or not.
     * </p>
     *
     * @return <ul>
     * <li>true : this instance representing the terminator of query result (eg: SELECT command)</li>
     * <li>false : this instance representing the update result (eg: INSERT/UPDATE/DELETE command)</li>
     * </ul>
     */
    boolean hasColumn();

    /**
     * <p>
     * Current result row count.
     * </p>
     *
     * @return the row count.<ul>
     * <li>If use fetch (eg: {@link Statement#setFetchSize(int)} , {@link RefCursor}) , then the row count representing only the row count of current fetch result.</li>
     * <li>Else then the row count representing the total row count of query result.</li>
     * </ul>
     */
    long rowCount();


    /**
     * warn info
     *
     * @return nullable warn info
     */
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
     * <br/>
     *
     * @param option option key
     * @param <T>    option value java type
     * @return nullable option value
     */
    @Nullable
    @Override
    <T> T valueOf(Option<T> option);

}
