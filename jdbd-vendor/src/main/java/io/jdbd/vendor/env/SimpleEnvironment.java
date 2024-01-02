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
import io.jdbd.vendor.util.JdbdCollections;

import java.util.Collections;
import java.util.Map;
import java.util.function.Supplier;

public final class SimpleEnvironment implements Environment {

    public static SimpleEnvironment from(Map<String, Object> sourceMap) {
        return new SimpleEnvironment(sourceMap);
    }


    private final Map<String, Object> sourceMap;

    private SimpleEnvironment(Map<String, Object> sourceMap) {
        this.sourceMap = Collections.unmodifiableMap(JdbdCollections.hashMap(sourceMap));
    }

    @Override
    public Map<String, Object> sourceMap() {
        return this.sourceMap;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(final Key<T> key) throws JdbdException {
        final Object source;
        source = this.sourceMap.get(key.name);
        final T value;
        if (source == null || key.valueClass.isInstance(source)) {
            value = (T) source;
        } else if (source instanceof String) {
            value = Converters.findConvertor(key.valueClass)
                    .apply(key.valueClass, (String) source);
        } else {
            String m = String.format("%s isn't %s or %s .",
                    source.getClass().getName(),
                    String.class.getName(),
                    key.valueClass.getName()
            );
            throw new JdbdException(m);
        }
        return value;
    }

    @Override
    public <T> T get(Key<T> key, Supplier<T> supplier) throws JdbdException {
        T value;
        value = get(key);
        if (value == null) {
            value = supplier.get();
        }
        if (value == null) {
            String m = String.format("%s return null", supplier);
            throw new JdbdException(m);
        }
        return value;
    }

    @Override
    public <T> T getOrDefault(final Key<T> key) throws JdbdException {
        T value;
        value = get(key);
        if (value == null) {
            value = key.defaultValue;
        }
        if (value == null) {
            String m = String.format("%s no default value", key);
            throw new JdbdException(m);
        }
        return value;
    }

    @Override
    public <T extends Comparable<T>> T getInRange(final Key<T> key, final T minValue, final T maxValue)
            throws JdbdException {
        T value;
        value = getOrDefault(key);

        if (value.compareTo(minValue) < 0) {
            value = minValue;
        } else if (value.compareTo(maxValue) > 0) {
            value = maxValue;
        }
        return value;
    }

    @Override
    public <T> T getRequired(Key<T> key) throws JdbdException {
        final T value;
        value = get(key);
        if (value == null) {
            String m = String.format("%s value is null", key);
            throw new JdbdException(m);
        }
        return value;
    }

    @Override
    public boolean isOn(Key<Boolean> key) throws JdbdException {
        return getOrDefault(key);
    }

    @Override
    public boolean isOff(Key<Boolean> key) throws JdbdException {
        return !getOrDefault(key);
    }


}
