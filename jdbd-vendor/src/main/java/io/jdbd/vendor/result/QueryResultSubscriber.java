package io.jdbd.vendor.result;

import io.jdbd.ResultStatusConsumerException;
import io.jdbd.result.NoMoreResultException;
import io.jdbd.result.Result;
import io.jdbd.result.ResultRow;
import io.jdbd.result.ResultState;
import io.jdbd.stmt.ResultType;
import io.jdbd.vendor.task.ITaskAdjutant;
import io.jdbd.vendor.util.JdbdExceptions;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.util.List;
import java.util.function.Consumer;

final class QueryResultSubscriber extends AbstractResultSubscriber<Result> {

    static Flux<ResultRow> create(ITaskAdjutant adjutant, Consumer<ResultState> stateConsumer
            , Consumer<FluxSink<Result>> callback) {
        final Flux<Result> flux = Flux.create(sink -> {
            try {
                callback.accept(sink);
            } catch (Throwable e) {
                sink.error(JdbdExceptions.wrap(e));
            }
        });
        return Flux.create(sink -> flux.subscribe(new QueryResultSubscriber(adjutant, sink, stateConsumer)));
    }


    private final FluxSink<ResultRow> sink;

    private final Consumer<ResultState> stateConsumer;

    private ResultState state;

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
    public final void onNext(final Result result) {
        if (result.getResultIndex() != 0) {
            addError(ResultType.MULTI_RESULT);
            return;
        }
        if (result instanceof ResultRow) {
            this.sink.next((ResultRow) result);
        } else if (result instanceof ResultState) {
            final ResultState state = (ResultState) result;
            if (!state.hasReturnColumn()) {
                addError(ResultType.UPDATE);
            } else if (this.adjutant.inEventLoop()) {
                if (!hasError()) {
                    this.state = state;
                }
            } else {
                this.adjutant.execute(() -> {
                    if (!hasError()) {
                        this.state = state;
                    }
                });
            }
        } else {
            throw createUnknownTypeError(result);
        }
    }

    @Override
    public final void onError(Throwable t) {
        this.sink.error(JdbdExceptions.wrap(t));
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

    private void doCompleteInEventLoop() {
        final List<Throwable> errorList = this.errorList;

        if (errorList == null || errorList.isEmpty()) {
            if (this.state == null) {
                this.sink.error(new NoMoreResultException("No receive query ResultState from upstream."));
            } else {
                Throwable error = null;
                try {
                    this.stateConsumer.accept(this.state);
                } catch (Throwable e) {
                    error = e;
                    this.sink.error(ResultStatusConsumerException.create(this.stateConsumer, e));
                }
                if (error == null) {
                    this.sink.complete();
                }
            }
        } else {
            this.sink.error(JdbdExceptions.createException(errorList));
        }

    }


}
