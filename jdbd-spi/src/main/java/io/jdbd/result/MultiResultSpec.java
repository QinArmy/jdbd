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

import io.jdbd.JdbdException;
import io.jdbd.statement.MultiResultStatement;
import io.jdbd.statement.StaticStatementSpec;
import org.reactivestreams.Publisher;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * <p>
 * This interface representing multi-result of statement.
 * <br/>
 * <p>
 * This interface is base interface of following :
 *     <ul>
 *         <li>{@link MultiResult}</li>
 *         <li>{@link QueryResults}</li>
 *     </ul>
 * <br/>
 * <p>
 * <strong>NOTE</strong> : driver don't send message to database server before first subscribing.
 * <br/>
 *<p>This interface instance is crated by following methods:
 * <ul>
 *     <li>{@link MultiResultStatement#executeBatchQuery()}</li>
 *     <li>{@link MultiResultStatement#executeBatchAsMulti()}</li>
 *     <li>{@link StaticStatementSpec#executeBatchQuery(List)}</li>
 *     <li>{@link StaticStatementSpec#executeBatchAsMulti(List)}</li>
 * </ul>
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
     * <br/>
     *
     * @return see {@link #nextQuery(Function, Consumer)}
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
     * <br/>
     *
     * @param rowFunc see {@link #nextQuery(Function, Consumer)}
     * @param <R>     see {@link #nextQuery(Function, Consumer)}
     * @return see {@link #nextQuery(Function, Consumer)}
     * @see #nextQuery(Function, Consumer)
     */
    <R> Publisher<R> nextQuery(Function<CurrentRow, R> rowFunc);

    /**
     * Subscribe next query result,the result consist of :
     * <ol>
     *     <li>one {@link ResultRowMeta}</li>
     *     <li>0-N data row,the {@link DataRow#getResultNo()} return same with {@link ResultRowMeta#getResultNo()}</li>
     *     <li>one {@link ResultStates},the {@link ResultStates#hasColumn()} always return true,the {@link ResultStates#getResultNo()} return same with {@link ResultRowMeta#getResultNo()}</li>
     * </ol>
     * To avoid creating {@link ResultRow} instance for improving performance ,driver create just one {@link CurrentRow} instance for this result<br/>
     * and wrap {@link ResultRowMeta} to {@link CurrentRow#getRowMeta()},and {@link ResultStates} is optional, if you don't need.
     * <p><strong>NOTE</strong>: if you don't subscribe before emit {@link ResultRowMeta},then driver will cache {@link ResultItem}s of this result to {@link java.util.Queue}.
     * <p>more info see {@link MultiResultStatement#executeBatchAsMulti()} and {@link MultiResultStatement#executeBatchQuery()}
     *
     * @param rowFunc        current row map function.Using rowFunc to avoid create {@link ResultRow} instance for improving performance.<br/>
     *                       <strong>NOTE</strong>:
     *                       <ul>
     *                           <li>rowFunc couldn't return {@link CurrentRow} instance.</li>
     *                           <li>you couldn't invoke the block method of {@link Publisher} in rowFunc,or emit {@link Throwable}. For example :
     *                                  <ul>
     *                                      <li>{@code reactor.core.publisher.Flux#blockLast()}</li>
     *                                      <li>{@code reactor.core.publisher.Flux#blockFirst()}</li>
     *                                  </ul>
     *                           </li>
     *                           <li>driver will invoke rowFunc in an ordered / serial fashion.</li>
     *                       </ul>
     * @param statesConsumer a consumer to receive the {@link ResultStates},statesConsumer will be invoked just once by driver.<br/>
     *                       <strong>NOTE</strong>: driver will invoke statesConsumer in an ordered / serial fashion.
     * @param <R>            the row java type,it is returned by rowFunc.
     * @return the {@link Publisher} emit 0-N element or {@link Throwable}, Like {@code reactor.core.publisher.Flux} .
     * @throws JdbdException        emmit(not throw) when
     *                              <ul>
     *                                  <li>you reuse appropriate {@link io.jdbd.statement.Statement} instance</li>
     *                                  <li>param bind error</li>
     *                                  <li>the java type of value of appropriate dataType isn't supported by the implementation of this method ,for example : {@link io.jdbd.meta.JdbdType#BIGINT} bind {@link io.jdbd.type.Clob}</li>
     *                                  <li>rowFunc throw {@link Throwable}</li>
     *                                  <li>statesConsumer throw {@link Throwable}</li>
     *                                  <li>sql error</li>
     *                                  <li>session have closed ,see {@link io.jdbd.session.SessionCloseException}</li>
     *                                  <li>server response error ,see {@link ServerException}</li>
     *                                  <li>current result result not match,e.g: current result is update result</li>
     *                                  <li>rowFunc return {@link CurrentRow} instance</li>
     *                              </ul>
     * @throws NullPointerException emit(not throw) when
     *                              <ul>
     *                                  <li>rowFunc is null</li>
     *                                  <li>statesConsumer is null</li>
     *                              </ul>
     */
    <R> Publisher<R> nextQuery(Function<CurrentRow, R> rowFunc, Consumer<ResultStates> statesConsumer);

    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // s is a instance of {@link MultiResultSpec}
     *              R flux  = fluxFunc.apply(s.nextQuery(rowFunc,statesConsumer)) ;
     *
     *              // for example ,if use Project reactor , reactor.core.publisher.Flux
     *              s.nextQuery(rowFunc,statesConsumer,Flux::from)
     *                 .collectList()
     *
     *         </code>
     *     </pre>
     * <br/>
     *
     * @param rowFunc        see {@link #nextQuery(Function, Consumer)}
     * @param statesConsumer see {@link #nextQuery(Function, Consumer)}
     * @param fluxFunc       convertor function of Publisher ,for example : {@code reactor.core.publisher.Flux#from(org.reactivestreams.Publisher)}
     * @param <F>            F representing Flux that emit 0-N element or {@link Throwable}.
     * @param <R>            row java type
     * @return non-null Flux that emit just one element or {@link Throwable}.
     * @throws NullPointerException throw when
     *                              <ul>
     *                                  <li>fluxFunc is null</li>
     *                                  <li>fluxFunc return null</li>
     *                              </ul>
     * @see #nextQuery(Function, Consumer)
     */
    <R, F extends Publisher<R>> F nextQuery(Function<CurrentRow, R> rowFunc, Consumer<ResultStates> statesConsumer, Function<Publisher<R>, F> fluxFunc);

    /**
     * <p>Subscribe next query result.
     * <p>more info see:
     * <ul>
     *     <li>{@link OrderedFlux}</li>
     *     <li>{@link MultiResultStatement#executeBatchAsMulti()}</li>
     *     <li>{@link MultiResultStatement#executeBatchQuery()}</li>
     * </ul>
     *
     * @return {@link OrderedFlux} that emit 2-N {@link ResultItem} or {@link Throwable}
     * @throws JdbdException emmit(not throw) when
     *                       <ul>
     *                           <li>you reuse appropriate {@link io.jdbd.statement.Statement} instance</li>
     *                           <li>param bind error</li>
     *                           <li>the java type of value of appropriate dataType isn't supported by the implementation of this method ,for example : {@link io.jdbd.meta.JdbdType#BIGINT} bind {@link io.jdbd.type.Clob}</li>
     *                           <li>sql error</li>
     *                           <li>session have closed ,see {@link io.jdbd.session.SessionCloseException}</li>
     *                           <li>server response error ,see {@link ServerException}</li>
     *                           <li>current result result not match,e.g: current result is update result</li>
     *                       </ul>
     */
    OrderedFlux nextQueryFlux();

    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // s is a instance of {@link StaticStatementSpec}
     *              R flux  = fluxFunc.apply(s.nextQueryFlux()) ;
     *
     *              // for example ,if use Project reactor , reactor.core.publisher.Flux
     *              s.nextQueryFlux(Flux::from)
     *                 .collectList()
     *
     *         </code>
     *     </pre>
     * <br/>
     *
     * @param fluxFunc convertor function of Publisher ,for example : {@code reactor.core.publisher.Flux#from(org.reactivestreams.Publisher)}
     * @param <F>      F representing Flux that emit 0-N element or {@link Throwable}.
     * @return non-null Flux that emit just one element or {@link Throwable}.
     * @throws NullPointerException throw when
     *                              <ul>
     *                                  <li>fluxFunc is null</li>
     *                                  <li>fluxFunc return null</li>
     *                              </ul>
     * @see #nextQueryFlux()
     */
    <F extends Publisher<ResultItem>> F nextQueryFlux(Function<OrderedFlux, F> fluxFunc);


}
