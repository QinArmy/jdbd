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
        return new ErrorMultiResult(e);
    }


    public static OrderedFlux fluxError(Throwable error) {
        return new OrderedFluxError(error);
    }

    public static OrderedFlux emptyAndError(Mono<Void> mono, Throwable error) {
        throw new UnsupportedOperationException();
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

    public static BatchQuery batchQuery(ITaskAdjutant adjutant, Consumer<ResultSink> consumer) {
        throw new UnsupportedOperationException();
    }

    public static BatchQuery deferBatchQuery(Mono<Void> empty, Supplier<BatchQuery> supplier) {
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
