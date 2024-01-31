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

package io.jdbd.util;

import io.jdbd.session.Option;

import java.util.function.Function;

public interface SqlLogger {

    void print(String sql);


    static void printLog(Function<Option<?>, ?> function, String sql) {
        final Object logger;
        if (function == Option.EMPTY_OPTION_FUNC
                || !((logger = function.apply(Option.SQL_LOGGER)) instanceof SqlLogger)) {
            return;
        }

        try {
            ((SqlLogger) logger).print(sql);
        } catch (Exception e) {
            // ignore
        }
    }


}
