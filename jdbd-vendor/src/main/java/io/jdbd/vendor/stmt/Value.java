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

package io.jdbd.vendor.stmt;


import io.jdbd.lang.Nullable;
import io.jdbd.meta.DataType;
import io.jdbd.statement.Parameter;

/**
 * <p>
 * This interface representing a value that is bound to sql.
 * This is a base interface of below interface:
 * <ul>
 * <li>{@link ParamValue}</li>
 * <li>{@link NamedValue }</li>
 * </ul>
 * <br/>
 * <br/>
 *
 * @see ParamValue
 * @see NamedValue
 */
public interface Value {

    /**
     * Get parameter value
     *
     * @return <ul>
     * <li>null</li>
     * <li>non-{@link Parameter} instance</li>
     * <li>{@link Parameter instance}</li>
     * </ul>
     */
    @Nullable
    Object get();

    /**
     * Get parameter value,throw {@link NullPointerException} if value is null
     * @return <ul>
     * <li>non-{@link Parameter} instance</li>
     * <li>{@link Parameter instance}</li>
     * </ul>
     * @throws NullPointerException throw when param is null
     */
    Object getNonNull() throws NullPointerException;

    /**
     * Get parameter type
     * @return sql type of bind value.
     */
    DataType getType();

}
