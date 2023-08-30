package io.jdbd.vendor.task;

import io.jdbd.meta.DataType;
import io.jdbd.result.*;
import io.jdbd.vendor.stmt.ParamBatchStmt;
import io.jdbd.vendor.stmt.ParamStmt;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.annotation.Nullable;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public interface PrepareTask {

    Mono<ResultStates> executeUpdate(ParamStmt stmt);

    <R> Flux<R> executeQuery(ParamStmt stmt, Function<CurrentRow, R> function, Consumer<ResultStates> consumer);

    OrderedFlux executeAsFlux(ParamStmt stmt);

    Flux<ResultStates> executeBatchUpdate(ParamBatchStmt stmt);

    BatchQuery executeBatchQuery(ParamBatchStmt stmt);

    MultiResult executeBatchAsMulti(ParamBatchStmt stmt);

    OrderedFlux executeBatchAsFlux(ParamBatchStmt stmt);

    List<? extends DataType> getParamTypes();


    ResultRowMeta getRowMeta();

    void closeOnBindError(Throwable error);

    String getSql();

    void abandonBind();

    void suspendTask();

    @Nullable
    Warning getWarning();

}
