package io.jdbd.statement;

import io.jdbd.result.*;
import io.jdbd.session.DatabaseSession;
import org.reactivestreams.Publisher;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * <p>
 * This interface representing the ability that execute static sql statement that couldn't contain any sql parameter placeholder({@code ?})
 * </p>
 * <p>
 * This interface is base interface of following:
 *     <ul>
 *         <li>{@link StaticStatement},it execute static statement with statement options. eg: timeout</li>
 *         <li>{@link DatabaseSession},it execute static statement without any statement option. eg: timeout</li>
 *     </ul>
 * </p>
 *
 * @since 1.0
 */
public interface StaticStatementSpec {


    /**
     * Executes the given SQL statement(no parameter placeholder) thant can only producer one update result.
     * for example :
     * <ul>
     *     <li>INSERT</li>
     *     <li>UPDATE</li>
     *     <li>DELETE</li>
     *     <li>CREATE TABLE</li>
     *     <li>CALL Stored procedures that just produce one update result and no out parameter.</li>
     * </ul>
     * this method like {@code java.sql.Statement#executeUpdate(String)}
     * <p>
     *     <strong>NOTE</strong> : driver don't send message to database server before subscribing.
     * </p>
     *
     * @param sql sql thant can only producer one update result.
     * @return the {@link Publisher} emit just one {@link ResultStates} or {@link Throwable}, Like {@code reactor.core.publisher.Mono} .
     * @throws io.jdbd.JdbdException emit(not throw) when
     *                               <ul>
     *                                   <li>sql have no text</li>
     *                                   <li>sql syntax error</li>
     *                                   <li>session have closed, see {@link io.jdbd.session.SessionCloseException}</li>
     *                                   <l>network error</l>
     *                                   <li>server response error message, see {@link ServerException}</li>
     *                               </ul>
     */
    Publisher<ResultStates> executeUpdate(String sql);


    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // s is instance of {@link DatabaseSession} or {@link StaticStatement}.
     *             s.executeQuery(sql,CurrentRow::asResultRow,states -> {}) ; // ignore ResultStates instance.
     *         </code>
     *     </pre>
     * </p>
     *
     * @see #executeQuery(String, Function, Consumer)
     */
    Publisher<ResultRow> executeQuery(String sql);

    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // s is instance of {@link DatabaseSession} or {@link StaticStatement}.
     *             s.executeQuery(sql,function,states -> {}) ; // ignore ResultStates instance.
     *         </code>
     *     </pre>
     * </p>
     *
     * @see #executeQuery(String, Function, Consumer)
     */
    <R> Publisher<R> executeQuery(String sql, Function<CurrentRow, R> function);

    /**
     * <p>
     * Executes the static SQL query in this {@link StaticStatementSpec} object
     * and returns 0-n row generated by the query.
     * </p>
     * <p>
     * <strong>NOTE</strong> : driver don't send message to database server before subscribing.
     * </p>
     *
     * @return the {@link Publisher} emit 0-N element or {@link Throwable}, Like {@code reactor.core.publisher.Flux} .
     * @throws NullPointerException  emit(not throw) when
     *                               <ul>
     *                                   <li>function is null</li>
     *                                   <li>consumer is null</li>
     *                               </ul>
     * @throws io.jdbd.JdbdException emit(not throw) when
     *                               <ul>
     *                                   <li>sql have no text</li>
     *                                   <li>sql syntax error</li>
     *                                   <li>session have closed, see {@link io.jdbd.session.SessionCloseException}</li>
     *                                   <l>network error</l>
     *                                   <li>server response error message, see {@link ServerException}</li>
     *                               </ul>
     */
    <R> Publisher<R> executeQuery(String sql, Function<CurrentRow, R> function, Consumer<ResultStates> consumer);


    /**
     * <p>
     * <strong>NOTE</strong> : driver don't send message to database server before subscribing.
     * </p>
     */
    Publisher<ResultStates> executeBatchUpdate(List<String> sqlGroup);

    /**
     * <p>
     * <strong>NOTE</strong> : driver don't send message to database server before subscribing.
     * </p>
     */
    QueryResults executeBatchQuery(List<String> sqlGroup);

    /**
     * <p>
     * <strong>NOTE</strong> : driver don't send message to database server before subscribing.
     * </p>
     */
    MultiResult executeBatchAsMulti(List<String> sqlGroup);

    /**
     * <p>
     * <strong>NOTE</strong> : driver don't send message to database server before subscribing.
     * </p>
     */
    OrderedFlux executeBatchAsFlux(List<String> sqlGroup);

    /**
     * <p>
     * Execute one or more static sql statement (separated by semicolons {@code ;}) .
     * </p>
     * <p>
     * This method is similar to {@link MultiStatement} interface,
     * except that don't support any sql parameter placeholder({@code ?}).
     * </p>
     * <p>
     * <strong>NOTE</strong> : driver don't send message to database server before subscribing.
     * </p>
     *
     * @param multiStmt a single single sql statement or multi sql statement (separated by semicolons {@code ;})
     * @return the {@link OrderedFlux} emit 0-N {@link ResultItem} or {@link Throwable}, Like {@code reactor.core.publisher.Flux} .
     * @throws io.jdbd.JdbdException emit(not throw) when
     *                               <ul>
     *                                   <li>multiStmt have no text</li>
     *                                   <li>multiStmt syntax error</li>
     *                                   <li>session have closed, see {@link io.jdbd.session.SessionCloseException}</li>
     *                                   <l>network error</l>
     *                                   <li>server response error message, see {@link ServerException}</li>
     *                               </ul>
     */
    OrderedFlux executeMultiStmt(String multiStmt);


}
