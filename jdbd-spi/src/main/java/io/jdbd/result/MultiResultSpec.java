package io.jdbd.result;

import org.reactivestreams.Publisher;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * <p>
 * This interface representing multi-result of statement.
 * </p>
 * <p>
 * This interface is base interface of following :
 *     <ul>
 *         <li>{@link MultiResult}</li>
 *         <li>{@link BatchQuery}</li>
 *     </ul>
 * </p>
 * <p>
 * <strong>NOTE</strong> : driver don't send message to database server before first subscribing.
 * </p>
 *
 * @since 1.0
 */
public interface MultiResultSpec {

    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // s is instance of {@link MultiResultSpec}.
     *             s.nextQuery(CurrentRow::asResultRow,states -> {}) ; // ignore ResultStates instance.
     *         </code>
     *     </pre>
     * </p>
     *
     * @see #nextQuery(Function, Consumer)
     */
    Publisher<ResultRow> nextQuery();

    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // s is instance of {@link MultiResultSpec}.
     *             s.nextQuery(function,states -> {}) ; // ignore ResultStates instance.
     *         </code>
     *     </pre>
     * </p>
     *
     * @see #nextQuery(Function, Consumer)
     */
    <R> Publisher<R> nextQuery(Function<CurrentRow, R> function);


    <R> Publisher<R> nextQuery(Function<CurrentRow, R> function, Consumer<ResultStates> consumer);


    OrderedFlux nextQueryFlux();


}
