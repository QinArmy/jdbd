package io.jdbd.vendor.result;

import io.jdbd.result.*;
import io.jdbd.vendor.task.ITaskAdjutant;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;
import java.util.function.Function;

public abstract class MultiResults {

    protected MultiResults() {
        throw new UnsupportedOperationException();
    }


    public static MultiResult error(Throwable e) {
        return new ErrorMultiResult(e);
    }


    public static OrderedFlux fluxError(Throwable error) {
        return new OrderedFluxError(error);
    }

    public static BatchQuery batchQueryError(Throwable error) {
        throw new UnsupportedOperationException();
    }

    public static Mono<ResultStates> update(Consumer<ResultSink> callback) {
        return UpdateResultSubscriber.create(callback);
    }

    public static <R> Flux<R> query(Function<CurrentRow, R> function, Consumer<ResultStates> stateConsumer,
                                    Consumer<ResultSink> callback) {
        return QueryResultSubscriber.create(function, stateConsumer, callback);
    }

    public static Flux<ResultStates> batchUpdate(Consumer<ResultSink> consumer) {
        return BatchUpdateResultSubscriber.create(consumer);
    }

    public static MultiResult asMulti(ITaskAdjutant adjutant, Consumer<ResultSink> consumer) {
        return MultiResultSubscriber.create(adjutant, consumer);
    }

    public static OrderedFlux asFlux(Consumer<ResultSink> consumer) {
        return FluxResult.create(consumer);
    }


}
