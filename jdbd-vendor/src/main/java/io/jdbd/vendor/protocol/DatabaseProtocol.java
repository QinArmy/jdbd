package io.jdbd.vendor.protocol;

import io.jdbd.result.*;
import io.jdbd.session.Closeable;
import io.jdbd.session.DatabaseSession;
import io.jdbd.session.OptionSpec;
import io.jdbd.session.ServerVersion;
import io.jdbd.statement.BindStatement;
import io.jdbd.statement.MultiStatement;
import io.jdbd.statement.StaticStatement;
import io.jdbd.statement.StaticStatementSpec;
import io.jdbd.vendor.stmt.*;
import io.jdbd.vendor.task.PrepareTask;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public interface DatabaseProtocol extends OptionSpec, Closeable {



    long sessionIdentifier();


    /**
     * <p>
     * This method is underlying api of {@link StaticStatementSpec#executeUpdate(String)} method.
     * <br/>
     */
    Mono<ResultStates> update(StaticStmt stmt);

    /**
     * <p>
     * This method is underlying api of below methods:
     * <ul>
     *     <li>{@link StaticStatementSpec#executeQuery(String)}</li>
     *     <li>{@link StaticStatementSpec#executeQuery(String, Function)}</li>
     *     <li>{@link StaticStatementSpec#executeQuery(String, Function, Consumer)}</li>
     * </ul>
     *<br/>
     */
    <R> Flux<R> query(StaticStmt stmt, Function<CurrentRow, R> function, Consumer<ResultStates> consumer);


    /**
     * <p>
     * This method is underlying api of {@link StaticStatement#executeBatchUpdate(List)} method.
     *<br/>
     */
    Flux<ResultStates> batchUpdate(StaticBatchStmt stmt);

    QueryResults batchQuery(StaticBatchStmt stmt);

    /**
     * <p>
     * This method is underlying api of {@link StaticStatement#executeBatchAsMulti(List)} method.
     *<br/>
     */
    MultiResult batchAsMulti(StaticBatchStmt stmt);

    /**
     * <p>
     * This method is underlying api of {@link StaticStatement#executeBatchAsFlux(List)} method.
     *<br/>
     */
    OrderedFlux batchAsFlux(StaticBatchStmt stmt);

    OrderedFlux staticMultiStmtAsFlux(StaticMultiStmt stmt);

    /**
     * <p>
     * This method is one of underlying api of {@link BindStatement#executeUpdate()} method.
     *<br/>
     */
    Mono<ResultStates> paramUpdate(ParamStmt stmt, boolean usePrepare);

    /**
     * <p>
     * This method is one of underlying api of below methods:
     *<br/>
     */
    <R> Flux<R> paramQuery(ParamStmt stmt, boolean usePrepare, Function<CurrentRow, R> function, Consumer<ResultStates> consumer);


    OrderedFlux paramAsFlux(ParamStmt stmt, boolean usePrepare);

    /**
     * <p>
     * This method is one of underlying api of {@link BindStatement#executeBatchUpdate()} method.
     * <br/>
     */
    Flux<ResultStates> paramBatchUpdate(ParamBatchStmt stmt, boolean usePrepare);

    QueryResults paramBatchQuery(ParamBatchStmt stmt, boolean usePrepare);

    <R> Flux<R> paramBatchQueryAsFlux(ParamBatchStmt stmt, boolean usePrepare, Function<CurrentRow, R> function, Consumer<ResultStates> consumer);

    /**
     * <p>
     * This method is one of underlying api of {@link BindStatement#executeBatchAsMulti()} method.
     * <br/>
     */
    MultiResult paramBatchAsMulti(ParamBatchStmt stmt, boolean usePrepare);

    /**
     * <p>
     * This method is one of underlying api of {@link BindStatement#executeBatchAsFlux()} method.
     *<br/>
     */
    OrderedFlux paramBatchAsFlux(ParamBatchStmt stmt, boolean usePrepare);

    /**
     * <p>
     * This method is underlying api of {@link MultiStatement#executeBatchUpdate()} method.
     *<br/>
     */
    Flux<ResultStates> multiStmtBatchUpdate(ParamMultiStmt stmt);

    QueryResults multiStmtBatchQuery(ParamMultiStmt stmt);

    /**
     * <p>
     * This method is underlying api of {@link MultiStatement#executeBatchAsMulti()} method.
     *<br/>
     */
    MultiResult multiStmtAsMulti(ParamMultiStmt stmt);

    /**
     * <p>
     * This method is underlying api of {@link MultiStatement#executeBatchAsFlux()} method.
     *<br/>
     */
    OrderedFlux multiStmtAsFlux(ParamMultiStmt stmt);

    /**
     * <p>
     * This method is underlying api of {@link DatabaseSession#prepareStatement(String)} methods:
     *<br/>
     */
    Mono<PrepareTask> prepare(String sql);


    Mono<Void> ping();

    Mono<Void> reset();

    Mono<Void> logicallyClose();

    boolean supportMultiStmt();

    boolean supportOutParameter();


    boolean supportStmtVar();


    ServerVersion serverVersion();


    boolean inTransaction();


    void addSessionCloseListener(Runnable listener);

    void addTransactionEndListener(Runnable listener);

    boolean isClosed();

    @Override
    <T> Mono<T> close();


}
