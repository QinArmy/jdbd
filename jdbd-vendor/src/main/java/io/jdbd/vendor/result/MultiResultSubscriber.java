package io.jdbd.vendor.result;

import io.jdbd.JdbdException;
import io.jdbd.result.*;
import io.jdbd.vendor.JdbdCompositeException;
import io.jdbd.vendor.ResultType;
import io.jdbd.vendor.SubscribeException;
import io.jdbd.vendor.protocol.DatabaseProtocol;
import io.jdbd.vendor.stmt.Stmts;
import io.jdbd.vendor.task.ITaskAdjutant;
import io.jdbd.vendor.util.JdbdExceptions;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoSink;
import reactor.util.concurrent.Queues;

import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @see FluxResult
 */
final class MultiResultSubscriber implements Subscriber<ResultItem> {


    static MultiResult multi(ITaskAdjutant adjutant, Consumer<ResultSink> callback) {
        final OrderedFlux result = FluxResult.create(sink -> {
            try {
                callback.accept(sink);
            } catch (Throwable e) {
                sink.error(JdbdExceptions.wrapIfNonJvmFatal(e));
            }
        }, false);
        return new JdbdMultiResult(new MultiResultSubscriber(result, adjutant));
    }

    static BatchQuery batch(final ITaskAdjutant adjutant, final Consumer<ResultSink> callback) {
        final OrderedFlux result = FluxResult.create(sink -> {
            try {
                callback.accept(sink);
            } catch (Throwable e) {
                sink.error(JdbdExceptions.wrapIfNonJvmFatal(e));
            }
        }, false);
        return new JdbdBatchQuery(new MultiResultSubscriber(result, adjutant));
    }

    private final OrderedFlux source;

    private final ITaskAdjutant adjutant;

    private Queue<ResultItem> resultItemQueue;

    private Queue<DownstreamSink> sinkQueue;

    private Subscription subscription;

    private DownstreamSink currentSink;

    private boolean done;

    private Throwable error;

    private boolean disposable;

    private boolean receiveItem;


    private MultiResultSubscriber(OrderedFlux source, ITaskAdjutant adjutant) {
        this.source = source;
        this.adjutant = adjutant;
        //this.resultItemQueue = Queues.<ResultItem>unbounded(20).get();
        // this.sinkQueue = Queues.<DownstreamSink>unbounded(10).get();
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

        if (!this.receiveItem) {
            this.receiveItem = true;
        }
        DownstreamSink currentSink = this.currentSink;
        if (currentSink == null) {
            Queue<DownstreamSink> sinkQueue = this.sinkQueue;
            if (sinkQueue == null) {
                this.sinkQueue = sinkQueue = Queues.<DownstreamSink>unbounded(10).get();
            }
            this.currentSink = currentSink = sinkQueue.poll();
        }

        if (currentSink == null) {

        }
    }

    @Override
    public void onError(Throwable t) {
        // this method invoker in EventLoop
    }

    @Override
    public void onComplete() {
        // this method invoker in EventLoop

    }


    private void drainError() {
        final List<Throwable> errorList = this.errorList;
        if (errorList == null || errorList.isEmpty()) {
            throw new IllegalStateException("No error");
        }
        final Queue<SinkWrapper> sinkQueue = this.sinkQueue;
        if (!sinkQueue.isEmpty()) {
            final JdbdException error = JdbdExceptions.createException(errorList);
            SinkWrapper sink;
            while ((sink = sinkQueue.poll()) != null) {
                sink.error(error);
            }
        }
        if (!this.resultItemQueue.isEmpty()) {
            this.resultItemQueue.clear();
        }
    }


    private void drainResult() {
        if (hasError()) {
            throw new IllegalStateException("has error ,reject drain result.");
        }
        final Queue<SinkWrapper> sinkQueue = this.sinkQueue;
        final Queue<ResultItem> resultQueue = this.resultItemQueue;

        SinkWrapper sink;
        ResultItem currentResult;
        ResultType nonExpectedType = null;

        while ((sink = sinkQueue.peek()) != null) {
            currentResult = resultQueue.poll();
            if (currentResult == null) {
                break;
            }
            if (currentResult instanceof ResultRow) {
                final FluxSink<ResultRow> fluxSink = sink.fluxSink;
                if (fluxSink == null) {
                    nonExpectedType = ResultType.QUERY;
                    break;
                } else {
                    fluxSink.next((ResultRow) currentResult);
                }
            } else if (currentResult instanceof ResultStates) {
                final ResultStates state = (ResultStates) currentResult;
                sinkQueue.poll();
                if (state.hasColumn()) {
                    final FluxSink<ResultRow> fluxSink = sink.fluxSink;
                    if (fluxSink == null) {
                        nonExpectedType = ResultType.QUERY;
                        break;
                    } else {
                        final Consumer<ResultStates> stateConsumer = sink.stateConsumer;
                        assert stateConsumer != null;
                        if (fluxSinkComplete(fluxSink, stateConsumer, state)) {
                            break;
                        }
                    }
                } else {
                    final MonoSink<ResultStates> monoSink = sink.updateSink;
                    if (monoSink == null) {
                        nonExpectedType = ResultType.UPDATE;
                        break;
                    } else {
                        monoSink.success(state);
                    }
                }
            } else {
                throw JdbdResultSubscriber.createUnknownTypeError(currentResult);
            }

        }

        if (nonExpectedType != null) {
            addMultiResultSubscribeError(nonExpectedType);
        }

    }

    private <R> Flux<R> addQuerySubscriber(Function<CurrentRow, R> function, Consumer<ResultStates> statesConsumer) {
        return Flux.empty();
    }

    private OrderedFlux addQueryFluxSubscriber() {
        return null;
    }

    private Mono<ResultStates> addUpdateSubscriber() {
        return Mono.empty();
    }

    private void addMultiResultSubscribeError(final ResultType nonExpectedType) {
        final List<Throwable> errorList = this.errorList;
        if (errorList != null) {
            for (Throwable e : errorList) {
                if (e instanceof SubscribeException) {
                    return;
                }
            }
        }
        switch (nonExpectedType) {
            case QUERY: {
                addError(new SubscribeException(ResultType.UPDATE, nonExpectedType));
            }
            break;
            case UPDATE: {
                addError(new SubscribeException(ResultType.QUERY, nonExpectedType));
            }
            break;
            default:
                throw new IllegalArgumentException(String.format("nonExpectedType[%s]", nonExpectedType));
        }

    }


    private void subscribeInEventLoop(SinkWrapper sink) {
        if (hasError()) {
            this.sinkQueue.add(sink);
            drainError();
        } else if (this.done && this.resultItemQueue.isEmpty()) {
            sink.error(new NoMoreResultException("No more result."));
        } else {
            this.sinkQueue.add(sink);
            if (this.subscription == null) {
                // first subscribe
                this.source.subscribe(this);
            } else {
                drainResult();
            }

        }
    }


    private static final class ReactorMultiResultImpl implements MultiResult {

        private final ITaskAdjutant adjutant;

        private final MultiResultSubscriber subscriber;

        private ReactorMultiResultImpl(ITaskAdjutant adjutant, Publisher<ResultItem> source) {
            this.adjutant = adjutant;
            this.subscriber = new MultiResultSubscriber(source);
        }

        @Override
        public Mono<ResultStates> nextUpdate() {
            return Mono.create(sink -> {
                if (this.adjutant.inEventLoop()) {
                    this.subscriber.subscribeInEventLoop(new SinkWrapper(sink));
                } else {
                    this.adjutant.execute(() -> this.subscriber.subscribeInEventLoop(new SinkWrapper(sink)));
                }
            });
        }


        @Override
        public Publisher<ResultRow> nextQuery() {
            return null;
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
        public OrderedFlux nextQueryFlux() {
            return null;
        }

    }// ReactorMultiResultImpl


    private static abstract class DownstreamSink {

        final int resultNo;

        private DownstreamSink(int resultNo) {
            this.resultNo = resultNo;
        }

        abstract boolean isCanceled();

        abstract void onNext(ResultItem item);

        abstract void onError(Throwable t);

        abstract void onComplete();


    }//DownstreamSink


    private static final class UpdateSink extends DownstreamSink {

        private final MonoSink<ResultStates> sink;

        private Throwable error;

        private ResultStates resultStates;

        private UpdateSink(int resultNo, MonoSink<ResultStates> sink) {
            super(resultNo);
            this.sink = sink;
        }

        @Override
        boolean isCanceled() {
            return this.error != null;
        }

        @Override
        void onNext(final ResultItem item) {
            // this method invoker in EventLoop
            if (this.error != null) {
                return;
            }
            if (item.getResultNo() != this.resultNo) {
                String m = String.format("error,expected resultNo[%s],but receive resultNo[%s]",
                        this.resultNo, item.getResultNo());
                this.error = new JdbdException(m);
            } else if (!(item instanceof ResultStates) || ((ResultStates) item).hasColumn()) {
                String m = String.format("error,resultNo[%s] expected update result,but receive query result.",
                        this.resultNo);
                this.error = new NonUpdateException(m);
            } else {
                this.resultStates = (ResultStates) item;
            }
        }

        @Override
        void onError(final Throwable t) {
            // this method invoker in EventLoop
            final Throwable error = this.error;
            if (error == null) {
                this.sink.error(t);
            } else {
                this.sink.error(new JdbdCompositeException(Arrays.asList(error, t)));
            }
        }

        @Override
        void onComplete() {
            // this method invoker in EventLoop
            final Throwable error = this.error;
            final ResultStates resultStates = this.resultStates;
            if (error != null) {
                this.sink.error(error);
            } else if (resultStates == null) {
                this.sink.error(MultiResults.noReceiveAnyItem());
            } else {
                this.sink.success(resultStates);
            }
        }


    }//UpdateSink

    private static final class QuerySink<R> extends DownstreamSink {

        private final FluxSink<R> sink;

        private final Function<CurrentRow, R> function;

        private final Consumer<ResultStates> statesConsumer;

        private Throwable error;

        private boolean disposable;

        private boolean receiveItem;

        private QuerySink(int resultNo, FluxSink<R> sink, Function<CurrentRow, R> function,
                          Consumer<ResultStates> statesConsumer) {
            super(resultNo);
            this.sink = sink;
            this.function = function;
            this.statesConsumer = statesConsumer;
        }

        @Override
        boolean isCanceled() {
            // this method invoker in EventLoop
            return this.disposable || this.sink.isCancelled();
        }

        @Override
        void onNext(final ResultItem item) {
            // this method invoker in EventLoop
            if (this.disposable) {
                return;
            }

            if (!this.receiveItem) {
                this.receiveItem = true;
            }

            if (item.getResultNo() != this.resultNo) {
                String m = String.format("error,expected resultNo[%s],but receive resultNo[%s]",
                        this.resultNo, item.getResultNo());
                this.handleError(new JdbdException(m));
            } else if (item instanceof CurrentRow) {
                final R r;
                try {
                    r = this.function.apply((CurrentRow) item);
                } catch (Throwable e) {
                    this.handleError(JdbdExceptions.queryMapFuncInvokeError(this.function, e));
                    return;
                }

                if (r instanceof CurrentRow) {
                    this.handleError(JdbdExceptions.queryMapFuncError(this.function));
                } else {
                    this.sink.next(r);
                }
            } else if (item instanceof ResultStates) {
                final Consumer<ResultStates> statesConsumer = this.statesConsumer;
                if (statesConsumer != Stmts.IGNORE_RESULT_STATES) {
                    try {
                        statesConsumer.accept((ResultStates) item);
                    } catch (Throwable e) {
                        this.handleError(JdbdExceptions.resultStatusConsumerInvokingError(statesConsumer, e));
                    }
                }
            } else if (!(item instanceof ResultRowMeta)) {
                // no bug ,never here
                String m = String.format("unknown %s %s", ResultStates.class.getName(), item);
                this.handleError(new JdbdException(m));
            }
        }

        @Override
        void onError(final Throwable t) {
            // this method invoker in EventLoop
            final Throwable error = this.error;
            if (error == null) {
                this.sink.error(t);
            } else {
                this.sink.error(new JdbdCompositeException(Arrays.asList(t, error)));
            }
        }

        @Override
        void onComplete() {
            // this method invoker in EventLoop
            final Throwable error = this.error;
            if (error != null) {
                this.sink.error(error);
            } else if (this.receiveItem) {
                this.sink.complete();
            } else {
                this.sink.error(MultiResults.noReceiveAnyItem());
            }
        }

        private void handleError(Throwable error) {
            this.disposable = true;
            this.error = error;
        }


    }// QuerySink


    private static final class OrderedFluxSink extends DownstreamSink {

        private final Subscriber<? super ResultItem> subscriber;

        private final OrderedSubscription subscription;

        private boolean disposable;

        private Throwable error;

        private boolean receiveItem;

        private OrderedFluxSink(int resultNo, Subscriber<? super ResultItem> subscriber,
                                OrderedSubscription subscription) {
            super(resultNo);
            this.subscriber = subscriber;
            this.subscription = subscription;
        }

        @Override
        boolean isCanceled() {
            // this method invoker in EventLoop
            return this.disposable || (this.disposable = this.subscription.canceled != 0);
        }

        @Override
        void onNext(final ResultItem item) {
            // this method invoker in EventLoop
            if (this.disposable) {
                return;
            }

            if (!this.receiveItem) {
                this.receiveItem = true;
            }
            if (item.getResultNo() != this.resultNo) {
                String m = String.format("error,expected resultNo[%s],but receive resultNo[%s]",
                        this.resultNo, item.getResultNo());
                this.handleError(new JdbdException(m));
            } else if (item instanceof CurrentRow) {
                this.subscriber.onNext(((CurrentRow) item).asResultRow());
            } else if (item instanceof ResultStates || item instanceof ResultRowMeta) {
                this.subscriber.onNext(item);
            } else {
                // no bug ,never here
                String m = String.format("unexpected %s %s", ResultItem.class.getName(), item);
                this.handleError(new JdbdException(m));
            }

        }

        @Override
        void onError(final Throwable t) {
            // this method invoker in EventLoop
            final Throwable error = this.error;
            if (error == null) {
                this.subscriber.onError(t);
            } else {
                this.subscriber.onError(new JdbdCompositeException(Arrays.asList(t, error)));
            }
        }

        @Override
        void onComplete() {
            final Throwable error = this.error;
            if (error != null) {
                this.subscriber.onError(error);
            } else if (this.receiveItem) {
                this.subscriber.onComplete();
            } else {
                this.subscriber.onError(MultiResults.noReceiveAnyItem());
            }
        }

        private void handleError(Throwable error) {
            this.disposable = true;
            this.error = error;
        }


    }// OrderedFluxSink

    private static final class OrderedSubscription implements Subscription {

        private static final AtomicIntegerFieldUpdater<OrderedSubscription> CANCELED =
                AtomicIntegerFieldUpdater.newUpdater(OrderedSubscription.class, "canceled");

        private volatile int canceled = 0;

        @Override
        public void request(long n) {
            //no-op,ignore
        }

        @Override
        public void cancel() {
            CANCELED.set(this, 1);
        }


    }// OrderedSubscription


    private static abstract class JdbdMultiResultSpec implements MultiResultSpec {

        final MultiResultSubscriber upstream;

        private JdbdMultiResultSpec(MultiResultSubscriber upstream) {
            this.upstream = upstream;
        }

        @Override
        public final Publisher<ResultRow> nextQuery() {
            return this.nextQuery(DatabaseProtocol.ROW_FUNC, Stmts.IGNORE_RESULT_STATES);
        }

        @Override
        public final <R> Publisher<R> nextQuery(Function<CurrentRow, R> function) {
            return this.nextQuery(function, Stmts.IGNORE_RESULT_STATES);
        }

        @Override
        public final <R> Publisher<R> nextQuery(Function<CurrentRow, R> function, Consumer<ResultStates> consumer) {
            return this.upstream.addQuerySubscriber(function, consumer);
        }

        @Override
        public final OrderedFlux nextQueryFlux() {
            return this.upstream.addQueryFluxSubscriber();
        }


    }// JdbdMultiResultSpec


    private static final class JdbdBatchQuery extends JdbdMultiResultSpec implements BatchQuery {

        private JdbdBatchQuery(MultiResultSubscriber upstream) {
            super(upstream);
        }


    }// JdbdBatchQuery


    private static final class JdbdMultiResult extends JdbdMultiResultSpec implements MultiResult {

        private JdbdMultiResult(MultiResultSubscriber upstream) {
            super(upstream);
        }

        @Override
        public Publisher<ResultStates> nextUpdate() {
            return this.upstream.addUpdateSubscriber();
        }

    }// JdbdMultiResult


}
