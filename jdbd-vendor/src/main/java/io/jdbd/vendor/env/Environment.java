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

package io.jdbd.vendor.env;

import io.jdbd.JdbdException;
import io.jdbd.lang.Nullable;

import java.util.Map;
import java.util.function.Supplier;

public interface Environment {

    /**
     * @return a unmodified map
     */
    Map<String, Object> sourceMap();

    @Nullable
    <T> T get(Key<T> key) throws JdbdException;

    <T> T get(Key<T> key, Supplier<T> supplier) throws JdbdException;

    <T> T getOrDefault(Key<T> key) throws JdbdException;


    <T extends Comparable<T>> T getInRange(Key<T> key, T minValue, T maxValue) throws JdbdException;

    <T> T getRequired(Key<T> key) throws JdbdException;

    /**
     * @throws JdbdException throw when key default is null
     */
    boolean isOn(Key<Boolean> key) throws JdbdException;

    /**
     * @throws JdbdException throw when key default is null
     */
    boolean isOff(Key<Boolean> key) throws JdbdException;


}
