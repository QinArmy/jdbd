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
import io.jdbd.result.CurrentRow;
import io.jdbd.result.ResultItem;
import io.jdbd.result.ResultRowMeta;
import io.jdbd.result.ResultStates;
import io.jdbd.vendor.JdbdCompositeException;
import io.jdbd.vendor.util.JdbdExceptions;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @see FluxResult
 */
@SuppressWarnings("all")
final class QueryResultSubscriber<R> implements Subscriber<ResultItem> {

    static <R> Flux<R> forQuery(final @Nullable Function<CurrentRow, R> function,
                                final @Nullable Consumer<ResultStates> stateConsumer,
                                final Consumer<ResultSink> callback) {
        return create(function, stateConsumer, callback, false);
    }

    static <R> Flux<R> forBatchQuery(final @Nullable Function<CurrentRow, R> function,
                                     final @Nullable Consumer<ResultStates> stateConsumer,
                                     final Consumer<ResultSink> callback) {
        return create(function, stateConsumer, callback, true);
    }

    private static <R> Flux<R> create(final @Nullable Function<CurrentRow, R> function,
                                      final @Nullable Consumer<ResultStates> stateConsumer,
                                      final Consumer<ResultSink> callback, final boolean batch) {
        final Flux<R> flux;
        if (function == null) {
            flux = Flux.error(JdbdExceptions.queryMapFuncIsNull());
        } else if (stateConsumer == null) {
            flux = Flux.error(JdbdExceptions.statesConsumerIsNull());
        } else {
            flux = Flux.create(sink -> {
                FluxResult.create(callback, false)
                        .subscribe(new QueryResultSubscriber<>(function, sink, stateConsumer, batch));
            });
        }
        return flux;
    }


    private final Function<CurrentRow, R> function;

    private final FluxSink<R> sink;

    private final Consumer<ResultStates> statesConsumer;

    private final boolean batch;

    private Subscription subscription;

    private Throwable error;

    private boolean disposable;

    private int itemCount = 0;

    private boolean receiveItem;

    private QueryResultSubscriber(Function<CurrentRow, R> function, FluxSink<R> sink,
                                  Consumer<ResultStates> statesConsumer, boolean batch) {
        this.function = function;
        this.sink = sink.onRequest(this::onRequrest);
        this.statesConsumer = statesConsumer;
        this.batch = batch;
    }


    @Override
    public void onSubscribe(Subscription s) {
        this.subscription = s;
        s.request(Long.MAX_VALUE);
    }

    @Override
    public void onNext(final ResultItem item) {
        // this method invoker in EventLoop
        if (this.disposable) {
            return;
        }

        if (!this.receiveItem) {
            this.receiveItem = true;
        }

        if (!this.batch && item.resultNo() != 1) {
            this.handleError(new NonQueryResultException("subscribe query result,but server response multi-result"));
        } else if (((++this.itemCount) & 31) == 0 && this.sink.isCancelled()) {
            this.disposable = true;
            this.subscription.cancel();
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
    public void onError(Throwable t) {
        // this method invoker in EventLoop
        final Throwable error = this.error;
        if (error == null) {
            this.sink.error(t);
        } else {
            this.sink.error(new JdbdCompositeException(Arrays.asList(t, error)));
        }
    }

    @Override
    public void onComplete() {
        // this method invoker in EventLoop
        final Throwable error = this.error;
        if (error != null) {
            this.sink.error(error);
        } else if (this.receiveItem) {
            this.sink.complete();
        } else {
            this.sink.error(new JdbdException("don't receive any result"));
        }
    }


    private void handleError(Throwable error) {
        this.disposable = true;
        this.error = error;
        this.subscription.cancel();
    }

    private void onRequrest(long n) {
        // currently,no-op
    }

}
