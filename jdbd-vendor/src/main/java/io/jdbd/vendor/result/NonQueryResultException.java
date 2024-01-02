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

package io.jdbd.vendor.result;

import io.jdbd.JdbdException;
import io.jdbd.statement.BindSingleStatement;

import java.util.function.Consumer;
import java.util.function.Function;


/**
 * <p>
 * emit(not throw) when subscribe executeQuery() method but database server response not match.
 * For example : subscribe {@link BindSingleStatement#executeQuery(Function, Consumer)} but database server response update result.
 * <br/>
 *
 * @since 1.0
 */
public final class NonQueryResultException extends JdbdException {

    public NonQueryResultException(String message) {
        super(message);
    }

}
