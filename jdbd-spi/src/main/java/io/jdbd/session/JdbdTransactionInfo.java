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

import io.jdbd.util.JdbdUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Function;

final class JdbdTransactionInfo implements TransactionInfo {


    static JdbdTransactionInfo create(final boolean inTransaction, final @Nullable Isolation isolation,
                                      final boolean readOnly, final @Nullable Function<Option<?>, ?> optionFunc) {
        if (isolation == null || optionFunc == null) {
            throw new NullPointerException();
        }

        final XaStates states;
        if (optionFunc.apply(Option.XID) != null) {
            switch (states = (XaStates) optionFunc.apply(Option.XA_STATES)) {
                case ACTIVE:
                case IDLE: {
                    if (!inTransaction) {
                        throw new IllegalArgumentException("inTransaction error");
                    }
                }
                break;
                case PREPARED: {
                    if (inTransaction) {
                        throw new IllegalArgumentException("inTransaction error");
                    }
                }
                break;
                default:
                    throw new IllegalArgumentException(String.format("unknown %s", states));
            }
        }

        if (inTransaction && optionFunc.apply(Option.START_MILLIS) == null) {
            String m = String.format("inTransaction : true ,but %s is null", Option.START_MILLIS);
            throw new IllegalArgumentException(m);
        }
        return new JdbdTransactionInfo(inTransaction, isolation, readOnly, optionFunc);
    }

    static Function<Option<?>, ?> extractFunc(final TransactionInfo info) {
        if (info instanceof JdbdTransactionInfo) {
            return ((JdbdTransactionInfo) info).function;
        }
        return info::valueOf;
    }


    private final boolean inTransaction;

    private final Isolation isolation;

    private final boolean readOnly;

    private final Function<Option<?>, ?> function;

    private JdbdTransactionInfo(boolean inTransaction, Isolation isolation, boolean readOnly,
                                Function<Option<?>, ?> function) {
        this.inTransaction = inTransaction;
        this.isolation = isolation;
        this.readOnly = readOnly;
        this.function = function;
    }


    @Override
    public boolean inTransaction() {
        return this.inTransaction;
    }

    @Nonnull
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
    public <T> T valueOf(final @Nullable Option<T> option) {
        final Object value;
        if (option == null) {
            value = null;
        } else if (option == Option.IN_TRANSACTION) {
            value = this.inTransaction;
        } else if (option == Option.ISOLATION) {
            value = this.isolation;
        } else if (option == Option.READ_ONLY) {
            value = this.readOnly;
        } else if (this.function == Option.EMPTY_OPTION_FUNC) {
            value = null;
        } else {
            value = this.function.apply(option);
        }
        if (option != null && option.javaType().isInstance(value)) {
            return (T) value;
        }
        return null;
    }


    @Override
    public String toString() {
        return JdbdUtils.builder(88)
                .append(getClass().getName())
                .append("[name:")
                .append(valueOf(Option.NAME))
                .append(",inTransaction:")
                .append(this.inTransaction)
                .append(",isolation")
                .append(this.isolation.name())
                .append(",readOnly")
                .append(this.readOnly)
                .append(",hash:")
                .append(System.identityHashCode(this))
                .append(",label:")
                .append(valueOf(Option.LABEL))
                .append(']')
                .toString();
    }


}
