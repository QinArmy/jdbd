package io.jdbd.result;

/**
 * <p>
 * This interface representing one row of query result (eg: SELECT command).
 * <br/>
 * <p>
 * The instance of this interface always after the instance of {@link ResultRowMeta} and before the instance of {@link ResultStates}
 * in same query result in the {@link OrderedFlux}.
 * <br/>
 * <p>
 * The {@link #getResultNo()} of this interface always return same value with {@link ResultRowMeta} in same query result.
 * See {@link #getRowMeta()}
 * <br/>
 *
 * @see ResultRowMeta
 * @see ResultStates
 * @since 1.0
 */
public interface ResultRow extends DataRow {


}
