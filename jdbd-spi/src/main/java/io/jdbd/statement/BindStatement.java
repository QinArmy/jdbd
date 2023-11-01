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
 * <p>This interface representing the adaptor of client-prepared statement and server-prepared statement.
 * <p>The instance of this interface is created by {@link DatabaseSession#bindStatement(String, boolean)} method.
 * <p>Application developer can help driver developer cache server-prepared statement by {@link #setFrequency(int)} methods.
 *
 * @see io.jdbd.session.DatabaseSession#bindStatement(String, boolean)
 * @since 1.0
 */
public interface BindStatement extends BindSingleStatement {


    /**
     * whether force use server-prepare
     *
     * @return true : must use server prepare statement.
     * @see io.jdbd.session.DatabaseSession#bindStatement(String, boolean)
     */
    boolean isForcePrepare();


    /**
     * {@inheritDoc }
     */
    @Override
    BindStatement bind(int indexBasedZero, DataType dataType, @Nullable Object value) throws JdbdException;


    /**
     * {@inheritDoc }
     */
    @Override
    BindStatement bindStmtVar(String name, DataType dataType, @Nullable Object value) throws JdbdException;

    /**
     * {@inheritDoc }
     */
    @Override
    BindStatement addBatch() throws JdbdException;

    /**
     * {@inheritDoc }
     */
    @Override
    BindStatement setFrequency(int frequency) throws IllegalArgumentException;

    /**
     * {@inheritDoc }
     */
    @Override
    BindStatement setTimeout(int millSeconds) throws IllegalArgumentException;

    /**
     * {@inheritDoc }
     */
    @Override
    BindStatement setFetchSize(int fetchSize) throws IllegalArgumentException;

    /**
     * {@inheritDoc }
     */
    @Override
    BindStatement setImportPublisher(Function<ChunkOption, Publisher<byte[]>> function) throws JdbdException;

    /**
     * {@inheritDoc }
     */
    @Override
    BindStatement setExportSubscriber(Function<ChunkOption, Subscriber<byte[]>> function) throws JdbdException;


    /**
     * {@inheritDoc }
     */
    <T> BindStatement setOption(Option<T> option, @Nullable T value) throws JdbdException;


}
