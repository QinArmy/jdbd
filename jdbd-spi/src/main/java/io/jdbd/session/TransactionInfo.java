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

import javax.annotation.Nullable;

/**
 * <p>
 * This interface representing {@link DatabaseSession}'s transaction status,see {@link DatabaseSession#transactionInfo()}.
 * <br/>
 *
 * @since 1.0
 */
public interface TransactionInfo extends TransactionSpec {


    /**
     * <p>
     * {@link DatabaseSession}'s transaction isolation level.
     * <br/>
     *
     * @return non-null
     */
    @NonNull
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

    static InfoBuilder builder(boolean inTransaction, Isolation isolation, boolean readOnly) {
        return JdbdTransactionInfo.infoBuilder(inTransaction, isolation, readOnly);
    }


    /**
     * <p>Get a {@link TransactionInfo} instance that {@link TransactionInfo#inTransaction()} is false
     * and option is empty.
     */
    static TransactionInfo notInTransaction(Isolation isolation, boolean readOnly) {
        return JdbdTransactionInfo.noInTransaction(isolation, readOnly);
    }


    /**
     * @throws IllegalArgumentException throw when
     *                                  <ul>
     *                                      <li>info is unknown implementation</li>
     *                                      <li>info's {@link TransactionInfo#inTransaction()} is false </li>
     *                                  </ul>
     */
    static TransactionInfo forChain(TransactionInfo info) {
        return JdbdTransactionInfo.forChain(info);
    }

    static TransactionInfo forXaEnd(int flags, TransactionInfo info) {
        return JdbdTransactionInfo.forXaEnd(flags, info);
    }

    static TransactionInfo forXaJoinEnded(int flags, TransactionInfo info) {
        return JdbdTransactionInfo.forXaJoinEnded(flags, info);
    }

    interface InfoBuilder {

        <T> InfoBuilder option(Option<T> option, @Nullable T value);

        /**
         * @throws IllegalArgumentException throw when not in transaction.
         */
        InfoBuilder option(TransactionOption option);

        /**
         * @throws IllegalArgumentException throw when not in transaction.
         */
        InfoBuilder option(Xid xid, int flags, XaStates xaStates, TransactionOption option);


        /**
         * <p>Create a new {@link TransactionInfo} instance.
         * <p><strong>NOTE</strong>: if satisfy following :
         * <ul>
         *     <li>in transaction is true</li>
         *     <li>not found {@link Option#START_MILLIS}</li>
         * </ul>
         * then this method always auto add {@link Option#START_MILLIS}.
         *
         * @throws IllegalStateException throw when in transaction and not found {@link Option#DEFAULT_ISOLATION}.
         */
        TransactionInfo build();

    }


}
