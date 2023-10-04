package io.jdbd.session;

import io.jdbd.lang.NonNull;

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
     * <p>
     * Application developer can get
     *     <ul>
     *         <li>{@link XaStates}</li>
     *         <li>{@link Xid}</li>
     *         <li>{@code flag} of last phase</li>
     *     </ul>
     *     when {@link RmDatabaseSession} in XA transaction block.
     *<br/>
     */
    @Override
    <T> T valueOf(Option<T> option);


}
