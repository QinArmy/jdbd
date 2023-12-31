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

package io.jdbd.result;


import io.jdbd.statement.MultiResultStatement;
import io.jdbd.statement.StaticStatementSpec;
import org.reactivestreams.Publisher;

import java.util.List;
import java.util.function.Function;


/**
 * <p>
 * This interface representing multi-result.
 * </p>
 * <p>
 * <strong>NOTE</strong> : driver don't send message to database server before first subscribing.
 * <br/>
 * <p>This interface instance is crated by following methods:
 * <ul>
 *     <li>{@link MultiResultStatement#executeBatchAsMulti()}</li>
 *     <li>{@link StaticStatementSpec#executeBatchAsMulti(List)}</li>
 * </ul>
 *
 * @since 1.0
 */
public interface MultiResult extends MultiResultSpec {


    /**
     * <p>Subscribe next update result.
     *
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
     * <br/>
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
