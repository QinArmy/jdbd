package io.jdbd.result;


public interface ResultStates extends Result {


    long getAffectedRows();

    long getInsertId();

    /**
     * @return success info(maybe contain warning info)
     */
    String getMessage();

    boolean hasMoreResult();

    boolean hasMoreFetch();

    default boolean hasColumn() {
        throw new UnsupportedOperationException();
    }

}