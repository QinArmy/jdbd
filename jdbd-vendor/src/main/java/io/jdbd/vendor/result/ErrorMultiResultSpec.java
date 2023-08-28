package io.jdbd.vendor.result;

import io.jdbd.result.*;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;
import java.util.function.Function;

abstract class ErrorMultiResultSpec implements MultiResultSpec {

    static MultiResult errorMultiResult(Throwable error) {
        return new ErrorMultiResult(error);
    }

    static BatchQuery errorBatchQuery(Throwable error) {
        return new ErrorBatchQuery(error);
    }

    final Throwable error;

    private ErrorMultiResultSpec(Throwable error) {
        this.error = error;
    }

    @Override
    public final Flux<ResultRow> nextQuery() {
        return Flux.error(this.error);
    }

    @Override
    public final <R> Publisher<R> nextQuery(Function<CurrentRow, R> function) {
        return Flux.error(this.error);
    }

    @Override
    public final <R> Publisher<R> nextQuery(Function<CurrentRow, R> function, Consumer<ResultStates> consumer) {
        return Flux.error(this.error);
    }

    @Override
    public final OrderedFlux nextQueryFlux() {
        return MultiResults.fluxError(this.error);
    }


    private static final class ErrorMultiResult extends ErrorMultiResultSpec implements MultiResult {

        private ErrorMultiResult(Throwable error) {
            super(error);
        }

        @Override
        public Mono<ResultStates> nextUpdate() {
            return Mono.error(this.error);
        }


    }//ErrorMultiResult


    private static final class ErrorBatchQuery extends ErrorMultiResultSpec implements BatchQuery {

        private ErrorBatchQuery(Throwable error) {
            super(error);
        }


    }// ErrorBatchQuery


}
