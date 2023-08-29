package io.jdbd.statement;

import io.jdbd.JdbdException;
import io.jdbd.lang.Nullable;
import io.jdbd.meta.DataType;
import io.jdbd.session.ChunkOption;
import io.jdbd.session.DatabaseSession;
import io.jdbd.session.Option;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import java.util.function.Function;

/**
 * <p>
 * This interface representing the multi sql statement (separated by semicolons {@code ;})
 * that support sql parameter placeholder({@code ?}) with client-prepare.
 * </p>
 * <p>
 * If you invoke only once {@link #addStatement(String)} ,then this interface is similar to {@link BindStatement},
 * except that don't support server-prepare.
 * </p>
 * <p>
 * This interface is similar to {@link StaticStatementSpec#executeMultiStmt(String)} method,
 * except that support sql parameter placeholder({@code ?}) with client-prepare.
 * </p>
 * <p>
 * The instance of this interface is created by {@link DatabaseSession#multiStatement()} method.
 * </p>
 *
 * @since 1.0
 */
public interface MultiStatement extends MultiResultStatement, ParametrizedStatement {

    /**
     * @param sql must have text.
     * @return <strong>this</strong>
     * @throws IllegalArgumentException when sql has no text.
     * @throws JdbdException            when reuse this instance after invoke below method:
     *                                  <ul>
     *                                      <li>{@link #executeBatchUpdate()}</li>
     *                                      <li>{@link #executeBatchQuery()}</li>
     *                                      <li>{@link #executeBatchAsMulti()}</li>
     *                                      <li>{@link #executeBatchAsFlux()}</li>
     *                                  </ul>
     */
    MultiStatement addStatement(String sql) throws JdbdException;

    /**
     * {@inheritDoc }
     */
    @Override
    MultiStatement bind(int indexBasedZero, DataType dataType, @Nullable Object value) throws JdbdException;

    /**
     * {@inheritDoc }
     */
    @Override
    MultiStatement bindStmtVar(String name, DataType dataType, @Nullable Object value) throws JdbdException;


    /**
     * {@inheritDoc }
     */
    @Override
    MultiStatement setTimeout(int seconds) throws JdbdException;

    /**
     * {@inheritDoc }
     */
    @Override
    MultiStatement setFetchSize(int fetchSize) throws JdbdException;

    /**
     * {@inheritDoc }
     */
    @Override
    MultiStatement setImportPublisher(Function<ChunkOption, Publisher<byte[]>> function) throws JdbdException;

    /**
     * {@inheritDoc }
     */
    @Override
    MultiStatement setExportSubscriber(Function<ChunkOption, Subscriber<byte[]>> function) throws JdbdException;


    /**
     * {@inheritDoc }
     */
    <T> MultiStatement setOption(Option<T> option, @Nullable T value) throws JdbdException;


}
