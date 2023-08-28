package io.jdbd.vendor.result;


import io.jdbd.result.ResultItem;

/**
 * <p>
 * upstream must invoke {@link #complete()} or {@link #error(Throwable)} ,even if {@link #isCancelled()} return true.
 * </p>
 *
 * @since 1.0
 */
public interface ResultSink {

    boolean isCancelled();

    void next(ResultItem result);

    void error(Throwable e);

    void complete();

}
