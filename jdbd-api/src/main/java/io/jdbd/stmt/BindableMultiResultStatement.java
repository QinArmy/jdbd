package io.jdbd.stmt;

import io.jdbd.lang.Nullable;
import io.jdbd.result.MultiResult;
import io.jdbd.result.Result;
import io.jdbd.result.ResultStates;
import org.reactivestreams.Publisher;

/**
 * <p>
 * This interface is base interface of below:
 *     <ul>
 *         <li>{@link BindableStatement}</li>
 *         <li>{@link PreparedStatement}</li>
 *         <li>{@link MultiStatement}</li>
 *     </ul>
 * </p>
 *
 * @see BindableStatement
 * @see PreparedStatement
 * @see MultiStatement
 */
public interface BindableMultiResultStatement extends Statement {

    void bind(int index, @Nullable Object nullable);

    Publisher<ResultStates> executeBatch();

    /**
     * @see BindableStatement#executeBatchAsMulti()
     * @see PreparedStatement#executeBatchAsMulti()
     * @see MultiStatement#executeBatchAsMulti()
     */
    MultiResult executeBatchAsMulti();

    /**
     * @see BindableStatement#executeBatchAsMulti()
     * @see PreparedStatement#executeBatchAsMulti()
     * @see MultiStatement#executeBatchAsMulti()
     */
    Publisher<Result> executeBatchAsFlux();

}
