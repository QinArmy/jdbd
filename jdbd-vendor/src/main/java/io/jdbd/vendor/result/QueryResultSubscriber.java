package io.jdbd.vendor.result;

import io.jdbd.result.NoMoreResultException;
import io.jdbd.result.ResultRow;
import io.jdbd.result.ResultState;
import io.jdbd.result.SingleResult;
import io.jdbd.stmt.ResultType;
import io.jdbd.vendor.task.ITaskAdjutant;
import io.jdbd.vendor.util.JdbdExceptions;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Consumer;

final class QueryResultSubscriber extends AbstractSingleResultSubscriber {

    static Flux<ResultRow> create(ITaskAdjutant adjutant, Consumer<ResultState> stateConsumer
            , Consumer<MultiResultSink> callback) {
        final Flux<SingleResult> flux = Flux.create(sink -> {
            try {
                callback.accept(MultiResultFluxSink.create(sink, adjutant));
            } catch (Throwable e) {
                sink.error(JdbdExceptions.wrap(e));
            }
        });
        return Flux.create(sink -> flux.subscribe(new QueryResultSubscriber(adjutant, sink, stateConsumer)));
    }

    private static final Logger LOG = LoggerFactory.getLogger(QueryResultSubscriber.class);

    private final FluxSink<ResultRow> sink;

    private final Consumer<ResultState> stateConsumer;

    private boolean receiveFirstResult;

    private QueryResultSubscriber(ITaskAdjutant adjutant, FluxSink<ResultRow> sink
            , Consumer<ResultState> stateConsumer) {
        super(adjutant);
        this.sink = sink;
        this.stateConsumer = stateConsumer;
    }

    @Override
    public final void onSubscribe(Subscription s) {
        s.request(Long.MAX_VALUE);
    }

    @Override
    public final void onNext(SingleResult singleResult) {
        if (!singleResult.isQuery()) {
            addError(ResultType.UPDATE);
            Mono.from(singleResult.receiveUpdate())
                    .doOnError(this::printUpstreamErrorAfterSkip)
                    .subscribe();
        } else if (singleResult.getIndex() == 0) {
            if (this.adjutant.inEventLoop()) {
                this.receiveFirstResult = true;
            } else {
                this.adjutant.execute(() -> this.receiveFirstResult = true);
            }
            Flux.from(singleResult.receiveQuery(this.stateConsumer))
                    .doOnNext(this.sink::next)
                    .doOnError(this::addUpstreamError)
                    .subscribe();
        } else {
            addError(ResultType.MULTI_RESULT);
        }
    }

    @Override
    public final void onError(Throwable t) {
        this.sink.error(t);
    }

    @Override
    public final void onComplete() {
        if (this.adjutant.inEventLoop()) {
            doCompleteInEventLoop();
        } else {
            this.adjutant.execute(this::doCompleteInEventLoop);
        }
    }


    @Override
    final ResultType getSubscribeType() {
        return ResultType.QUERY;
    }

    private void printUpstreamErrorAfterSkip(Throwable error) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Skip error type occur error.", error);
        }
    }

    private void doCompleteInEventLoop() {
        final List<Throwable> errorList = this.errorList;

        if (errorList == null || errorList.isEmpty()) {
            if (this.receiveFirstResult) {
                this.sink.complete();
            } else {
                this.sink.error(new NoMoreResultException("No receive any result from upstream."));
            }
        } else {
            this.sink.error(JdbdExceptions.createException(errorList));
        }

    }


}