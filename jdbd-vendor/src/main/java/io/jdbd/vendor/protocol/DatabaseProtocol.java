package io.jdbd.vendor.protocol;

import io.jdbd.result.*;
import io.jdbd.session.*;
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


    Function<CurrentRow, ResultRow> ROW_FUNC = CurrentRow::asResultRow;

    Function<Option<?>, ?> OPTION_FUNC = option -> null;


    long sessionIdentifier();


    /**
     * <p>
     * This method is underlying api of {@link StaticStatementSpec#executeUpdate(String)} method.
     * </p>
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
     * </p>
     */
    <R> Flux<R> query(StaticStmt stmt, Function<CurrentRow, R> function);


    /**
     * <p>
     * This method is underlying api of {@link StaticStatement#executeBatchUpdate(List)} method.
     * </p>
     */
    Flux<ResultStates> batchUpdate(StaticBatchStmt stmt);

    BatchQuery batchQuery(StaticBatchStmt stmt);

    /**
     * <p>
     * This method is underlying api of {@link StaticStatement#executeBatchAsMulti(List)} method.
     * </p>
     */
    MultiResult batchAsMulti(StaticBatchStmt stmt);

    /**
     * <p>
     * This method is underlying api of {@link StaticStatement#executeBatchAsFlux(List)} method.
     * </p>
     */
    OrderedFlux batchAsFlux(StaticBatchStmt stmt);

    OrderedFlux executeAsFlux(StaticMultiStmt stmt);

    /**
     * <p>
     * This method is one of underlying api of {@link BindStatement#executeUpdate()} method.
     * </p>
     */
    Mono<ResultStates> paramUpdate(ParamStmt stmt, boolean usePrepare);

    /**
     * <p>
     * This method is one of underlying api of below methods:
     * </p>
     */
    <R> Flux<R> paramQuery(ParamStmt stmt, boolean usePrepare, Function<CurrentRow, R> function);

    /**
     * <p>
     * This method is one of underlying api of {@link BindStatement#executeBatchUpdate()} method.
     * </p>
     */
    Flux<ResultStates> paramBatchUpdate(ParamBatchStmt stmt, boolean usePrepare);

    BatchQuery paramBatchQuery(ParamBatchStmt stmt, boolean usePrepare);

    /**
     * <p>
     * This method is one of underlying api of {@link BindStatement#executeBatchAsMulti()} method.
     * </p>
     */
    MultiResult paramBatchAsMulti(ParamBatchStmt stmt, boolean usePrepare);

    /**
     * <p>
     * This method is one of underlying api of {@link BindStatement#executeBatchAsFlux()} method.
     * </p>
     */
    OrderedFlux paramBatchAsFlux(ParamBatchStmt stmt, boolean usePrepare);

    /**
     * <p>
     * This method is underlying api of {@link MultiStatement#executeBatchUpdate()} method.
     * </p>
     */
    Flux<ResultStates> multiStmtBatchUpdate(ParamMultiStmt stmt);

    BatchQuery multiStmtBatchQuery(ParamMultiStmt stmt);

    /**
     * <p>
     * This method is underlying api of {@link MultiStatement#executeBatchAsMulti()} method.
     * </p>
     */
    MultiResult multiStmtAsMulti(ParamMultiStmt stmt);

    /**
     * <p>
     * This method is underlying api of {@link MultiStatement#executeBatchAsFlux()} method.
     * </p>
     */
    OrderedFlux multiStmtAsFlux(ParamMultiStmt stmt);

    /**
     * <p>
     * This method is underlying api of {@link DatabaseSession#prepareStatement(String)} methods:
     * </p>
     */
    Mono<PrepareTask> prepare(String sql);


    Mono<Void> ping(int timeSeconds);

    Mono<Void> reset();

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
