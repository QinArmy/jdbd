package io.jdbd.vendor.result;

import io.jdbd.JdbdException;
import io.jdbd.result.OrderedFlux;
import io.jdbd.result.ResultItem;
import io.jdbd.result.ResultStates;
import io.jdbd.vendor.util.JdbdExceptions;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.util.function.Consumer;

/**
 * @see FluxResult
 */
@SuppressWarnings("all")
final class BatchUpdateResultSubscriber implements Subscriber<ResultItem> {

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

    private Subscription subscription;

    private NonBatchUpdateException error;

    private boolean receiveResult;

    private int itemCount = 0;

    private boolean disposable;

    private BatchUpdateResultSubscriber(FluxSink<ResultStates> sink) {
        this.sink = sink;
    }

    @Override
    public void onSubscribe(Subscription s) {
        this.subscription = s;
        s.request(Long.MAX_VALUE);
    }

    @Override
    public void onNext(final ResultItem result) {
        // this method invoker in EventLoop
        if (this.disposable) {
            return;
        }
        if (!(result instanceof ResultStates)) {
            this.disposable = true;
            this.error = new NonBatchUpdateException("subscribe batch update , but server response contain query result.");
            return;
        }

        if (!this.receiveResult) {
            this.receiveResult = true;
        }

        if ((((++this.itemCount) & 31) != 0)) {
            this.sink.next((ResultStates) result);
        } else if (this.sink.isCancelled()) {
            this.disposable = true;
            this.subscription.cancel();
        } else {
            this.sink.next((ResultStates) result);
        }


    }

    @Override
    public void onError(final Throwable t) {
        // this method invoker in EventLoop
        final NonBatchUpdateException error = this.error;
        if (error == null) {
            this.sink.error(t);
        } else {
            // subscribe error precedence
            this.sink.error(error);
        }

    }

    @Override
    public void onComplete() {
        // this method invoker in EventLoop
        final NonBatchUpdateException error = this.error;
        if (error != null) {
            this.sink.error(error);
        } else if (this.receiveResult) {
            this.sink.complete();
        } else {
            this.sink.error(new JdbdException("don't receive any result"));
        }

    }


}
