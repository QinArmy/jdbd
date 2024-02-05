/*
 * Copyright 2023-2043 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.jdbd.vendor.result;

import io.jdbd.JdbdException;
import io.jdbd.lang.Nullable;
import io.jdbd.result.*;
import io.jdbd.vendor.JdbdCompositeException;
import io.jdbd.vendor.task.ITaskAdjutant;
import io.jdbd.vendor.util.JdbdCollections;
import io.jdbd.vendor.util.JdbdExceptions;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoSink;

import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @see FluxResult
 */
@SuppressWarnings("all")
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

    static QueryResults batch(final ITaskAdjutant adjutant, final Consumer<ResultSink> callback) {
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

    private final AtomicBoolean subscribeUpstream = new AtomicBoolean(false);

    private final Queue<DownstreamSink> sinkQueue;

    private final Queue<ResultItem> resultItemQueue;

    private Subscription subscription;

    private DownstreamSink currentSink;

    private boolean done;

    private Throwable error;


    private int sinkNo = 1; // from 1

    private boolean disposable;

    private boolean receiveItem;


    private MultiResultSubscriber(OrderedFlux source, ITaskAdjutant adjutant) {
        this.source = source;
        this.adjutant = adjutant;
        this.sinkQueue = JdbdCollections.linkedList();
        this.resultItemQueue = JdbdCollections.linkedList();
    }


    @Override
    public void onSubscribe(Subscription s) {
        this.subscription = s;
        s.request(Long.MAX_VALUE);
    }

    @Override
    public void onNext(final ResultItem upstreamItem) {
        // this method invoker in EventLoop
        if (this.done || this.disposable) {
            return;
        }

        if (!this.receiveItem) {
            this.receiveItem = true;
        }

         drianQueueToDownstream();

        if (this.error != null) {
            return;
        }
        final DownstreamSink currentSink = this.currentSink;
        if (currentSink == null) {
            final ResultItem actualItem;
            if (upstreamItem instanceof CurrentRow) {
                actualItem = ((VendorDataRow) upstreamItem).copyCurrentRowIfNeed();
            } else {
                actualItem = upstreamItem;
            }
            if (!this.resultItemQueue.offer(actualItem)) {
                // no bug, never here
                throw new IllegalStateException("capacity error");
            }
        } else {
            currentSink.next(upstreamItem);
            if (upstreamItem instanceof ResultStates) {
                this.currentSink = null;
                currentSink.complete();

            }
        }

    }

    @Override
    public void onError(final Throwable t) {
        // this method invoker in EventLoop
        if (this.done) {
            return;
        }
        this.done = true;

        Throwable error = this.error;
        if (error == null) {
            drainError(JdbdExceptions.wrapIfNonJvmFatal(t));
        } else {
            this.error = error = new JdbdCompositeException(Arrays.asList(t, error));
            drainError(error);
        }
    }

    @Override
    public void onComplete() {
        // this method invoker in EventLoop
        if (this.done) {
            return;
        }
        this.done = true;
        final Throwable error = this.error;
        if (error != null) {
            drainError(JdbdExceptions.wrapIfNonJvmFatal(error));
        } else if (this.receiveItem) {
            drianQueueToDownstream();
        } else {
            drainError(MultiResults.noReceiveAnyItem());
        }
    }

    private void drianQueueToDownstream() {
        final Queue<ResultItem> resultItemQueue = this.resultItemQueue;
        DownstreamSink currentSink = this.currentSink;
        if (currentSink == null) {
            this.currentSink = currentSink = this.sinkQueue.poll();
        }
        // drain queue
        for (ResultItem queueItem; currentSink != null && (queueItem = resultItemQueue.poll()) != null; ) {

            if (queueItem.resultNo() != currentSink.resultNo) {
                String m = String.format("error,expected resultNo[%s],but receive resultNo[%s]",
                        currentSink.resultNo, queueItem.resultNo());
                this.handleError(new JdbdException(m));
                break;
            }

            currentSink.next(queueItem);
            if (queueItem instanceof ResultStates) {
                currentSink.complete();
                this.currentSink = currentSink = this.sinkQueue.poll();
            }

        }
    }


    private void handleError(Throwable error) {
        this.disposable = true;
        this.error = error;

        this.subscription.cancel();
        this.resultItemQueue.clear();
    }


    private void drainError(final Throwable error) {
        DownstreamSink currentSink = this.currentSink;
        this.currentSink = null;

        if (currentSink != null) {
            currentSink.error(error);
        }

        while ((currentSink = this.sinkQueue.poll()) != null) {
            currentSink.error(error);
        }

        this.resultItemQueue.clear();

    }


    private <R> Flux<R> addQuerySubscriber(final Function<CurrentRow, R> function,
                                           final Consumer<ResultStates> statesConsumer) {

        return Flux.create(sink -> {
            if (this.adjutant.inEventLoop()) {
                addQuerySubscriberInEventLoop(sink, function, statesConsumer);
            } else {
                this.adjutant.execute(() -> addQuerySubscriberInEventLoop(sink, function, statesConsumer));
            }
        });
    }

    private OrderedFlux addQueryFluxSubscriber() {
        final boolean applicationDeveloper = true;
        return FluxResult.create(sink -> {
            if (this.adjutant.inEventLoop()) {
                addQueryFluxSubscriberInEventLoop(sink);
            } else {
                this.adjutant.execute(() -> addQueryFluxSubscriberInEventLoop(sink));
            }
        }, applicationDeveloper); // application developer subscribing ,so auto invoke io.jdbd.result.CurrentRow.asResultRow().
    }

    private Mono<ResultStates> addUpdateSubscriber() {
        return Mono.create(sink -> {
            if (this.adjutant.inEventLoop()) {
                addUpdateSubscriberInEventLoop(sink);
            } else {
                this.adjutant.execute(() -> addUpdateSubscriberInEventLoop(sink));
            }
        });
    }


    /**
     * @see #addQuerySubscriber(Function, Consumer)
     */
    private <R> void addQuerySubscriberInEventLoop(FluxSink<R> sink, @Nullable Function<CurrentRow, R> func,
                                                   @Nullable Consumer<ResultStates> consumer) {
        final int nextSinkNo = this.sinkNo++;
        final Throwable error;
        if (!this.done) {
            if (this.sinkQueue.size() == 0) {
                subscribeUpstreamIfNeedInEventLoop();
            }
            this.sinkQueue.offer(new QuerySink<>(nextSinkNo, sink, func, consumer));
        } else if ((error = this.error) != null) {
            sink.error(error);
        } else if (this.resultItemQueue.isEmpty()) {
            String m = String.format("expected resultNo[%s],but no more result.", nextSinkNo);
            sink.error(new NoMoreResultException(m));
        } else {
            this.sinkQueue.offer(new QuerySink<>(nextSinkNo, sink, func, consumer));
            this.drianQueueToDownstream();
        }
    }

    /**
     * @see #addUpdateSubscriber()
     */
    private void addUpdateSubscriberInEventLoop(MonoSink<ResultStates> sink) {
        final int nextSinkNo = this.sinkNo++;
        final Throwable error;
        if (!this.done) {
            if (this.sinkQueue.size() == 0) {
                subscribeUpstreamIfNeedInEventLoop();
            }
            this.sinkQueue.offer(new UpdateSink(nextSinkNo, sink));
        } else if ((error = this.error) != null) {
            sink.error(error);
        } else if (this.resultItemQueue.isEmpty()) {
            String m = String.format("expected resultNo[%s],but no more result.", nextSinkNo);
            sink.error(new NoMoreResultException(m));
        } else {
            this.sinkQueue.offer(new UpdateSink(nextSinkNo, sink));
            this.drianQueueToDownstream();
        }
    }

    /**
     * @see #addQueryFluxSubscriber()
     */
    private void addQueryFluxSubscriberInEventLoop(ResultSink sink) {
        final int nextSinkNo = this.sinkNo++;
        final Throwable error;
        if (!this.done) {
            if (this.sinkQueue.size() == 0) {
                subscribeUpstreamIfNeedInEventLoop();
            }
            this.sinkQueue.offer(new OrderedFluxSink(nextSinkNo, sink));
        } else if ((error = this.error) != null) {
            sink.error(error);
        } else if (this.resultItemQueue.isEmpty()) {
            String m = String.format("expected resultNo[%s],but no more result.", nextSinkNo);
            sink.error(new NoMoreResultException(m));
        } else {
            this.sinkQueue.offer(new OrderedFluxSink(nextSinkNo, sink));
            this.drianQueueToDownstream();
        }
    }

    private void subscribeUpstreamIfNeedInEventLoop() {
        if (this.subscribeUpstream.compareAndSet(false, true)) {
            this.source.subscribe(this);
        }
    }


    private static abstract class DownstreamSink {

        final int resultNo;

        private DownstreamSink(int resultNo) {
            this.resultNo = resultNo;
        }

        abstract void next(ResultItem item);

        abstract void error(Throwable t);

        abstract void complete();


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
        void next(final ResultItem item) {
            // this method invoker in EventLoop
            if (this.error != null) {
                return;
            }
            if (item.resultNo() != this.resultNo) {
                String m = String.format("error,expected resultNo[%s],but receive resultNo[%s]",
                        this.resultNo, item.resultNo());
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
        void error(final Throwable t) {
            // this method invoker in EventLoop
            final Throwable error = this.error;
            if (error == null) {
                this.sink.error(t);
            } else {
                this.sink.error(new JdbdCompositeException(Arrays.asList(error, t)));
            }
        }

        @Override
        void complete() {
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

        private QuerySink(int resultNo, FluxSink<R> sink, @Nullable Function<CurrentRow, R> function,
                          @Nullable Consumer<ResultStates> statesConsumer) {
            super(resultNo);
            this.sink = sink.onRequest(this::onRequrest);
            this.function = function;
            this.statesConsumer = statesConsumer;

            if (function == null) {
                this.disposable = true;
                this.error = JdbdExceptions.queryMapFuncIsNull();
            } else if (statesConsumer == null) {
                this.disposable = true;
                this.error = JdbdExceptions.statesConsumerIsNull();
            }
        }


        @Override
        void next(final ResultItem item) {
            // this method invoker in EventLoop
            if (this.disposable) {
                return;
            }

            if (!this.receiveItem) {
                this.receiveItem = true;
            }

            if (item.resultNo() != this.resultNo) {
                String m = String.format("error,expected resultNo[%s],but receive resultNo[%s]",
                        this.resultNo, item.resultNo());
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
                if (statesConsumer != ResultStates.IGNORE_STATES) {
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
        void error(final Throwable t) {
            // this method invoker in EventLoop
            final Throwable error = this.error;
            if (error == null) {
                this.sink.error(t);
            } else {
                this.sink.error(new JdbdCompositeException(Arrays.asList(t, error)));
            }
        }

        @Override
        void complete() {
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

        private void onRequrest(long n) {
            // currently,no-op
        }


    }// QuerySink


    private static final class OrderedFluxSink extends DownstreamSink {

        private final ResultSink sink;

        private boolean disposable;

        private Throwable error;

        private boolean receiveItem;


        private OrderedFluxSink(int resultNo, ResultSink sink) {
            super(resultNo);
            this.sink = sink;
        }

        @Override
        void next(final ResultItem item) {
            // this method invoker in EventLoop
            if (this.disposable) {
                return;
            }

            if (!this.receiveItem) {
                this.receiveItem = true;
            }
            if (item.resultNo() != this.resultNo) {
                String m = String.format("error,expected resultNo[%s],but receive resultNo[%s]",
                        this.resultNo, item.resultNo());
                this.handleError(new JdbdException(m));
            } else {
                this.sink.next(item);
            }

        }

        @Override
        void error(final Throwable t) {
            // this method invoker in EventLoop
            final Throwable error = this.error;
            if (error == null) {
                this.sink.error(t);
            } else {
                this.sink.error(new JdbdCompositeException(Arrays.asList(t, error)));
            }
        }

        @Override
        void complete() {
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


    }// OrderedFluxSink


    private static abstract class JdbdMultiResultSpec implements MultiResultSpec {

        final MultiResultSubscriber upstream;

        private JdbdMultiResultSpec(MultiResultSubscriber upstream) {
            this.upstream = upstream;
        }

        @Override
        public final Publisher<ResultRow> nextQuery() {
            return this.nextQuery(CurrentRow.AS_RESULT_ROW, ResultStates.IGNORE_STATES);
        }

        @Override
        public final <R> Publisher<R> nextQuery(Function<CurrentRow, R> function) {
            return this.nextQuery(function, ResultStates.IGNORE_STATES);
        }

        @Override
        public final <R> Publisher<R> nextQuery(Function<CurrentRow, R> function, Consumer<ResultStates> consumer) {
            return this.upstream.addQuerySubscriber(function, consumer);
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
            return this.upstream.addQueryFluxSubscriber();
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


    }// JdbdMultiResultSpec


    private static final class JdbdBatchQuery extends JdbdMultiResultSpec implements QueryResults {

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


    }// JdbdMultiResult


}
