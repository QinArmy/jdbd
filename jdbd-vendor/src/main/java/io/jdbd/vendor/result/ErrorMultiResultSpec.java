package io.jdbd.vendor.result;

import io.jdbd.lang.Nullable;
import io.jdbd.result.*;
import io.jdbd.vendor.util.JdbdExceptions;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;
import java.util.function.Function;

abstract class ErrorMultiResultSpec implements MultiResultSpec {

    static MultiResult errorMultiResult(Throwable error) {
        return new ErrorMultiResult(error);
    }

    static QueryResults errorBatchQuery(Throwable error) {
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
    public final <R, F extends Publisher<R>> F nextQuery(Function<CurrentRow, R> rowFunc,
                                                         Consumer<ResultStates> statesConsumer,
                                                         @Nullable Function<Publisher<R>, F> fluxFunc) {
        if (fluxFunc == null) {
            throw JdbdExceptions.fluxFuncIsNull();
        }
        final F flux;
        flux = fluxFunc.apply(nextQuery(rowFunc, statesConsumer));
        if (flux == null) {
            throw JdbdExceptions.fluxFuncReturnNull(fluxFunc);
        }
        return flux;
    }

    @Override
    public final OrderedFlux nextQueryFlux() {
        return MultiResults.fluxError(this.error);
    }

    @Override
    public final <F extends Publisher<ResultItem>> F nextQueryFlux(@Nullable Function<OrderedFlux, F> fluxFunc) {
        if (fluxFunc == null) {
            throw JdbdExceptions.fluxFuncIsNull();
        }
        final F flux;
        flux = fluxFunc.apply(nextQueryFlux());
        if (flux == null) {
            throw JdbdExceptions.fluxFuncReturnNull(fluxFunc);
        }
        return flux;
    }


    private static final class ErrorMultiResult extends ErrorMultiResultSpec implements MultiResult {

        private ErrorMultiResult(Throwable error) {
            super(error);
        }

        @Override
        public Mono<ResultStates> nextUpdate() {
            return Mono.error(this.error);
        }

        @Override
        public <M extends Publisher<ResultStates>> M nextUpdate(@Nullable Function<Publisher<ResultStates>, M> monoFunc) {
            if (monoFunc == null) {
                throw JdbdExceptions.monoFuncIsNull();
            }
            final M mono;
            mono = monoFunc.apply(nextUpdate());
            if (mono == null) {
                throw JdbdExceptions.monoFuncReturnNull(monoFunc);
            }
            return mono;
        }

    }//ErrorMultiResult


    private static final class ErrorBatchQuery extends ErrorMultiResultSpec implements QueryResults {

        private ErrorBatchQuery(Throwable error) {
            super(error);
        }


    }// ErrorBatchQuery


}
