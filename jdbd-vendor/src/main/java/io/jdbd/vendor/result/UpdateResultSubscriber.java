package io.jdbd.vendor.result;

import io.jdbd.JdbdException;
import io.jdbd.result.OrderedFlux;
import io.jdbd.result.ResultItem;
import io.jdbd.result.ResultStates;
import io.jdbd.vendor.util.JdbdExceptions;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoSink;

import java.util.function.Consumer;

/**
 * @see FluxResult
 */
@SuppressWarnings("all")
final class UpdateResultSubscriber implements Subscriber<ResultItem> {

    static Mono<ResultStates> create(final Consumer<ResultSink> callback) {
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

    private Subscription subscription;

    private ResultStates resultStates;

    private NonUpdateException error;


    private UpdateResultSubscriber(MonoSink<ResultStates> sink) {
        this.sink = sink;
    }

    @Override
    public void onSubscribe(Subscription s) {
        this.subscription = s;
        s.request(Long.MAX_VALUE);
    }

    @Override
    public void onNext(final ResultItem item) {
        // this method invoker in EventLoop
        if (this.error != null) {
            return;
        }
        if (!(item instanceof ResultStates) || ((ResultStates) item).hasColumn() || item.getResultNo() != 1) {
            this.error = new NonUpdateException("subscribe update,but server response non-update result");
            this.subscription.cancel();
            return;
        }
        this.resultStates = (ResultStates) item;
    }

    @Override
    public void onError(final Throwable t) {
        // this method invoker in EventLoop
        final NonUpdateException error = this.error;
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
        final NonUpdateException error = this.error;
        final ResultStates resultStates = this.resultStates;
        if (error != null) {
            this.sink.error(error);
        } else if (resultStates == null) {
            this.sink.error(new JdbdException("don't receive any result"));
        } else {
            this.sink.success(resultStates);
        }
    }


}
