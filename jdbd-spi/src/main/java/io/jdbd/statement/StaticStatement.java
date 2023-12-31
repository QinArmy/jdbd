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
import io.jdbd.result.CurrentRow;
import io.jdbd.result.OrderedFlux;
import io.jdbd.result.ResultItem;
import io.jdbd.result.ResultStates;
import io.jdbd.session.ChunkOption;
import io.jdbd.session.DatabaseSession;
import io.jdbd.session.Option;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * <p>
 * This interface representing static sql statement with statement option (eg: timeout).
 * <br/>
 * <p>
 * This interface is similar to {@code java.sql.Statement}, except that this interface is reactive.
 * <br/>
 * <p>
 * This interface representing the statement couldn't contain any sql parameter placeholder({@code ?}) .
 * <br/>
 * <p>
 * The instance of this interface is created by {@link DatabaseSession#statement()} method.
 * <br/>
 *
 * @see DatabaseSession#statement()
 * @since 1.0
 */
public interface StaticStatement extends Statement, StaticStatementSpec {


    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // s is a instance of {@link StaticStatementSpec}
     *              R mono  = monoFunc.apply(s.executeUpdate(sql)) ;
     *
     *              // for example ,if use Project reactor , reactor.core.publisher.Mono
     *              s.executeUpdate(sql,Mono::from)
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
     * @see #executeUpdate(String)
     */
    <M extends Publisher<ResultStates>> M executeUpdate(String sql, Function<Publisher<ResultStates>, M> monoFunc);

    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // s is a instance of {@link StaticStatementSpec}
     *              R flux  = fluxFunc.apply(s.executeQuery(sql,rowFunc,statesConsumer)) ;
     *
     *              // for example ,if use Project reactor , reactor.core.publisher.Flux
     *              s.executeQuery(sql,rowFunc,statesConsumer,Flux::from)
     *                 .collectList()
     *
     *         </code>
     *     </pre>
     *<br/>
     *
     * @param fluxFunc convertor function of Publisher ,for example : {@code reactor.core.publisher.Flux#from(org.reactivestreams.Publisher)}
     * @param <F>      F representing Flux that emit 0-N element or {@link Throwable}.
     * @return non-null Flux that emit just one element or {@link Throwable}.
     * @throws NullPointerException throw when
     *                              <ul>
     *                                  <li>fluxFunc is null</li>
     *                                  <li>fluxFunc return null</li>
     *                              </ul>
     * @see #executeQuery(String, Function, Consumer)
     */
    <R, F extends Publisher<R>> F executeQuery(String sql, Function<CurrentRow, R> rowFunc, Consumer<ResultStates> statesConsumer, Function<Publisher<R>, F> fluxFunc);


    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // s is a instance of {@link StaticStatementSpec}
     *              R flux  = fluxFunc.apply(s.executeBatchUpdate(sqlList)) ;
     *
     *              // for example ,if use Project reactor , reactor.core.publisher.Flux
     *              s.executeBatchUpdate(sqlList,Flux::from)
     *                 .collectList()
     *
     *         </code>
     *     </pre>
     *<br/>
     *
     * @param fluxFunc convertor function of Publisher ,for example : {@code reactor.core.publisher.Flux#from(org.reactivestreams.Publisher)}
     * @param <F>      F representing Flux that emit 0-N element or {@link Throwable}.
     * @return non-null Flux that emit just one element or {@link Throwable}.
     * @throws NullPointerException throw when
     *                              <ul>
     *                                  <li>fluxFunc is null</li>
     *                                  <li>fluxFunc return null</li>
     *                              </ul>
     * @see #executeBatchUpdate(List)
     */
    <F extends Publisher<ResultStates>> F executeBatchUpdate(List<String> sqlGroup, Function<Publisher<ResultStates>, F> fluxFunc);

    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // s is a instance of {@link StaticStatementSpec}
     *              R flux  = fluxFunc.apply(s.executeBatchAsFlux(sqlList)) ;
     *
     *              // for example ,if use Project reactor , reactor.core.publisher.Flux
     *              s.executeBatchAsFlux(sqlList,Flux::from)
     *                 .collectList()
     *
     *         </code>
     *     </pre>
     *<br/>
     *
     * @param fluxFunc convertor function of Publisher ,for example : {@code reactor.core.publisher.Flux#from(org.reactivestreams.Publisher)}
     * @param <F>      F representing Flux that emit 0-N element or {@link Throwable}.
     * @return non-null Flux that emit just one element or {@link Throwable}.
     * @throws NullPointerException emit(not throw) when
     *                              <ul>
     *                                  <li>fluxFunc is null</li>
     *                                  <li>fluxFunc return null</li>
     *                              </ul>
     * @see #executeBatchAsFlux(List)
     */
    <F extends Publisher<ResultItem>> F executeBatchAsFlux(List<String> sqlGroup, Function<OrderedFlux, F> fluxFunc);

    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // s is a instance of {@link StaticStatementSpec}
     *              R flux  = fluxFunc.apply(s.executeMultiStmt(multiStmt)) ;
     *
     *              // for example ,if use Project reactor , reactor.core.publisher.Flux
     *              s.executeMultiStmt(multiStmt,Flux::from)
     *                 .collectList()
     *
     *         </code>
     *     </pre>
     *<br/>
     *
     * @param fluxFunc convertor function of Publisher ,for example : {@code reactor.core.publisher.Flux#from(org.reactivestreams.Publisher)}
     * @param <F>      F representing Flux that emit 0-N element or {@link Throwable}.
     * @return non-null Flux that emit just one element or {@link Throwable}.
     * @throws NullPointerException throw when
     *                              <ul>
     *                                  <li>fluxFunc is null</li>
     *                                  <li>fluxFunc return null</li>
     *                              </ul>
     * @see #executeMultiStmt(String)
     */
    <F extends Publisher<ResultItem>> F executeMultiStmt(String multiStmt, Function<OrderedFlux, F> fluxFunc);


    /**
     * {@inheritDoc }
     */
    @Override
    StaticStatement bindStmtVar(String name, DataType dataType, @Nullable Object value) throws JdbdException;

    /**
     * {@inheritDoc }
     */
    @Override
    StaticStatement setTimeout(int millSeconds) throws IllegalArgumentException;

    /**
     * {@inheritDoc }
     */
    @Override
    StaticStatement setFetchSize(int fetchSize) throws IllegalArgumentException;

    /**
     * {@inheritDoc }
     */
    @Override
    StaticStatement setImportPublisher(Function<ChunkOption, Publisher<byte[]>> function) throws JdbdException;

    /**
     * {@inheritDoc }
     */
    @Override
    StaticStatement setExportSubscriber(Function<ChunkOption, Subscriber<byte[]>> function) throws JdbdException;

    /**
     * {@inheritDoc }
     */
    <T> StaticStatement setOption(Option<T> option, @Nullable T value) throws JdbdException;


}
