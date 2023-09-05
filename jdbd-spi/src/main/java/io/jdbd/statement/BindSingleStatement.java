package io.jdbd.statement;


import io.jdbd.JdbdException;
import io.jdbd.lang.Nullable;
import io.jdbd.meta.DataType;
import io.jdbd.result.*;
import io.jdbd.session.ChunkOption;
import io.jdbd.session.Option;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * <p>
 * This interface representing the single sql statement that support sql parameter placeholder({@code ?}) .
 * </p>
 * <p>
 * This interface is base interface of following:
 *     <ul>
 *         <li>{@link BindStatement}</li>
 *         <li>{@link PreparedStatement}</li>
 *     </ul>
 * </p>
 *
 * @see BindStatement
 * @see PreparedStatement
 */
public interface BindSingleStatement extends ParametrizedStatement, MultiResultStatement {


    /**
     * {@inheritDoc }
     */
    @Override
    BindSingleStatement bind(int indexBasedZero, DataType dataType, @Nullable Object value) throws JdbdException;

    /**
     * {@inheritDoc }
     */
    @Override
    BindSingleStatement bindStmtVar(String name, DataType dataType, @Nullable Object value) throws JdbdException;


    /**
     * <p>
     * Add current parameter group to batch item list.
     * </p>
     *
     * @return <strong>this</strong>
     */
    BindSingleStatement addBatch() throws JdbdException;

    /**
     * <p>
     * <strong>NOTE</strong> : driver don't send message to database server before subscribing.
     * </p>
     *
     * @throws JdbdException emmit(not throw) when
     *                       <ul>
     *                           <li>param bind error</li>
     *                           <li>the java type of value of appropriate dataType isn't supported by the implementation of this method ,for example : {@link io.jdbd.meta.JdbdType#TINYTEXT} bind {@link io.jdbd.type.Clob}</li>
     *                       </ul>
     * @see BindStatement#executeUpdate()
     * @see PreparedStatement#executeUpdate()
     */
    Publisher<ResultStates> executeUpdate();

    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // statement is a instance of {@link BindSingleStatement}
     *              R mono  = monoFunc.apply(statement.executeUpdate()) ;
     *
     *              // for example ,if use Project reactor , reactor.core.publisher.Mono
     *              statement.executeUpdate(Mono::from)
     *                 .map(ResultStates::affectedRows)
     *
     *         </code>
     *     </pre>
     * </p>
     *
     * @param monoFunc convertor function of Publisher ,for example : {@code reactor.core.publisher.Mono#from(org.reactivestreams.Publisher)}
     * @param <M>      M representing Mono that emit just one element or {@link Throwable}.
     * @return Mono that emit just one element or {@link Throwable}.
     * @see #executeUpdate()
     */
    <M extends Publisher<ResultStates>> M executeUpdate(Function<Publisher<ResultStates>, M> monoFunc);

    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // stmt is instance of {@link BindSingleStatement}.
     *             stmt.executeQuery(CurrentRow::asResultRow,states -> {}) ; // ignore ResultStates instance.
     *         </code>
     *     </pre>
     * </p>
     *
     * @see #executeQuery(Function, Consumer)
     */
    Publisher<ResultRow> executeQuery();


    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // stmt is instance of {@link BindSingleStatement}.
     *             stmt.executeQuery(function,states -> {}) ; // ignore ResultStates instance.
     *         </code>
     *     </pre>
     * </p>
     *
     * @see #executeQuery(Function, Consumer)
     */
    <R> Publisher<R> executeQuery(Function<CurrentRow, R> rowFunc);


    /**
     * <p>
     * <strong>NOTE</strong> : driver don't send message to database server before subscribing.
     * </p>
     *
     * @param rowFunc current row map function.<strong>NOTE</strong>: you couldn't invoke the block method of {@link Publisher} in rowFunc,or emit {@link Throwable}.<br/>
     *                for example :
     *                <ul>
     *                     <li>{@code reactor.core.publisher.Flux#blockLast()}</li>
     *                     <li>{@code reactor.core.publisher.Flux#blockFirst()}</li>
     *                </ul>
     * @throws JdbdException emmit(not throw) when
     *                       <ul>
     *                           <li>param bind error</li>
     *                           <li>the java type of value of appropriate dataType isn't supported by the implementation of this method ,for example : {@link io.jdbd.meta.JdbdType#TINYTEXT} bind {@link io.jdbd.type.Clob}</li>
     *                       </ul>
     */
    <R> Publisher<R> executeQuery(Function<CurrentRow, R> rowFunc, Consumer<ResultStates> statesConsumer);

    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // statement is a instance of {@link BindSingleStatement}
     *              R flux  = fluxFunc.apply(statement.executeQuery(rowFunc,statesConsumer)) ;
     *
     *              // for example ,if use Project reactor , reactor.core.publisher.Flux
     *              statement.executeQuery(rowFunc,statesConsumer,Flux::from)
     *                 .collectList()
     *
     *         </code>
     *     </pre>
     * </p>
     *
     * @param fluxFunc convertor function of Publisher ,for example : {@code reactor.core.publisher.Flux#from(org.reactivestreams.Publisher)}
     * @param <F>      F representing Flux that emit 0-N element or {@link Throwable}.
     * @return Flux that emit just one element or {@link Throwable}.
     * @see #executeQuery(Function, Consumer)
     */
    <R, F extends Publisher<R>> F executeQuery(Function<CurrentRow, R> rowFunc, Consumer<ResultStates> statesConsumer, Function<Publisher<R>, F> fluxFunc);


    /**
     * <p>
     * <strong>NOTE</strong> : driver don't send message to database server before subscribing.
     * </p>
     */
    OrderedFlux executeAsFlux();

    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // statement is a instance of {@link BindSingleStatement}
     *              R flux  = fluxFunc.apply(statement.executeAsFlux()) ;
     *
     *              // for example ,if use Project reactor , reactor.core.publisher.Flux
     *              statement.executeAsFlux(Flux::from)
     *                 .collectList()
     *
     *         </code>
     *     </pre>
     * </p>
     *
     * @param fluxFunc convertor function of Publisher ,for example : {@code reactor.core.publisher.Flux#from(org.reactivestreams.Publisher)}
     * @param <F>      F representing Flux that emit 0-N element or {@link Throwable}.
     * @return Flux that emit just one element or {@link Throwable}.
     * @see #executeAsFlux()
     */
    <F extends Publisher<ResultItem>> F executeAsFlux(Function<OrderedFlux, F> fluxFunc);


    /**
     * {@inheritDoc }
     */
    @Override
    BindSingleStatement setTimeout(int seconds) throws JdbdException;

    /**
     * {@inheritDoc }
     */
    @Override
    BindSingleStatement setFetchSize(int fetchSize) throws JdbdException;

    /**
     * {@inheritDoc }
     */
    @Override
    BindSingleStatement setImportPublisher(Function<ChunkOption, Publisher<byte[]>> function) throws JdbdException;

    /**
     * {@inheritDoc }
     */
    @Override
    BindSingleStatement setExportSubscriber(Function<ChunkOption, Subscriber<byte[]>> function) throws JdbdException;

    /**
     * {@inheritDoc }
     */
    <T> BindSingleStatement setOption(Option<T> option, @Nullable T value) throws JdbdException;


}
