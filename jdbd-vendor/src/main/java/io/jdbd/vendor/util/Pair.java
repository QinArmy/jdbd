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

package io.jdbd.vendor.util;


import java.util.Objects;

public final class Pair<F, S> {

    public static <F, S> Pair<F, S> create(F first, S second) {
        return new Pair<>(first, second);
    }

    public final F first;

    public final S second;

    private Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }

    /**
     * For {@link java.util.function.Supplier}
     */
    public F getFirst() {
        return this.first;
    }

    /**
     * For {@link java.util.function.Supplier}
     */
    public S getSecond() {
        return this.second;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.first, this.second);
    }

    @Override
    public boolean equals(final Object obj) {
        final boolean match;
        if (obj == this) {
            match = true;
        } else if (obj instanceof Pair) {
            final Pair<?, ?> o = (Pair<?, ?>) obj;
            match = Objects.equals(o.first, this.first) && Objects.equals(o.second, this.second);
        } else {
            match = false;
        }
        return match;
    }

    @Override
    public String toString() {
        return JdbdStrings.builder()
                .append(Pair.class.getName())
                .append("[ first : ")
                .append(this.first)
                .append(" , second : ")
                .append(this.second)
                .append(" , hash : ")
                .append(System.identityHashCode(this))
                .append(" ]")
                .toString();
    }


}
