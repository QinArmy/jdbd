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

package io.jdbd.pool;

import io.jdbd.session.DatabaseSession;
import io.jdbd.session.DatabaseSessionFactory;
import org.reactivestreams.Publisher;

/**
 * <p>
 * This interface representing {@link DatabaseSession} than can be pooled.
 * This interface is base interface of following :
 *     <ul>
 *         <li>{@link PoolLocalDatabaseSession}</li>
 *         <li>{@link PoolRmDatabaseSession}</li>
 *     </ul>
 * <br/>
 * <p>
 * The instance of this interface is created by {@link DatabaseSessionFactory}.
 * <br/>
 * <p>
 *     This interface is used by pool vendor,application developer shouldn't use this interface.
 *     Driver developer create the instance of this interface,but driver developer don't use this interface,
 *     because driver developer are not responsible for pooling.
 * <p>
 * <br/>
 *
 * @since 1.0
 */
public interface PoolDatabaseSession extends DatabaseSession {

    /**
     * send ping message to server
     *
     * @return {@link Publisher} emit <strong>this</strong> or {@link Throwable},like {@code reactor.core.publisher.Mono}
     * @throws io.jdbd.JdbdException emit(not throw) when
     *                               <ul>
     *                                   <li>session have closed,see {@link io.jdbd.session.SessionCloseException}</li>
     *                                   <li>server response error message,see {@link io.jdbd.result.ServerException}</li>
     *                               </ul>
     */
    Publisher<? extends PoolDatabaseSession> ping();


    /**
     * <p>
     * Reset session :
     * <ul>
     *     <li>reset key session variable , for example :
     *          <ul>
     *              <li>transaction isolation level</li>
     *              <li>auto commit</li>
     *              <li>server zone</li>
     *              <li>charset</li>
     *              <li>data type output format</li>
     *          </ul>
     *      </li>
     *     <li>if database support user-defined data type,then should check whether exists new data type or not.</li>
     * </ul>
     * <br/>
     * <p>
     *  @return {@link Publisher} emit <strong>this</strong> or {@link Throwable},like {@code reactor.core.publisher.Mono}
     *
     * @throws io.jdbd.JdbdException emit(not throw) when
     *                               <ul>
     *                                   <li>session have closed,see {@link io.jdbd.session.SessionCloseException}</li>
     *                                   <li>server response error message,see {@link io.jdbd.result.ServerException}</li>
     *                               </ul>
     */
    Publisher<? extends PoolDatabaseSession> reset();

    /**
     * <p>
     * cancel all have not executed statement,emit {@link io.jdbd.session.SessionCloseException} to the downstream of statement.
     * <br/>
     *
     * @return {@link Publisher} emit <strong>this</strong> or {@link Throwable},like {@code reactor.core.publisher.Mono}
     * @throws io.jdbd.JdbdException emit(not throw) when
     *                               <ul>
     *                                   <li>session have closed,see {@link io.jdbd.session.SessionCloseException}</li>
     *                                   <li>server response error message,see {@link io.jdbd.result.ServerException}</li>
     *                               </ul>
     */
    Publisher<? extends PoolDatabaseSession> softClose();

}
