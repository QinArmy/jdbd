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

package io.jdbd.vendor.result;

import io.jdbd.JdbdException;
import io.jdbd.result.*;
import io.jdbd.vendor.task.ITaskAdjutant;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class MultiResults {

    protected MultiResults() {
        throw new UnsupportedOperationException();
    }


    public static MultiResult multiError(Throwable e) {
        return ErrorMultiResultSpec.errorMultiResult(e);
    }


    public static OrderedFlux fluxError(Throwable error) {
        return new OrderedFluxError(error);
    }

    public static OrderedFlux emptyAndError(Mono<Void> mono, Throwable error) {
        throw new UnsupportedOperationException();
    }

    public static QueryResults batchQueryError(Throwable error) {
        return ErrorMultiResultSpec.errorBatchQuery(error);
    }

    public static Mono<ResultStates> update(Consumer<ResultSink> callback) {
        return UpdateResultSubscriber.create(callback);
    }

    public static <R> Flux<R> query(Function<CurrentRow, R> function, Consumer<ResultStates> stateConsumer,
                                    Consumer<ResultSink> callback) {
        return QueryResultSubscriber.forQuery(function, stateConsumer, callback);
    }

    public static Flux<ResultStates> batchUpdate(Consumer<ResultSink> consumer) {
        return BatchUpdateResultSubscriber.create(consumer);
    }

    public static QueryResults batchQuery(ITaskAdjutant adjutant, Consumer<ResultSink> consumer) {
        return MultiResultSubscriber.batch(adjutant, consumer);
    }

    public static <R> Flux<R> batchQueryAsFlux(Function<CurrentRow, R> function, Consumer<ResultStates> stateConsumer,
                                               Consumer<ResultSink> callback) {
        return QueryResultSubscriber.forBatchQuery(function, stateConsumer, callback);
    }

    public static QueryResults deferBatchQuery(Mono<Void> empty, Supplier<QueryResults> supplier) {
        throw new UnsupportedOperationException();
    }

    public static OrderedFlux deferFlux(Mono<Void> empty, Supplier<OrderedFlux> supplier) {
        throw new UnsupportedOperationException();
    }

    public static MultiResult asMulti(ITaskAdjutant adjutant, Consumer<ResultSink> consumer) {
        return MultiResultSubscriber.multi(adjutant, consumer);
    }

    public static MultiResult deferMulti(Mono<Void> mono, Supplier<MultiResult> supplier) {
        throw new UnsupportedOperationException();
    }

    public static OrderedFlux asFlux(Consumer<ResultSink> consumer) {
        return FluxResult.create(consumer, true);
    }


    static JdbdException noReceiveAnyItem() {
        return new JdbdException("don't receive any result");
    }

}
