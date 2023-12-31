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
import io.jdbd.result.ResultRowMeta;
import io.jdbd.result.Warning;
import io.jdbd.session.ChunkOption;
import io.jdbd.session.DatabaseSession;
import io.jdbd.session.Option;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * <p>This interface is is similar to {@code java.sql.PreparedStatement}, except that this interface is reactive.
 * <p>This interface is designed for providing following methods:
 *     <ul>
 *         <li>{@link #paramTypeList()}</li>
 *         <li>{@link #resultRowMeta()}</li>
 *         <li>{@link #waring()}</li>
 *     </ul>
 *     , so if you don't need above methods, then you can use {@link DatabaseSession#bindStatement(String, boolean)}.
 * <p>The instance of this interface is created by {@link DatabaseSession#prepareStatement(String)} method.
 * <p>
 * You should invoke one of following :
 * <ul>
 *     <li>{@link #executeUpdate()}</li>
 *     <li>{@link #executeQuery()}</li>
 *     <li>{@link #executeQuery(Function)}</li>
 *     <li>{@link #executeQuery(Function, Consumer)}</li>
 *     <li>{@link #executeBatchUpdate()}</li>
 *     <li>{@link #executeBatchQuery()}</li>
 *     <li>{@link #executeBatchAsMulti()}</li>
 *     <li>{@link #executeBatchAsFlux()}</li>
 *     <li>{@link #abandonBind()}</li>
 * </ul>
 * <p>Application developer can help driver developer cache server-prepared statement by {@link #setFrequency(int)} methods.
 * <p>
 *     <strong>NOTE</strong>: this interface is auto closing when
 *     <ul>
 *         <li>after executeXxx() method accomplishing</li>
 *         <li>binding occur error</li>
 *         <li>set statement option occur error,for example : {@link #setTimeout(int)}</li>
 *     </ul> ,
 *     so {@link PreparedStatement} have no close() method.
 * <br/>
 *
 * @see DatabaseSession#prepareStatement(String)
 * @see BindStatement
 */
public interface PreparedStatement extends BindSingleStatement {

    /**
     * {@link ResultRowMeta#getColumnCount()} is 0 , if statement don't produce result set.
     */
    ResultRowMeta resultRowMeta();

    List<? extends DataType> paramTypeList();


    @Nullable
    Warning waring();

    /**
     * {@inheritDoc }
     */
    @Override
    PreparedStatement bind(int indexBasedZero, DataType dataType, @Nullable Object value) throws JdbdException;


    /**
     * {@inheritDoc }
     */
    @Override
    PreparedStatement bindStmtVar(String name, DataType dataType, @Nullable Object value) throws JdbdException;

    /**
     * {@inheritDoc }
     */
    @Override
    PreparedStatement addBatch() throws JdbdException;

    /**
     * <p>
     * This method close this  {@link PreparedStatement} if you don't invoke any executeXxx() method.
     * <br/>
     * <p>
     * Abandon binding before invoke executeXxx() method.
     * <br/>
     *
     * @return Publisher like {@code reactor.core.publisher.Mono} ,
     * if success emit {@link DatabaseSession} that create this {@link PreparedStatement}.
     * @throws JdbdException emit(not throw), when after invoking executeXxx().
     */
    DatabaseSession abandonBind();

    /**
     * {@inheritDoc }
     */
    @Override
    PreparedStatement setFrequency(int frequency) throws IllegalArgumentException;

    /**
     * {@inheritDoc }
     */
    @Override
    PreparedStatement setTimeout(int millSeconds) throws IllegalArgumentException;

    /**
     * {@inheritDoc }
     */
    @Override
    PreparedStatement setFetchSize(int fetchSize) throws IllegalArgumentException;

    /**
     * {@inheritDoc }
     */
    @Override
    PreparedStatement setImportPublisher(Function<ChunkOption, Publisher<byte[]>> function) throws JdbdException;

    /**
     * {@inheritDoc }
     */
    @Override
    PreparedStatement setExportSubscriber(Function<ChunkOption, Subscriber<byte[]>> function) throws JdbdException;


    /**
     * {@inheritDoc }
     */
    <T> PreparedStatement setOption(Option<T> option, @Nullable T value) throws JdbdException;


}
