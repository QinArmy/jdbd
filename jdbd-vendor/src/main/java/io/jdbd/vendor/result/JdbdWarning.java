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

import io.jdbd.result.Warning;
import io.jdbd.session.Option;
import io.jdbd.vendor.util.JdbdOptionSpec;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

public final class JdbdWarning extends JdbdOptionSpec implements Warning {

    private static final JdbdWarning EMPTY = new JdbdWarning("", Collections.emptyMap());

    public static JdbdWarning create(String message, Map<Option<?>, ?> optionMap) {
        Objects.requireNonNull(message, "message");
        final JdbdWarning warning;
        if (message.isEmpty() && optionMap.size() == 0) {
            warning = EMPTY;
        } else {
            warning = new JdbdWarning(message, optionMap);
        }
        return warning;
    }


    private final String message;

    private JdbdWarning(String message, Map<Option<?>, ?> optionMap) {
        super(optionMap);
        this.message = message;

    }


    @Override
    public String message() {
        return this.message;
    }


}
