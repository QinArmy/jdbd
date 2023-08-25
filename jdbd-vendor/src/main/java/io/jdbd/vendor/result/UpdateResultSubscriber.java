package io.jdbd.vendor.result;

import io.jdbd.result.*;
import io.jdbd.vendor.ResultType;
import io.jdbd.vendor.util.JdbdExceptions;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoSink;

import java.util.List;
import java.util.function.Consumer;

/**
 * @see FluxResult
 */
final class UpdateResultSubscriber extends JdbdResultSubscriber {

    static Mono<ResultStates> create(Consumer<ResultSink> callback) {
        final OrderedFlux result = FluxResult.create(sink -> {
            try {
                callback.accept(sink);
            } catch (Throwable e) {
                sink.error(JdbdExceptions.wrap(e));
            }
        }, false);
        return Mono.create(sink -> result.subscribe(new UpdateResultSubscriber(sink)));
    }


    private final MonoSink<ResultStates> sink;

    private ResultStates state;

    private UpdateResultSubscriber(MonoSink<ResultStates> sink) {
        this.sink = sink;
    }

    @Override
    public final void onSubscribe(Subscription s) {
        this.subscription = s;
        s.request(Long.MAX_VALUE);
    }

    @Override
    public final boolean isCancelled() {
        return false;
    }

    @Override
    public final void onNext(final ResultItem result) {
        // this method invoker in EventLoop
        if (hasError()) {
            return;
        }
        if (result.getResultNo() != 0) {
            addSubscribeError(ResultType.MULTI_RESULT);
        } else if (result instanceof ResultRow) {
            addSubscribeError(ResultType.QUERY);
        } else if (result instanceof ResultStates) {
            final ResultStates state = (ResultStates) result;
            if (state.hasColumn()) {
                addSubscribeError(ResultType.QUERY);
            } else if (this.state == null) {
                this.state = state;
            } else {
                throw createDuplicationResultState(state);
            }
        } else {
            throw createUnknownTypeError(result);
        }
    }

    @Override
    public final void onError(Throwable t) {
        // this method invoker in EventLoop
        final List<Throwable> errorList = this.errorList;
        if (errorList == null || errorList.isEmpty()) {
            this.sink.error(t);
        } else {
            this.sink.error(JdbdExceptions.createException(errorList));
        }

    }

    @Override
    public final void onComplete() {
        // this method invoker in EventLoop
        final List<Throwable> errorList = this.errorList;
        if (errorList == null || errorList.isEmpty()) {
            final ResultStates state = this.state;
            if (state == null) {
                this.sink.error(new NoMoreResultException("No receive any result from upstream."));
            } else {
                this.sink.success(state);
            }
        } else {
            this.sink.error(JdbdExceptions.createException(errorList));
        }
    }


    @Override
    final ResultType getSubscribeType() {
        return ResultType.UPDATE;
    }


}
