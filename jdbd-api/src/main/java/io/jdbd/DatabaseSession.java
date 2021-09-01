package io.jdbd;

import io.jdbd.meta.DatabaseMetaData;
import io.jdbd.result.MultiResult;
import io.jdbd.stmt.BindableStatement;
import io.jdbd.stmt.MultiStatement;
import io.jdbd.stmt.PreparedStatement;
import io.jdbd.stmt.StaticStatement;
import org.reactivestreams.Publisher;

import java.sql.Connection;
import java.sql.Savepoint;
import java.util.List;

public interface DatabaseSession extends ReactiveCloseable {

    DatabaseMetaData getDatabaseMetaData();


    /**
     * @see Connection#createStatement()
     */
    StaticStatement statement();

    /**
     * @see java.sql.Connection#prepareStatement(String)
     */
    Publisher<PreparedStatement> prepare(String sql);

    /**
     * @see java.sql.Connection#prepareStatement(String)
     */
    @Deprecated
    Publisher<PreparedStatement> prepare(String sql, int executeTimeout);

    BindableStatement bindable(String sql);

    MultiStatement multi();

    MultiResult multi(List<String> sqlList);

    /**
     * @see java.sql.DatabaseMetaData#supportsSavepoints()
     */
    boolean supportSavePoints();

    /**
     * @see java.sql.Connection#setSavepoint()
     */
    Publisher<Savepoint> setSavepoint();

    /**
     * @see java.sql.Connection#setSavepoint(String)
     */
    Publisher<Savepoint> setSavepoint(String name);

    /**
     * @see java.sql.Connection#releaseSavepoint(Savepoint)
     */
    Publisher<Void> releaseSavePoint(Savepoint savepoint);

    /**
     * @see java.sql.Connection#rollback(Savepoint)
     */
    Publisher<Void> rollbackToSavePoint(Savepoint savepoint);


    /**
     * @see Connection#isClosed()
     */
    Publisher<Boolean> isClosed();


}
