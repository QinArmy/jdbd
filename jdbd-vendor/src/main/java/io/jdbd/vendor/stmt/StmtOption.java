package io.jdbd.vendor.stmt;

import io.jdbd.lang.Nullable;
import io.jdbd.result.Cursor;
import io.jdbd.session.ChunkOption;
import io.jdbd.session.DatabaseSession;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import java.util.List;
import java.util.function.Function;

/**
 * <p>
 * This interface representing option of {@link io.jdbd.statement.Statement},
 * and is used by  the implementation of {@link Stmt} .
 * <br/>
 * <p>
 * This interface is base interface of following :
 *     <ul>
 *         <li>the implementation of {@link io.jdbd.statement.Statement} (here ,perhaps not must),it's up to driver vendor.</li>
 *         <li>{@link Stmt}</li>
 *     </ul>
 * <br/>
 */
public interface StmtOption {

    /**
     * time out millSeconds
     *
     * @return millSeconds
     */
    int getTimeout();

    /**
     * Get fetch size
     *
     * @return fetch size
     */
    int getFetchSize();

    /**
     * Get stmt var list
     *
     * @return a unmodified list.
     */
    List<NamedValue> getStmtVarList();

    /**
     * Get import function
     *
     * @return nullable
     */
    @Nullable
    Function<ChunkOption, Publisher<byte[]>> getImportFunction();

    /**
     * Get export function
     * @return nullable
     */
    @Nullable
    Function<ChunkOption, Subscriber<byte[]>> getExportFunction();

    /**
     * <p>
     * This method can be useful in following scenarios :
     *     <ul>
     *         <li>create {@link Cursor} from {@link io.jdbd.result.DataRow#get(int, Class)} , for example : PostgreSQL return cursor name</li>
     *         <li>create {@link Cursor} from {@link io.jdbd.result.ResultStates}, for example : PostgreSQL DECLARE cursor command </li>
     *     </ul>
     * <br/>
     *
     * @return the session that create this stmt.
     * @throws UnsupportedOperationException throw when this instance is {@link Stmt} and {@link Stmt#isSessionCreated()} return false.
     */
    DatabaseSession databaseSession();

}
