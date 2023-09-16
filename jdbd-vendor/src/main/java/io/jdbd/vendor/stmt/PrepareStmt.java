package io.jdbd.vendor.stmt;


import io.jdbd.session.ChunkOption;
import io.jdbd.session.DatabaseSession;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import java.util.function.Function;

/**
 * <p>
 * This interface representing adapter of:
 *     <ul>
 *         <li>{@link ParamStmt}</li>
 *         <li>{@link ParamBatchStmt}</li>
 *     </ul>
 *     The implementation of this interface is used by underlying implementation of {@link io.jdbd.statement.PreparedStatement}.
 * <br/>
 *
 * @see DatabaseSession#prepareStatement(String)
 */
public interface PrepareStmt extends ParamSingleStmt {

    /**
     * @throws IllegalStateException when no actual {@link ParamSingleStmt}
     */
    ParamSingleStmt getStmt();

    /**
     * @throws IllegalStateException throw when {@link #getStmt()} throw {@link IllegalStateException}.
     */
    @Override
    int getTimeout();

    /**
     * @throws IllegalStateException throw when {@link #getStmt()} throw {@link IllegalStateException}.
     * @see ParamBatchStmt#getFetchSize()
     */
    @Override
    int getFetchSize();

    /**
     * @throws IllegalStateException throw when {@link #getStmt()} throw {@link IllegalStateException}.
     * @see ParamBatchStmt#getImportFunction()
     */
    @Override
    Function<ChunkOption, Publisher<byte[]>> getImportFunction();

    /**
     * @throws IllegalStateException throw when {@link #getStmt()} throw {@link IllegalStateException}.
     * @see ParamBatchStmt#getExportFunction()
     */
    @Override
    Function<ChunkOption, Subscriber<byte[]>> getExportFunction();


}
