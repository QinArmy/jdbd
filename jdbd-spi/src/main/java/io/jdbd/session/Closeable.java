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

package io.jdbd.session;

import io.jdbd.JdbdException;
import io.jdbd.result.Cursor;
import org.reactivestreams.Publisher;

/**
 * <p>
 * This interface is base interface of following :
 *     <ul>
 *         <li>{@link DatabaseSessionFactory}</li>
 *         <li>{@link DatabaseSession}</li>
 *         <li>{@link Cursor}</li>
 *     </ul>
 * <br/>
 *
 * @since 1.0
 */
public interface Closeable {

    /**
     * <p>
     * Close underlying resource.
     *<br/>
     * <p>
     * <strong>NOTE</strong> :
     *     <ul>
     *         <li>driver don't send message to database server before subscribing.</li>
     *         <li>If have closed emit nothing</li>
     *     </ul>
     *<br/>
     *
     * @param <T> representing any java type,because this method usually is used with concatWith(Publisher) method.
     * @return the {@link Publisher} that emit nothing or emit {@link JdbdException}
     */
    <T> Publisher<T> close();

    /**
     * whether have closed or not.
     *
     * @return true : have closed
     */
    boolean isClosed();


}
