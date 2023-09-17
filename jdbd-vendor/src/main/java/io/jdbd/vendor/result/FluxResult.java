package io.jdbd.vendor.result;

import io.jdbd.result.CurrentRow;
import io.jdbd.result.OrderedFlux;
import io.jdbd.result.ResultItem;
import io.jdbd.vendor.JdbdCompositeException;
import io.jdbd.vendor.util.JdbdExceptions;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.Exceptions;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.function.Consumer;
import java.util.function.LongConsumer;

/**
 * @see UpdateResultSubscriber
 * @see QueryResultSubscriber
 * @see BatchUpdateResultSubscriber
 * @see MultiResultSubscriber
 */
final class FluxResult implements OrderedFlux {

    static FluxResult create(Consumer<ResultSink> callBack, boolean applicationDeveloper) {
        return new FluxResult(callBack, applicationDeveloper);
    }

    private final Consumer<ResultSink> callBack;

    private final boolean applicationDeveloper;

    private FluxResult(Consumer<ResultSink> callBack, boolean applicationDeveloper) {
        this.callBack = callBack;
        this.applicationDeveloper = applicationDeveloper;
    }

    @Override
    public void subscribe(Subscriber<? super ResultItem> actual) {
        ResultSinkImpl sink = new ResultSinkImpl(actual, this.applicationDeveloper);
        actual.onSubscribe(sink.subscription);

        try {
            this.callBack.accept(sink);
        } catch (Throwable e) {
            Exceptions.throwIfJvmFatal(e);
            actual.onError(JdbdExceptions.wrap(e));
        }

    }

    private static final class ResultSinkImpl implements ResultSink {

        private static final Logger LOG = LoggerFactory.getLogger(ResultSinkImpl.class);

        private final Subscriber<? super ResultItem> subscriber;

        private final boolean applicationDeveloper;

        private final SubscriptionImpl subscription;

        private Throwable downstreamError;

        private ResultSinkImpl(Subscriber<? super ResultItem> subscriber, boolean applicationDeveloper) {
            this.subscriber = subscriber;
            this.applicationDeveloper = applicationDeveloper;
            this.subscription = new SubscriptionImpl();
        }


        @Override
        public void error(Throwable e) {
            // this method invoker in EventLoop
            try {
                final Throwable downstreamError = this.downstreamError;
                if (downstreamError == null) {
                    this.subscriber.onError(JdbdExceptions.wrapIfNonJvmFatal(e));
                } else {
                    this.subscriber.onError(new JdbdCompositeException(Arrays.asList(e, downstreamError)));
                }
            } catch (Throwable ex) {
                // never throw Throwable to upstream.
                LOG.error("downstream onError(Throwable) error ", e);
            }
        }

        @Override
        public void complete() {
            // this method invoker in EventLoop
            final Throwable downstreamError = this.downstreamError;
            try {

                if (downstreamError == null) {
                    this.subscriber.onComplete();
                } else {
                    this.subscriber.onError(JdbdExceptions.wrapIfNonJvmFatal(downstreamError));
                }
            } catch (Throwable e) {
                // never throw Throwable to upstream.
                if (downstreamError == null) {
                    this.error(e);
                } else {
                    LOG.error("downstream onComplete error ", e);
                }

            }
        }

        @Override
        public void onRequest(LongConsumer consumer) {
            this.subscription.consumer = consumer;
        }

        @Override
        public boolean isCancelled() {
            // this method invoker in EventLoop
            // upstream still invoke complete() or error() method
            return this.downstreamError != null || this.subscription.canceled != 0;
        }


        @Override
        public void next(final ResultItem result) {
            // this method invoker in EventLoop
            if (this.downstreamError != null) {
                return;
            }
            try {
                if (result instanceof CurrentRow && this.applicationDeveloper) {
                    this.subscriber.onNext(((CurrentRow) result).asResultRow());
                } else {
                    this.subscriber.onNext(result);
                }
            } catch (Throwable e) {
                // never throw Throwable to upstream.
                this.downstreamError = e;
            }

        }

    } // ResultSinkImpl


    private static final class SubscriptionImpl implements Subscription {

        private static final AtomicIntegerFieldUpdater<SubscriptionImpl> CANCELED =
                AtomicIntegerFieldUpdater.newUpdater(SubscriptionImpl.class, "canceled");

        private LongConsumer consumer;
        private volatile int canceled;

        private SubscriptionImpl() {
        }

        @Override
        public void request(long n) {
            //no-op ,because subscriber :
            // 1. io.jdbd.vendor.result.UpdateResultSubscriber
            // 2. io.jdbd.vendor.result.QueryResultSubscriber
            // 3. io.jdbd.vendor.result.BatchUpdateResultSubscriber
            // 4. io.jdbd.vendor.result.MultiResultSubscriber
            // always Long.MAX_VALUE
            // driver must clear connection chanel for next statement.
            final LongConsumer consumer = this.consumer;
            if (consumer != null) {
                consumer.accept(n);
            }
        }

        @Override
        public void cancel() {
            // this method invoker in EventLoop
            CANCELED.set(this, 1);
        }

    }


}
