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
import io.jdbd.lang.Nullable;
import io.jdbd.session.Option;
import io.jdbd.session.OptionSpec;

import java.util.function.Function;


/**
 * <p>
 * Emit(not throw), when server return error message.
 * <br/>
 *
 * @since 1.0
 */
public abstract class ServerException extends JdbdException implements OptionSpec {

    private final Function<Option<?>, ?> optionFunc;


    protected ServerException(String message, @Nullable String sqlState, int vendorCode,
                              Function<Option<?>, ?> optionFunc) {
        super(message, sqlState, vendorCode);
        this.optionFunc = optionFunc;
    }

    @SuppressWarnings("unchecked")
    @Override
    public final <T> T valueOf(Option<T> option) {
        final Object value;
        value = this.optionFunc.apply(option);
        if (option.javaType().isInstance(value)) {
            return (T) value;
        }
        return null;
    }


}
