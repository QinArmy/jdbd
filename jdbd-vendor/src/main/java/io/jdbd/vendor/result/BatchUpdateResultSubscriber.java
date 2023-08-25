package io.jdbd.vendor.result;

import io.jdbd.result.*;
import io.jdbd.vendor.ResultType;
import io.jdbd.vendor.util.JdbdExceptions;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.util.List;
import java.util.function.Consumer;

/**
 * @see FluxResult
 */
final class BatchUpdateResultSubscriber extends JdbdResultSubscriber {

    static Flux<ResultStates> create(Consumer<ResultSink> callback) {
        final OrderedFlux result = FluxResult.create(sink -> {
            try {
                callback.accept(sink);
            } catch (Throwable e) {
                sink.error(JdbdExceptions.wrap(e));
            }
        }, false);
        return Flux.create(sink -> result.subscribe(new BatchUpdateResultSubscriber(sink)));
    }

    private final FluxSink<ResultStates> sink;

    private boolean receiveResult;

    private BatchUpdateResultSubscriber(FluxSink<ResultStates> sink) {
        this.sink = sink;
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
        if (result instanceof ResultRow) {
            addSubscribeError(ResultType.QUERY);
        } else if (result instanceof ResultStates) {
            final ResultStates state = (ResultStates) result;
            if (state.hasColumn()) {
                addSubscribeError(ResultType.QUERY);
            } else {
                if (!this.receiveResult) {
                    this.receiveResult = true;
                }
                this.sink.next(state);
            }
        } else {
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
        if (errorList != null && !errorList.isEmpty()) {
            this.sink.error(JdbdExceptions.createException(errorList));
        } else if (this.receiveResult) {
            this.sink.complete();
        } else {
            this.sink.error(new NoMoreResultException("No receive any result from upstream."));
        }

    }

    @Override
    ResultType getSubscribeType() {
        return ResultType.BATCH_UPDATE;
    }


}
