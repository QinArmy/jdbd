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

package io.jdbd.statement;


import io.jdbd.JdbdException;
import io.jdbd.lang.Nullable;
import io.jdbd.meta.DataType;
import io.jdbd.result.*;
import io.jdbd.session.ChunkOption;
import io.jdbd.session.Option;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * <p>
 * This interface representing the single sql statement that support sql parameter placeholder({@code ?}) .
 * <br/>
 * <p>
 * This interface is base interface of following:
 *     <ul>
 *         <li>{@link BindStatement}</li>
 *         <li>{@link PreparedStatement}</li>
 *     </ul>
 * <br/>
 *
 * @see BindStatement
 * @see PreparedStatement
 */
public interface BindSingleStatement extends ParametrizedStatement, MultiResultStatement {


    /**
     * {@inheritDoc }
     */
    @Override
    BindSingleStatement bind(int indexBasedZero, DataType dataType, @Nullable Object value) throws JdbdException;

    /**
     * {@inheritDoc }
     */
    @Override
    BindSingleStatement bindStmtVar(String name, DataType dataType, @Nullable Object value) throws JdbdException;


    /**
     * <p>
     * Add current parameter group to batch item list.
     * <p>This method is invoked after {@link #bind(int, DataType, Object)},if no parameter ,you can directly invoke this method.
     * <p>This method is provide only for following methods:
     * <ul>
     *     <li>{@link #executeBatchUpdate()}</li>
     *     <li>{@link #executeBatchQuery()}</li>
     *     <li>{@link #executeBatchAsMulti()}</li>
     *     <li>{@link #executeBatchAsFlux()}</li>
     * </ul>
     *
     * @return <strong>this</strong>
     * @throws JdbdException emit(or throw)
     *                       <ul>
     *                           <li>you reuse this {@link BindSingleStatement} instance</li>
     *                           <li>parameter group size not match</li>
     *                       </ul>
     */
    BindSingleStatement addBatch() throws JdbdException;

    /**
     * Executes the given SQL statement thant can producer just one update result.
     * for example :
     * <ul>
     *     <li>INSERT</li>
     *     <li>UPDATE</li>
     *     <li>DELETE</li>
     *     <li>CREATE TABLE</li>
     *     <li>CALL Stored procedures that just produce one update result and no out parameter.</li>
     * </ul>
     * <p>The result is a {@link ResultStates} instance whose {@link ResultStates#hasColumn()} always return false and {@link ResultStates#resultNo()} always return 1 .
     * <p><strong>NOTE</strong> : driver don't send message to database server before subscribing. Driver developer must guarantee this feature.
     *
     * @return the {@link Publisher} emit just one {@link ResultStates} or {@link Throwable}, Like {@code reactor.core.publisher.Mono} .
     * @throws JdbdException emmit(not throw) when
     *                       <ul>
     *                           <li>you reuse this {@link BindSingleStatement} instance</li>
     *                           <li>param bind error</li>
     *                           <li>the java type of value of appropriate dataType isn't supported by the implementation of this method ,for example : {@link io.jdbd.meta.JdbdType#BIGINT} bind {@link io.jdbd.type.Clob}</li>
     *                           <li>sql error</li>
     *                           <li>session have closed ,see {@link io.jdbd.session.SessionCloseException}</li>
     *                           <li>server response error ,see {@link ServerException}</li>
     *                           <li>server response result not match,e.g: response multi-result,or query result</li>
     *                       </ul>
     */
    Publisher<ResultStates> executeUpdate();

    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // statement is a instance of {@link BindSingleStatement}
     *              R mono  = monoFunc.apply(statement.executeUpdate()) ;
     *
     *              // for example ,if use Project reactor , reactor.core.publisher.Mono
     *              statement.executeUpdate(Mono::from)
     *                 .map(ResultStates::affectedRows)
     *
     *         </code>
     *     </pre>
     * <br/>
     *
     * @param monoFunc convertor function of Publisher ,for example : {@code reactor.core.publisher.Mono#from(org.reactivestreams.Publisher)}
     * @param <M>      M representing Mono that emit just one element or {@link Throwable}.
     * @return Mono that emit just one element or {@link Throwable}.
     * @see #executeUpdate()
     */
    <M extends Publisher<ResultStates>> M executeUpdate(Function<Publisher<ResultStates>, M> monoFunc);

    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // stmt is instance of {@link BindSingleStatement}.
     *             stmt.executeQuery(CurrentRow::asResultRow,states -> {}) ; // ignore ResultStates instance.
     *         </code>
     *     </pre>
     * <br/>
     *
     * @return see {@link #executeQuery(Function, Consumer)}
     * @see #executeQuery(Function, Consumer)
     */
    Publisher<ResultRow> executeQuery();


    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // stmt is instance of {@link BindSingleStatement}.
     *             stmt.executeQuery(function,states -> {}) ; // ignore ResultStates instance.
     *         </code>
     *     </pre>
     * <br/>
     *
     * @param rowFunc see {@link #executeQuery(Function, Consumer)}
     * @param <R>     see {@link #executeQuery(Function, Consumer)}
     * @return see {@link #executeQuery(Function, Consumer)}
     * @see #executeQuery(Function, Consumer)
     */
    <R> Publisher<R> executeQuery(Function<CurrentRow, R> rowFunc);


    /**
     * <p>Execute a sql statement and server response just one query result,the result consist of :
     * <ol>
     *     <li>one {@link ResultRowMeta},the {@link ResultRowMeta#resultNo()} always return 1</li>
     *     <li>0-N data row,the {@link DataRow#resultNo()} return same with {@link ResultRowMeta#resultNo()}</li>
     *     <li>1-N {@link ResultStates},the {@link ResultStates#hasColumn()} always return true,he {@link ResultStates#resultNo()} return same with {@link ResultRowMeta#resultNo()}</li>
     * </ol>
     * To avoid creating {@link ResultRow} instance for improving performance ,driver create just one {@link CurrentRow} instance for this result<br/>
     * and wrap {@link ResultRowMeta} to {@link CurrentRow#getRowMeta()},and {@link ResultStates} is optional, if you don't need.
     * <p><strong>NOTE</strong>: if use {@link #setFetchSize(int)},then will produce multi {@link ResultStates} instance and the {@link ResultStates#resultNo()} always return 1 .
     * <p>
     * <strong>NOTE</strong> : driver don't send message to database server before subscribing. Driver developer must guarantee this feature.
     * <br/>
     *
     * @param rowFunc        current row map function.Using rowFunc to avoid create {@link ResultRow} instance for improving performance.<br/>
     *                       <strong>NOTE</strong>:
     *                       <ul>
     *                           <li>rowFunc couldn't return {@link CurrentRow} instance.</li>
     *                           <li>driver will invoke rowFunc in an ordered / serial fashion. Typically ,rowFunc run in {@code  io.netty.channel.EventLoop} </li>
     *                           <li>you couldn't invoke the block method of {@link Publisher} in rowFunc,or emit {@link Throwable}. For example :
     *                                  <ul>
     *                                      <li>{@code reactor.core.publisher.Flux#blockLast()}</li>
     *                                      <li>{@code reactor.core.publisher.Flux#blockFirst()}</li>
     *                                  </ul>
     *                           </li>
     *                       </ul>
     * @param statesConsumer a consumer to receive the {@link ResultStates},if don't use {@link #setFetchSize(int)} ,<br/>
     *                       then will be invoked just once by driver,else will be invoked multi-times by driver. <br/>
     *                       <strong>NOTE</strong>:
     *                       <ul>
     *                          <li>even if use {@link #setFetchSize(int)} the {@link ResultStates#resultNo()} always return 1</li>
     *                          <li>driver will invoke statesConsumer in an ordered / serial fashion. Typically ,statesConsumer run in {@code  io.netty.channel.EventLoop}</li>
     *                       </ul>
     * @param <R>            the row java type,it is returned by rowFunc.
     * @return the {@link Publisher} emit 0-N element or {@link Throwable}, Like {@code reactor.core.publisher.Flux} .
     * @throws JdbdException        emmit(not throw) when
     *                              <ul>
     *                                  <li>you reuse this {@link BindSingleStatement} instance</li>
     *                                  <li>param bind error</li>
     *                                  <li>the java type of value of appropriate dataType isn't supported by the implementation of this method ,for example : {@link io.jdbd.meta.JdbdType#BIGINT} bind {@link io.jdbd.type.Clob}</li>
     *                                  <li>rowFunc throw {@link Throwable}</li>
     *                                  <li>statesConsumer throw {@link Throwable}</li>
     *                                  <li>sql error</li>
     *                                  <li>session have closed ,see {@link io.jdbd.session.SessionCloseException}</li>
     *                                  <li>server response error ,see {@link ServerException}</li>
     *                                  <li>server response result not match,e.g: response multi-result,or update result</li>
     *                                  <li>rowFunc return {@link CurrentRow} instance</li>
     *                              </ul>
     * @throws NullPointerException emit(not throw) when
     *                              <ul>
     *                                  <li>rowFunc is null</li>
     *                                  <li>statesConsumer is null</li>
     *                              </ul>
     */
    <R> Publisher<R> executeQuery(Function<CurrentRow, R> rowFunc, Consumer<ResultStates> statesConsumer);

    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // statement is a instance of {@link BindSingleStatement}
     *              R flux  = fluxFunc.apply(statement.executeQuery(rowFunc,statesConsumer)) ;
     *
     *              // for example ,if use Project reactor , reactor.core.publisher.Flux
     *              statement.executeQuery(rowFunc,statesConsumer,Flux::from)
     *                 .collectList()
     *
     *         </code>
     *     </pre>
     * <br/>
     *
     * @param rowFunc        see {@link #executeQuery(Function, Consumer)}
     * @param statesConsumer see {@link #executeQuery(Function, Consumer)}
     * @param fluxFunc       convertor function of Publisher ,for example : {@code reactor.core.publisher.Flux#from(org.reactivestreams.Publisher)}
     * @param <R>            see {@link #executeQuery(Function, Consumer)}
     * @param <F>            F representing {@link Publisher} that emit 0-N element or {@link Throwable}.
     * @return {@link Publisher} that emit 0-N element or {@link Throwable}.
     * @see #executeQuery(Function, Consumer)
     */
    <R, F extends Publisher<R>> F executeQuery(Function<CurrentRow, R> rowFunc, Consumer<ResultStates> statesConsumer, Function<Publisher<R>, F> fluxFunc);


    /**
     * <p>Execute a sql statement and server response 1-N result.The result can be update result or query result.
     * <p>More info see {@link OrderedFlux}.
     * <p>Typically,in most case, only procedure can produce multi-result.
     *
     * @return {@link OrderedFlux} that emit 1-N result or {@link Throwable}
     * @throws JdbdException emmit(not throw) when
     *                       <ul>
     *                           <li>you reuse this {@link BindSingleStatement} instance</li>
     *                           <li>param bind error</li>
     *                           <li>the java type of value of appropriate dataType isn't supported by the implementation of this method ,for example : {@link io.jdbd.meta.JdbdType#BIGINT} bind {@link io.jdbd.type.Clob}</li>
     *                           <li>sql error</li>
     *                           <li>session have closed ,see {@link io.jdbd.session.SessionCloseException}</li>
     *                           <li>server response error ,see {@link ServerException}</li>
     *                       </ul>
     */
    OrderedFlux executeAsFlux();

    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // statement is a instance of {@link BindSingleStatement}
     *              R flux  = fluxFunc.apply(statement.executeAsFlux()) ;
     *
     *              // for example ,if use Project reactor , reactor.core.publisher.Flux
     *              statement.executeAsFlux(Flux::from)
     *                 .collectList()
     *
     *         </code>
     *     </pre>
     * <br/>
     *
     * @param fluxFunc convertor function of Publisher ,for example : {@code reactor.core.publisher.Flux#from(org.reactivestreams.Publisher)}
     * @param <F>      F representing {@link Publisher} that emit 0-N element or {@link Throwable}.
     * @return Flux that emit just one element or {@link Throwable}.
     * @see #executeAsFlux()
     */
    <F extends Publisher<ResultItem>> F executeAsFlux(Function<OrderedFlux, F> fluxFunc);


    /**
     * <p>Execute a sql statement with batch parameter and server response multi query result,the result consist of :
     * <ol>
     *     <li>one {@link ResultRowMeta},the {@link ResultRowMeta#resultNo()}</li>
     *     <li>0-N data row,the {@link DataRow#resultNo()} return same with {@link ResultRowMeta#resultNo()}</li>
     *     <li>one {@link ResultStates},the {@link ResultStates#hasColumn()} always return true,he {@link ResultStates#resultNo()} return same with {@link ResultRowMeta#resultNo()}</li>
     * </ol>
     * To avoid creating {@link ResultRow} instance for improving performance ,driver create just one {@link CurrentRow} instance for each query result<br/>
     * and wrap {@link ResultRowMeta} to {@link CurrentRow#getRowMeta()},and {@link ResultStates} is optional, if you don't need.
     * <p><strong>NOTE</strong> : driver don't send message to database server before subscribing. Driver developer must guarantee this feature.
     * <br/>
     *
     * @param rowFunc        current row map function.Using rowFunc to avoid create {@link ResultRow} instance for improving performance.<br/>
     *                       <strong>NOTE</strong>:
     *                       <ul>
     *                           <li>rowFunc couldn't return {@link CurrentRow} instance.</li>
     *                           <li>driver will invoke rowFunc in an ordered / serial fashion. Typically ,rowFunc run in {@code  io.netty.channel.EventLoop} </li>
     *                           <li>you couldn't invoke the block method of {@link Publisher} in rowFunc,or emit {@link Throwable}. For example :
     *                                  <ul>
     *                                      <li>{@code reactor.core.publisher.Flux#blockLast()}</li>
     *                                      <li>{@code reactor.core.publisher.Flux#blockFirst()}</li>
     *                                  </ul>
     *                           </li>
     *                       </ul>
     * @param statesConsumer a consumer to receive the {@link ResultStates} of each query result
     *                       <strong>NOTE</strong>:driver will invoke statesConsumer in an ordered / serial fashion. Typically ,statesConsumer run in {@code  io.netty.channel.EventLoop}
     * @param <R>            the row java type,it is returned by rowFunc.
     * @return the {@link Publisher} emit 0-N element or {@link Throwable}, Like {@code reactor.core.publisher.Flux} .
     * @throws JdbdException        emmit(not throw) when
     *                              <ul>
     *                                  <li>you reuse this {@link BindSingleStatement} instance</li>
     *                                  <li>param bind error</li>
     *                                  <li>the java type of value of appropriate dataType isn't supported by the implementation of this method ,for example : {@link io.jdbd.meta.JdbdType#BIGINT} bind {@link io.jdbd.type.Clob}</li>
     *                                  <li>rowFunc throw {@link Throwable}</li>
     *                                  <li>statesConsumer throw {@link Throwable}</li>
     *                                  <li>sql error</li>
     *                                  <li>session have closed ,see {@link io.jdbd.session.SessionCloseException}</li>
     *                                  <li>server response error ,see {@link ServerException}</li>
     *                                  <li>server response result not match,e.g: response multi-result,or update result</li>
     *                                  <li>rowFunc return {@link CurrentRow} instance</li>
     *                              </ul>
     * @throws NullPointerException emit(not throw) when
     *                              <ul>
     *                                  <li>rowFunc is null</li>
     *                                  <li>statesConsumer is null</li>
     *                              </ul>
     */
    <R> Publisher<R> executeBatchQueryAsFlux(Function<CurrentRow, R> rowFunc, Consumer<ResultStates> statesConsumer);

    /**
     * <p>Set frequency to help driver caching server-prepared statement.
     * <p>Default : -1  in the implementation of jdbd-spi,so if you don't invoke this method,driver will ignore this option.
     * <p><Strong>NOTE</Strong>: If you invoke this method ,then driver will always use server-prepared.
     *
     * @param frequency <ul>
     *                  <li>negative : throw {@link IllegalArgumentException}</li>
     *                  <li>0 : never cache server-prepared statement,if have cached ,close server-prepared statement and delete cache</li>
     *                  <li>positive : representing frequency</li>
     *                  <li>{@link Integer#MAX_VALUE} : always cache server-prepared statement</li>
     *                  </ul>
     * @return <strong>this</strong>
     */
    BindSingleStatement setFrequency(int frequency) throws IllegalArgumentException;


    /**
     * {@inheritDoc }
     */
    @Override
    BindSingleStatement setTimeout(int millSeconds) throws IllegalArgumentException;

    /**
     * {@inheritDoc }
     */
    @Override
    BindSingleStatement setFetchSize(int fetchSize) throws IllegalArgumentException;

    /**
     * {@inheritDoc }
     */
    @Override
    BindSingleStatement setImportPublisher(Function<ChunkOption, Publisher<byte[]>> function) throws JdbdException;

    /**
     * {@inheritDoc }
     */
    @Override
    BindSingleStatement setExportSubscriber(Function<ChunkOption, Subscriber<byte[]>> function) throws JdbdException;

    /**
     * {@inheritDoc }
     */
    <T> BindSingleStatement setOption(Option<T> option, @Nullable T value) throws JdbdException;


}
