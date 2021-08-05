package io.jdbd.vendor.result;

import io.jdbd.result.ResultState;


public interface MultiResultSink {

    boolean isCancelled();

    /**
     *
     */
    void error(Throwable e);

    /**
     *
     */
    void nextUpdate(ResultState resultState);

    /**
     *
     */
    QuerySink nextQuery();

    /**
     */
    void complete();

}