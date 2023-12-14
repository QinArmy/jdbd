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
