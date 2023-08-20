package io.jdbd.vendor.result;

import io.jdbd.result.CurrentRow;
import io.jdbd.result.MultiResult;
import io.jdbd.result.ResultRow;
import io.jdbd.result.ResultStates;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;
import java.util.function.Function;

final class ErrorMultiResult implements MultiResult {

    private final Throwable error;

    ErrorMultiResult(Throwable error) {
        this.error = error;
    }

    @Override
    public Mono<ResultStates> nextUpdate() {
        return Mono.error(this.error);
    }


    @Override
    public <R> Publisher<R> nextQuery(Function<CurrentRow, R> function) {
        return null;
    }

    @Override
    public <R> Publisher<R> nextQuery(Function<CurrentRow, R> function, Consumer<ResultStates> consumer) {
        return null;
    }

    @Override
    public Flux<ResultRow> nextQuery() {
        return Flux.error(this.error);
    }


}
