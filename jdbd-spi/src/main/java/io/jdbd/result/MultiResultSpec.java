package io.jdbd.result;

import io.jdbd.JdbdException;
import io.jdbd.statement.BindSingleStatement;
import io.jdbd.statement.StaticStatementSpec;
import org.reactivestreams.Publisher;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * <p>
 * This interface representing multi-result of statement.
 * <br/>
 * <p>
 * This interface is base interface of following :
 *     <ul>
 *         <li>{@link MultiResult}</li>
 *         <li>{@link QueryResults}</li>
 *     </ul>
 * <br/>
 * <p>
 * <strong>NOTE</strong> : driver don't send message to database server before first subscribing.
 * <br/>
 *
 * @since 1.0
 */
public interface MultiResultSpec {

    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // s is instance of {@link MultiResultSpec}.
     *             s.nextQuery(CurrentRow::asResultRow,states -> {}) ; // ignore ResultStates instance.
     *         </code>
     *     </pre>
     * <br/>
     *
     * @return see {@link #nextQuery(Function, Consumer)}
     * @see #nextQuery(Function, Consumer)
     */
    Publisher<ResultRow> nextQuery();

    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // s is instance of {@link MultiResultSpec}.
     *             s.nextQuery(function,states -> {}) ; // ignore ResultStates instance.
     *         </code>
     *     </pre>
     * <br/>
     *
     * @param rowFunc see {@link #nextQuery(Function, Consumer)}
     * @param <R>     see {@link #nextQuery(Function, Consumer)}
     * @return see {@link #nextQuery(Function, Consumer)}
     * @see #nextQuery(Function, Consumer)
     */
    <R> Publisher<R> nextQuery(Function<CurrentRow, R> rowFunc);

    /**
     * Subscribe next query result.
     *
     * @param rowFunc        current row map function.Using rowFunc to avoid create {@link ResultRow} instance for improving performance.<br/>
     *                       <strong>NOTE</strong>:
     *                       <ul>
     *                           <li>rowFunc couldn't return {@link CurrentRow} instance.</li>
     *                           <li>you couldn't invoke the block method of {@link Publisher} in rowFunc,or emit {@link Throwable}. For example :
     *                                  <ul>
     *                                      <li>{@code reactor.core.publisher.Flux#blockLast()}</li>
     *                                      <li>{@code reactor.core.publisher.Flux#blockFirst()}</li>
     *                                  </ul>
     *                           </li>
     *                       </ul>
     * @param statesConsumer a consumer to receive the {@link ResultStates}
     * @param <R>            row java type
     * @return the {@link Publisher} emit 0-N element or {@link Throwable}, Like {@code reactor.core.publisher.Flux} .
     */
    <R> Publisher<R> nextQuery(Function<CurrentRow, R> rowFunc, Consumer<ResultStates> statesConsumer);

    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // s is a instance of {@link MultiResultSpec}
     *              R flux  = fluxFunc.apply(s.nextQuery(rowFunc,statesConsumer)) ;
     *
     *              // for example ,if use Project reactor , reactor.core.publisher.Flux
     *              s.nextQuery(rowFunc,statesConsumer,Flux::from)
     *                 .collectList()
     *
     *         </code>
     *     </pre>
     * <br/>
     * @param rowFunc see {@link #nextQuery(Function, Consumer)}
     * @param statesConsumer  see {@link #nextQuery(Function, Consumer)}
     * @param fluxFunc convertor function of Publisher ,for example : {@code reactor.core.publisher.Flux#from(org.reactivestreams.Publisher)}
     * @param <F>      F representing Flux that emit 0-N element or {@link Throwable}.
     * @param <R> row java type
     * @return non-null Flux that emit just one element or {@link Throwable}.
     * @throws NullPointerException throw when
     *                              <ul>
     *                                  <li>fluxFunc is null</li>
     *                                  <li>fluxFunc return null</li>
     *                              </ul>
     * @see #nextQuery(Function, Consumer)
     */
    <R, F extends Publisher<R>> F nextQuery(Function<CurrentRow, R> rowFunc, Consumer<ResultStates> statesConsumer, Function<Publisher<R>, F> fluxFunc);

    /**
     * <p>subscribe next query result
     * <p>More info see {@link OrderedFlux}.
     *
     * @return {@link OrderedFlux} that emit just one query result or {@link Throwable}
     * @throws JdbdException emmit(not throw) when
     *                       <ul>
     *                           <li>you reuse this {@link io.jdbd.statement.Statement} instance</li>
     *                           <li>param bind error</li>
     *                           <li>the java type of value of appropriate dataType isn't supported by the implementation of this method ,for example : {@link io.jdbd.meta.JdbdType#BIGINT} bind {@link io.jdbd.type.Clob}</li>
     *                           <li>sql error</li>
     *                           <li>session have closed ,see {@link io.jdbd.session.SessionCloseException}</li>
     *                           <li>server response error ,see {@link ServerException}</li>
     *                           <li>result not match</li>
     *                       </ul>
     */
    OrderedFlux nextQueryFlux();

    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // s is a instance of {@link StaticStatementSpec}
     *              R flux  = fluxFunc.apply(s.nextQueryFlux()) ;
     *
     *              // for example ,if use Project reactor , reactor.core.publisher.Flux
     *              s.nextQueryFlux(Flux::from)
     *                 .collectList()
     *
     *         </code>
     *     </pre>
     * <br/>
     *
     * @param fluxFunc convertor function of Publisher ,for example : {@code reactor.core.publisher.Flux#from(org.reactivestreams.Publisher)}
     * @param <F>      F representing Flux that emit 0-N element or {@link Throwable}.
     * @return non-null Flux that emit just one element or {@link Throwable}.
     * @throws NullPointerException throw when
     *                              <ul>
     *                                  <li>fluxFunc is null</li>
     *                                  <li>fluxFunc return null</li>
     *                              </ul>
     * @see #nextQueryFlux()
     */
    <F extends Publisher<ResultItem>> F nextQueryFlux(Function<OrderedFlux, F> fluxFunc);


}
