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
import io.jdbd.util.JdbdUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * <p>
 * This enum is a standard implementation of {@link TransactionInfo}
 * that {@link TransactionInfo#inTransaction()} always is false.
 * <br/>
 *
 * @since 1.0
 */
final class JdbdTransactionOption implements TransactionOption {


    static TransactionOption option(final @Nullable Isolation isolation, final boolean readOnly,
                                    final @Nullable Function<Option<?>, ?> optionFunc) {
        if (optionFunc == null) {
            throw new NullPointerException();
        }
        final TransactionOption option;
        if (optionFunc != Option.EMPTY_OPTION_FUNC) {
            option = new JdbdTransactionOption(isolation, readOnly, optionFunc);
        } else if (isolation == null) {
            option = readOnly ? DEFAULT_READ : DEFAULT_WRITE;
        } else if (isolation == Isolation.REPEATABLE_READ) {
            option = readOnly ? REPEATABLE_READ_READ : REPEATABLE_READ_WRITE;
        } else if (isolation == Isolation.READ_COMMITTED) {
            option = readOnly ? READ_COMMITTED_READ : READ_COMMITTED_WRITE;
        } else if (isolation == Isolation.SERIALIZABLE) {
            option = readOnly ? SERIALIZABLE_READ : SERIALIZABLE_WRITE;
        } else if (isolation == Isolation.READ_UNCOMMITTED) {
            option = readOnly ? READ_UNCOMMITTED_READ : READ_UNCOMMITTED_WRITE;
        } else {
            option = new JdbdTransactionOption(isolation, readOnly, optionFunc);
        }
        return option;
    }

    static Builder builder() {
        return new OptionBuilder();
    }

    static Function<Option<?>, ?> extractFunc(final TransactionOption option) {
        if (option instanceof JdbdTransactionOption) {
            return ((JdbdTransactionOption) option).function;
        }
        return option::valueOf;
    }

    private static final JdbdTransactionOption READ_UNCOMMITTED_READ = new JdbdTransactionOption(Isolation.READ_UNCOMMITTED, true);
    private static final JdbdTransactionOption READ_UNCOMMITTED_WRITE = new JdbdTransactionOption(Isolation.READ_UNCOMMITTED, false);

    private static final JdbdTransactionOption READ_COMMITTED_READ = new JdbdTransactionOption(Isolation.READ_COMMITTED, true);
    private static final JdbdTransactionOption READ_COMMITTED_WRITE = new JdbdTransactionOption(Isolation.READ_COMMITTED, false);

    private static final JdbdTransactionOption REPEATABLE_READ_READ = new JdbdTransactionOption(Isolation.REPEATABLE_READ, true);
    private static final JdbdTransactionOption REPEATABLE_READ_WRITE = new JdbdTransactionOption(Isolation.REPEATABLE_READ, false);

    private static final JdbdTransactionOption SERIALIZABLE_READ = new JdbdTransactionOption(Isolation.SERIALIZABLE, true);
    private static final JdbdTransactionOption SERIALIZABLE_WRITE = new JdbdTransactionOption(Isolation.SERIALIZABLE, false);

    private static final JdbdTransactionOption DEFAULT_READ = new JdbdTransactionOption(null, true);

    private static final JdbdTransactionOption DEFAULT_WRITE = new JdbdTransactionOption(null, false);


    private final Isolation isolation;

    private final boolean readOnly;

    private final Function<Option<?>, ?> function;

    private JdbdTransactionOption(@Nullable Isolation isolation, boolean readOnly) {
        this(isolation, readOnly, Option.EMPTY_OPTION_FUNC);
    }

    private JdbdTransactionOption(@Nullable Isolation isolation, boolean readOnly, Function<Option<?>, ?> function) {
        this.isolation = isolation;
        this.readOnly = readOnly;
        this.function = function;
    }

    @Override
    public Isolation isolation() {
        return this.isolation;
    }

    @Override
    public boolean isReadOnly() {
        return this.readOnly;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T valueOf(final Option<T> key) {
        final Object value;
        if (key == Option.IN_TRANSACTION) {
            value = Boolean.FALSE;
        } else if (key == Option.ISOLATION) {
            value = this.isolation;
        } else if (key == Option.READ_ONLY) {
            value = this.readOnly;
        } else {
            final Object v;
            v = this.function.apply(key);
            if (key.javaType().isInstance(v)) {
                value = v;
            } else {
                value = null;
            }

        }
        return (T) value;
    }

    @Override
    public String toString() {
        final Isolation isolation = this.isolation;
        return JdbdUtils.builder(88)
                .append(getClass().getName())
                .append("[name:")
                .append(valueOf(Option.NAME))
                .append(",isolation")
                .append(isolation == null ? null : isolation.name())
                .append(",readOnly")
                .append(this.readOnly)
                .append(",hash:")
                .append(System.identityHashCode(this))
                .append(",label:")
                .append(valueOf(Option.LABEL))
                .append(']')
                .toString();
    }


    private static final class OptionBuilder implements Builder {

        private Map<Option<?>, Object> optionMap;


        @Override
        public <T> Builder option(final Option<T> key, final @Nullable T value) {
            Map<Option<?>, Object> optionMap = this.optionMap;
            if (optionMap == null && value == null) {
                return this;
            }
            if (optionMap == null) {
                this.optionMap = optionMap = new HashMap<>();
            }
            if (value == null) {
                optionMap.remove(key);
            } else {
                optionMap.put(key, value);
            }
            return this;
        }

        @Override
        public TransactionOption build() {

            final Map<Option<?>, Object> map = this.optionMap;
            if (map == null) {
                return DEFAULT_WRITE;
            }

            this.optionMap = null; // clear
            if (map.containsKey(Option.IN_TRANSACTION)) {
                throw new IllegalArgumentException("don't support IN_TRANSACTION option");
            }

            final Isolation isolation;
            isolation = (Isolation) map.remove(Option.ISOLATION);

            final boolean readOnly;
            readOnly = (Boolean) map.getOrDefault(Option.READ_ONLY, Boolean.FALSE);
            map.remove(Option.READ_ONLY);

            final TransactionOption option;
            if (map.size() == 0) {
                option = JdbdTransactionOption.option(isolation, readOnly, Option.EMPTY_OPTION_FUNC);
            } else {
                option = new JdbdTransactionOption(isolation, readOnly, map::get);
            }
            return option;
        }


    }//OptionBuilder


}
