package io.jdbd.result;

import io.jdbd.statement.StaticStatementSpec;
import org.reactivestreams.Publisher;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * <p>
 * This interface representing multi-result of statement.
 * </p>
 * <p>
 * This interface is base interface of following :
 *     <ul>
 *         <li>{@link MultiResult}</li>
 *         <li>{@link QueryResults}</li>
 *     </ul>
 * </p>
 * <p>
 * <strong>NOTE</strong> : driver don't send message to database server before first subscribing.
 * </p>
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
     * </p>
     *
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
     * </p>
     *
     * @see #nextQuery(Function, Consumer)
     */
    <R> Publisher<R> nextQuery(Function<CurrentRow, R> function);


    <R> Publisher<R> nextQuery(Function<CurrentRow, R> function, Consumer<ResultStates> consumer);

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
     * </p>
     *
     * @param fluxFunc convertor function of Publisher ,for example : {@code reactor.core.publisher.Flux#from(org.reactivestreams.Publisher)}
     * @param <F>      F representing Flux that emit 0-N element or {@link Throwable}.
     * @return non-null Flux that emit just one element or {@link Throwable}.
     * @throws NullPointerException throw when
     *                              <ul>
     *                                  <li>fluxFunc is null</li>
     *                                  <li>fluxFunc return null</li>
     *                              </ul>
     * @see #nextQuery(Function, Consumer)
     */
    <R, F extends Publisher<R>> F nextQuery(Function<CurrentRow, R> rowFunc, Consumer<ResultStates> statesConsumer, Function<Publisher<R>, F> fluxFunc);


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
     * </p>
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
