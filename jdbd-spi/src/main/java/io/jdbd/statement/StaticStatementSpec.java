package io.jdbd.statement;

import io.jdbd.JdbdException;
import io.jdbd.result.*;
import io.jdbd.session.DatabaseSession;
import org.reactivestreams.Publisher;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * <p>
 * This interface representing the ability that execute static sql statement that couldn't contain any sql parameter placeholder({@code ?})
 * <br/>
 * <p>
 * This interface is base interface of following:
 *     <ul>
 *         <li>{@link StaticStatement},it execute static statement with statement options. eg: timeout</li>
 *         <li>{@link DatabaseSession},it execute static statement without any statement option. eg: timeout</li>
 *     </ul>
 * <br/>
 *
 * @since 1.0
 */
public interface StaticStatementSpec {


    /**
     * Executes the given SQL statement(no parameter placeholder) thant can producer just one update result.
     * for example :
     * <ul>
     *     <li>INSERT</li>
     *     <li>UPDATE</li>
     *     <li>DELETE</li>
     *     <li>CREATE TABLE</li>
     *     <li>CALL Stored procedures that just produce one update result and no out parameter.</li>
     * </ul>
     * <p>The result is a {@link ResultStates} instance whose {@link ResultStates#hasColumn()} always return false and {@link ResultStates#getResultNo()} always return 1 .
     * <p>This method like {@code java.sql.Statement#executeUpdate(String)}
     * <p>This method is  similar to {@link BindSingleStatement#executeUpdate()}, except that don't support parameter.
     * <p><strong>NOTE</strong> : driver don't send message to database server before subscribing. Driver developer must guarantee this feature.
     *
     * @param sql sql thant can only producer one update result.
     * @return the {@link Publisher} emit just one {@link ResultStates} or {@link Throwable}, Like {@code reactor.core.publisher.Mono} .
     * @throws io.jdbd.JdbdException emit(not throw) when
     *                               <ul>
     *                                   <li>you reuse this {@link StaticStatement} instance,if this {@link StaticStatementSpec} instance is {@link StaticStatement} instance.</li>
     *                                   <li>sql have no text</li>
     *                                   <li>sql syntax error</li>
     *                                   <li>session have closed, see {@link io.jdbd.session.SessionCloseException}</li>
     *                                   <li>network error</li>
     *                                   <li>server response error message, see {@link ServerException}</li>
     *                                   <li>server response result not match,e.g: response multi-result,or query result</li>
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
     * <br/>
     *
     * @param sql see {@link #executeQuery(String, Function, Consumer)}
     * @return see {@link #executeQuery(String, Function, Consumer)}
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
     * <br/>
     *
     * @param sql     see {@link #executeQuery(String, Function, Consumer)}
     * @param rowFunc see {@link #executeQuery(String, Function, Consumer)}
     * @param <R>     see {@link #executeQuery(String, Function, Consumer)}
     * @return see {@link #executeQuery(String, Function, Consumer)}
     * @see #executeQuery(String, Function, Consumer)
     */
    <R> Publisher<R> executeQuery(String sql, Function<CurrentRow, R> rowFunc);

    /**
     * <p>Execute a sql statement and server response just one query result,the result consist of :
     * <ol>
     *     <li>one {@link ResultRowMeta},the {@link ResultRowMeta#getResultNo()} always return 1</li>
     *     <li>0-N data row,the {@link DataRow#getResultNo()} return same with {@link ResultRowMeta#getResultNo()}</li>
     *     <li>one {@link ResultStates},the {@link ResultStates#hasColumn()} always return true,he {@link ResultStates#getResultNo()} return same with {@link ResultRowMeta#getResultNo()}</li>
     * </ol>
     * To avoid creating {@link ResultRow} instance for improving performance ,driver create just one {@link CurrentRow} instance for this result<br/>
     * and wrap {@link ResultRowMeta} to {@link CurrentRow#getRowMeta()},and {@link ResultStates} is optional, if you don't need.
     * <p>This method like {@code  java.sql.Statement#executeQuery(String)}
     * <p>This method is  similar to {@link BindSingleStatement#executeQuery(Function, Consumer)}, except that don't support parameter.
     * <p><strong>NOTE</strong> : driver don't send message to database server before subscribing. Driver developer must guarantee this feature.
     *
     * @param sql            must have text,the sql that produce just one query result.
     * @param rowFunc        current row map function.Using rowFunc to avoid create {@link ResultRow} instance for improving performance.<br/>
     *                       <strong>NOTE</strong>:
     *                       <ul>
     *                           <li>rowFunc couldn't return {@link CurrentRow} instance.</li>
     *                           <li>driver will invoke rowFunc in an ordered / serial fashion. Typically ,rowFunc run in {@code  io.netty.channel.EventLoop} </li>
     *                           <li>you couldn't invoke the block method of {@link Publisher} in rowFunc,or emit {@link Throwable}. For example :
     *                                  <ul>
     *                                      <li>{@code reactor.core.publisher.Flux#blockLast()}</li>
     *                                      <li>{@code reactor.core.publisher.Flux#blockFirst()}</li>
     *                                  </ul>
     *                           </li>
     *                       </ul>
     * @param statesConsumer a consumer to receive {@link ResultStates},statesConsumer will be invoked just once by driver.<br/>
     *                       <strong>NOTE</strong>: driver will invoke statesConsumer in an ordered / serial fashion. Typically ,statesConsumer run in {@code  io.netty.channel.EventLoop}
     * @param <R>            the row java type,it is returned by rowFunc.
     * @return the {@link Publisher} emit 0-N element or {@link Throwable}, Like {@code reactor.core.publisher.Flux} .
     * @throws JdbdException        emmit(not throw) when
     *                              <ul>
     *                                  <li>you reuse this {@link StaticStatement} instance,if this {@link StaticStatementSpec} instance is {@link StaticStatement} instance.</li>
     *                                  <li>rowFunc throw {@link Throwable}</li>
     *                                  <li>statesConsumer throw {@link Throwable}</li>
     *                                  <li>sql error</li>
     *                                  <li>session have closed ,see {@link io.jdbd.session.SessionCloseException}</li>
     *                                  <li>server response error ,see {@link ServerException}</li>
     *                                  <li>server response result not match,e.g: response multi-result,or update result</li>
     *                                  <li>rowFunc return {@link CurrentRow} instance</li>
     *                              </ul>
     * @throws NullPointerException emit(not throw) when
     *                              <ul>
     *                                  <li>rowFunc is null</li>
     *                                  <li>statesConsumer is null</li>
     *                              </ul>
     */
    <R> Publisher<R> executeQuery(String sql, Function<CurrentRow, R> rowFunc, Consumer<ResultStates> statesConsumer);


    /**
     * <p>Execute batch static sql and server response multi update result.
     * <p>This method is  similar to {@link MultiResultStatement#executeBatchUpdate()}, except that don't support parameter.
     * <p><strong>NOTE</strong> : driver don't send message to database server before subscribing.Driver developer must guarantee this feature.
     * <p>More info ,see {@link MultiResultStatement#executeBatchUpdate()}
     *
     * @param sqlGroup sql group,non-empty
     * @return the {@link Publisher} emit 1-N {@link ResultStates} or {@link Throwable}, Like {@code reactor.core.publisher.Flux} .
     * @throws JdbdException emmit(not throw) when
     *                       <ul>
     *                           <li>you reuse this {@link StaticStatement} instance,if this {@link StaticStatementSpec} instance is {@link StaticStatement} instance.</li>
     *                           <li>sqlGroup is empty</li>
     *                           <li>sql error</li>
     *                           <li>session have closed ,see {@link io.jdbd.session.SessionCloseException}</li>
     *                           <li>server response error ,see {@link ServerException}</li>
     *                           <li>server response result not match ,for example:  query result</li>
     *                       </ul>
     */
    Publisher<ResultStates> executeBatchUpdate(List<String> sqlGroup);


    /**
     * <p>Execute batch static sql and server response multi query result.
     * <p>This is  similar to {@link MultiResultStatement#executeBatchQuery()}, except that don't support parameter.
     * <p><strong>NOTE</strong> : driver don't send message to database server before subscribing.Driver developer must guarantee this feature.
     * <p>More info ,see {@link MultiResultStatement#executeBatchQuery()}
     *
     * @param sqlGroup sql group,non-empty
     * @return {@link QueryResults} that can subscribe multi query result.
     */
    QueryResults executeBatchQuery(List<String> sqlGroup);


    /**
     * <p>Execute batch static sql and server response multi result.
     * <p>This method is  similar to {@link MultiResultStatement#executeBatchAsMulti()}, except that don't support parameter.
     * <p><strong>NOTE</strong> : driver don't send message to database server before subscribing.Driver developer must guarantee this feature.
     * <p>More info ,see {@link MultiResultStatement#executeBatchAsMulti()}
     *
     * @param sqlGroup sql group,non-empty
     * @return {@link QueryResults} that can subscribe multi query/update result.
     */
    MultiResult executeBatchAsMulti(List<String> sqlGroup);

    /**
     * <p>Execute batch static sql and server response multi result.
     * <p>This is  similar to {@link MultiResultStatement#executeBatchAsFlux()}, except that don't support parameter.
     * <p><strong>NOTE</strong> : driver don't send message to database server before subscribing.Driver developer must guarantee this feature.
     * <p>More info ,see {@link MultiResultStatement#executeBatchAsFlux()}
     *
     * @param sqlGroup sql group,non-empty
     * @return {@link OrderedFlux} that emit 1-N {@link ResultItem} instance.
     */
    OrderedFlux executeBatchAsFlux(List<String> sqlGroup);


    /**
     * <p>Execute one or more static sql statement (separated by semicolons {@code ;}) and server response multi-result
     * <p>This method is similar to {@link MultiStatement} interface,except that don't support any sql parameter placeholder({@code ?}).
     * <p><strong>NOTE</strong> : driver don't send message to database server before subscribing.Driver developer must guarantee this feature.
     *
     * @param multiStmt a single single sql statement or multi sql statement (separated by semicolons {@code ;})
     * @return the {@link OrderedFlux} emit 1-N {@link ResultItem} or {@link Throwable}, Like {@code reactor.core.publisher.Flux} .
     * @throws io.jdbd.JdbdException emit(not throw) when
     *                               <ul>
     *                                   <li>multiStmt have no text</li>
     *                                   <li>multiStmt syntax error</li>
     *                                   <li>session have closed, see {@link io.jdbd.session.SessionCloseException}</li>
     *                                   <li>network error</li>
     *                                   <li>server response error message, see {@link ServerException}</li>
     *                               </ul>
     */
    OrderedFlux executeMultiStmt(String multiStmt);


}
