package io.jdbd.vendor.result;

import io.jdbd.lang.Nullable;
import io.jdbd.result.*;
import io.jdbd.vendor.ResultType;
import io.jdbd.vendor.util.JdbdExceptions;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @see FluxResult
 */
final class QueryResultSubscriber<R> extends JdbdResultSubscriber {

    static <R> Flux<R> create(final @Nullable Function<CurrentRow, R> function,
                              final @Nullable Consumer<ResultStates> stateConsumer,
                              final Consumer<ResultSink> callback) {
        final Flux<R> flux;
        if (function == null) {
            flux = Flux.error(JdbdExceptions.queryMapFuncIsNull());
        } else if (stateConsumer == null) {
            flux = Flux.error(JdbdExceptions.statesConsumerIsNull());
        } else {
            flux = Flux.create(sink -> FluxResult.create(callback, false)
                    .subscribe(new QueryResultSubscriber<>(function, sink, stateConsumer))
            );
        }
        return flux;
    }

    private final Function<CurrentRow, R> function;

    private final FluxSink<R> sink;

    private final Consumer<ResultStates> stateConsumer;

    private ResultStates state;

    private QueryResultSubscriber(Function<CurrentRow, R> function, FluxSink<R> sink,
                                  Consumer<ResultStates> stateConsumer) {
        this.function = function;
        this.sink = sink;
        this.stateConsumer = stateConsumer;
    }

    @Override
    public void onSubscribe(Subscription s) {
        this.subscription = s;
        s.request(Long.MAX_VALUE);
    }

    @Override
    public boolean isCancelled() {
        return this.sink.isCancelled();
    }

    @Override
    public void onNext(final ResultItem result) {
        // this method invoker in EventLoop
        if (hasError()) {
            return;
        }

        if (result.getResultNo() != 0) {
            addSubscribeError(ResultType.MULTI_RESULT);
        } else if (result instanceof CurrentRow) {
            try {
                final R row;
                row = this.function.apply((CurrentRow) result);
                if (row == null || row == result) {
                    this.addError(JdbdExceptions.queryMapFuncError(this.function));
                } else {
                    this.sink.next(row);
                }
            } catch (Throwable e) {
                this.addError(e);
            }
        } else if (result instanceof ResultStates) {
            final ResultStates state = (ResultStates) result;
            if (!state.hasColumn()) {
                addSubscribeError(ResultType.UPDATE);
            } else if (state.hasMoreFetch()) {
                try {
                    this.stateConsumer.accept(state);
                } catch (Throwable e) {
                    addError(ResultStatusConsumerException.create(this.stateConsumer, e));
                }
            } else if (this.state == null) {
                this.state = state;
            } else {
                throw createDuplicationResultState(state);
            }
        } else if (!(result instanceof ResultRowMeta)) {
            throw createUnknownTypeError(result);
        }
    }

    @Override
    public void onError(Throwable t) {
        // this method invoker in EventLoop
        this.sink.error(t);
    }

    @Override
    public void onComplete() {
        // this method invoker in EventLoop
        if (this.sink.isCancelled()) {
            return;
        }
        final List<Throwable> errorList = this.errorList;

        if (errorList == null || errorList.isEmpty()) {
            final ResultStates state = this.state;
            if (state == null) {
                this.sink.error(new NoMoreResultException("No receive terminator query ResultState from upstream."));
            } else {
                fluxSinkComplete(this.sink, stateConsumer, state);
            }
        } else {
            this.sink.error(JdbdExceptions.createException(errorList));
        }
    }

    @Override
    ResultType getSubscribeType() {
        return ResultType.QUERY;
    }


}
