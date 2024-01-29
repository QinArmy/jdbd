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

import io.jdbd.lang.Nullable;
import io.jdbd.result.Cursor;

import java.util.Objects;
import java.util.Set;

/**
 * <p>
 * This interface is base interface of following :
 *     <ul>
 *         <li>{@link DatabaseSessionFactory}</li>
 *         <li>{@link DatabaseMetaSpec}</li>
 *         <li>{@link io.jdbd.statement.Statement}</li>
 *         <li>{@link io.jdbd.result.ResultStates}</li>
 *         <li>{@link io.jdbd.VersionSpec}</li>
 *         <li>{@link io.jdbd.result.ServerException}</li>
 *         <li>{@link Cursor}</li>
 *         <li>{@link TransactionOption}</li>
 *         <li>{@link io.jdbd.result.Warning}</li>
 *         <li>{@link ChunkOption}</li>
 *     </ul>
 *     ,it provider more dialectal driver.
 *<br/>
 *
 * @see Option
 * @see io.jdbd.statement.Statement#setOption(Option, Object)
 * @since 1.0
 */
public interface OptionSpec {

    /**
     * <p>
     * This method can provider more dialectal driver.
     * <br/>
     * <p>
     * The implementation of this method must provide java doc(html list) for explaining supporting {@link Option} list.
     * <br/>
     *
     * @param option non-null
     * @param <T>    value java class
     * @return null or the value of option.
     */
    @Nullable
    <T> T valueOf(Option<T> option);

    /**
     * Get non-null option value
     *
     * @param option non-null
     * @param <T>    value java class
     * @return non-null value
     * @throws NullPointerException throw when {@link #valueOf(Option)} return null
     */
    default <T> T nonNullOf(Option<T> option) {
        final T value;
        value = valueOf(option);
        Objects.requireNonNull(value);
        return value;
    }

    /**
     * @return a unmodified set
     */
    Set<Option<?>> optionSet();


}
