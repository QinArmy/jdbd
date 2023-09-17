package io.jdbd.vendor.result;


import io.jdbd.result.ResultItem;

import java.util.function.LongConsumer;

/**
 * <p>
 * upstream must invoke {@link #complete()} or {@link #error(Throwable)} ,even if {@link #isCancelled()} return true.
 * <br/>
 *
 * @since 1.0
 */
public interface ResultSink {

    /**
     * <p>
     * This method should be invoked just once by driver developer.
     * </p>
     *
     * @param consumer request number consumer
     */
    void onRequest(LongConsumer consumer);

    boolean isCancelled();

    void next(ResultItem result);

    void error(Throwable e);


    void complete();

}
