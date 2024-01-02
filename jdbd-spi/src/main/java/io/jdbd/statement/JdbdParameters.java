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

import io.jdbd.lang.Nullable;

import java.util.Objects;

/**
 * <p>
 * This class provider the method create {@link Parameter}
 * <br/>
 *
 * @since 1.0
 */
abstract class JdbdParameters {

    private JdbdParameters() {
        throw new UnsupportedOperationException();
    }


    static OutParameter outParam(final @Nullable String name) {
        if (name == null) {
            throw new NullPointerException("out parameter name must non-null");
        }
        return new JdbdOutParameter(name);
    }

    private static final class JdbdOutParameter implements OutParameter {

        private final String name;

        private JdbdOutParameter(String name) {
            this.name = name;
        }

        @Override
        public String name() {
            return this.name;
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.name);
        }

        @Override
        public boolean equals(final Object obj) {
            final boolean match;
            if (obj == this) {
                match = true;
            } else if (obj instanceof JdbdOutParameter) {
                match = ((JdbdOutParameter) obj).name.equals(this.name);
            } else {
                match = false;
            }
            return match;
        }

        @Override
        public String toString() {
            return String.format("%s[ name : %s , hash : %s]",
                    getClass().getName(),
                    this.name,
                    System.identityHashCode(this)
            );
        }


    }// JdbdOutParameter


}
