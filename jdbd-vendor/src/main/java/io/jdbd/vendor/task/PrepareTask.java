/*
 * Copyright 2023-2043 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

    <R> Flux<R> executeBatchQueryAsFlux(ParamBatchStmt stmt, Function<CurrentRow, R> function, Consumer<ResultStates> consumer);

    QueryResults executeBatchQuery(ParamBatchStmt stmt);

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
