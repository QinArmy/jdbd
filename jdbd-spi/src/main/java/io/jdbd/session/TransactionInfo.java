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

import io.jdbd.lang.NonNull;

import java.util.function.Function;

/**
 * <p>
 * This interface representing {@link DatabaseSession}'s transaction status,see {@link DatabaseSession#transactionInfo()}.
 * <br/>
 *
 * @since 1.0
 */
public interface TransactionInfo extends TransactionOption {


    /**
     * <p>
     * {@link DatabaseSession}'s transaction isolation level.
     * <br/>
     *
     * @return non-null
     */
    @NonNull
    @Override
    Isolation isolation();

    /**
     * @return true : {@link DatabaseSession} in transaction block.
     */
    boolean inTransaction();

    /**
     * <p>Application developer can get
     *     <ul>
     *         <li>{@link XaStates}</li>
     *         <li>{@link Xid}</li>
     *         <li>{@code flag} of last phase</li>
     *     </ul>
     * when {@link RmDatabaseSession} in XA transaction block.
     * <p>When {@link #inTransaction()} is true, the value of {@link Option#START_MILLIS} is non-null.
     * <p>When {@link #inTransaction()} is true and  the value of {@link Option#TIMEOUT_MILLIS},  the value of {@link Option#TIMEOUT_MILLIS} is non-null.
     */
    @Override
    <T> T valueOf(Option<T> option);

    static TransactionInfo info(boolean inTransaction, Isolation isolation, boolean readOnly,
                                Function<Option<?>, ?> optionFunc) {
        return JdbdTransactionInfo.create(inTransaction, isolation, readOnly, optionFunc);
    }


    static Function<Option<?>, ?> infoFunc(final TransactionInfo info) {
        return JdbdTransactionInfo.extractFunc(info);
    }

}
