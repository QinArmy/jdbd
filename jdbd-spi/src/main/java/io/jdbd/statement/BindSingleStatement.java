package io.jdbd.statement;


import io.jdbd.JdbdException;
import io.jdbd.lang.Nullable;
import io.jdbd.meta.DataType;
import io.jdbd.result.CurrentRow;
import io.jdbd.result.ResultRow;
import io.jdbd.result.ResultStates;
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
     *                           <li>the java type of value of appropriate dataType isn't supported by the implementation of this method ,for example : {@link io.jdbd.meta.JdbdType#TINYTEXT} bind {@link io.jdbd.type.Text}</li>
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
    <R> Publisher<R> executeQuery(Function<CurrentRow, R> function);


    /**
     * <p>
     * <strong>NOTE</strong> : driver don't send message to database server before subscribing.
     * </p>
     *
     * @throws JdbdException emmit(not throw) when
     *                       <ul>
     *                           <li>param bind error</li>
     *                           <li>the java type of value of appropriate dataType isn't supported by the implementation of this method ,for example : {@link io.jdbd.meta.JdbdType#TINYTEXT} bind {@link io.jdbd.type.Text}</li>
     *                       </ul>
     */
    <R> Publisher<R> executeQuery(Function<CurrentRow, R> function, Consumer<ResultStates> statesConsumer);


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
