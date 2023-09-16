package io.jdbd.result;


import io.jdbd.statement.StaticStatementSpec;
import org.reactivestreams.Publisher;

import java.util.function.Function;


/**
 * <p>
 * <strong>NOTE</strong> : driver don't send message to database server before first subscribing.
 * <br/>
 *
 * @since 1.0
 */
public interface MultiResult extends MultiResultSpec {


    /**
     * @return A Reactive Streams {@link Publisher} with rx operators that emits 0 to 1 elements
     * ,like {@code reactor.core.publisher.Mono}.
     * @throws NoMoreResultException emit when {@link MultiResult} end and no buffer.
     */
    Publisher<ResultStates> nextUpdate();

    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // s is a instance of {@link StaticStatementSpec}
     *              R mono  = monoFunc.apply(s.executeUpdate()) ;
     *
     *              // for example ,if use Project reactor , reactor.core.publisher.Mono
     *              s.executeUpdate(Mono::from)
     *                 .map(ResultStates::affectedRows)
     *
     *         </code>
     *     </pre>
     *<br/>
     *
     * @param monoFunc convertor function of Publisher ,for example : {@code reactor.core.publisher.Mono#from(org.reactivestreams.Publisher)}
     * @param <M>      M representing Mono that emit just one element or {@link Throwable}.
     * @return non-null Mono that emit just one element or {@link Throwable}.
     * @throws NullPointerException throw when
     *                              <ul>
     *                                  <li>monoFunc is null</li>
     *                                  <li>monoFunc return null</li>
     *                              </ul>
     * @see #nextUpdate()
     */
    <M extends Publisher<ResultStates>> M nextUpdate(Function<Publisher<ResultStates>, M> monoFunc);

}
