package io.jdbd.session;

import io.jdbd.lang.Nullable;

/**
 * <p>
 * This interface representing transaction option. see
 *     <ul>
 *         <li>{@link LocalDatabaseSession#startTransaction(TransactionOption, HandleMode)}</li>
 *         <li>{@link RmDatabaseSession#start(Xid, int, TransactionOption)}</li>
 *         <li>{@link DatabaseSession#setTransactionCharacteristics(TransactionOption)}</li>
 *     </ul>
 * <br/>
 * <p>
 *     This interface is the base interface of {@link TransactionStatus}.
 * <br/>
 * <p>
 *     Application can get this interface instance by following methods
 *     <ul>
 *         <li>{@link #option(Isolation, boolean)}</li>
 *         <li>{@link #builder()}</li>
 *     </ul>
 * <br/>
 *
 * @since 1.0
 */
public interface TransactionOption extends OptionSpec {

    /**
     * <p>
     * Transaction isolation level,if null,then use default transaction isolation level.
     *<br/>
     *
     * @return nullable {@link Isolation}
     */
    @Nullable
    Isolation isolation();

    /**
     * @return true : transaction is read-only.
     */
    boolean isReadOnly();


    /**
     * <p>
     * override {@link Object#toString()}
     *
     *<br/>
     *
     * @return transaction info, contain
     * <ul>
     *     <li>implementation class name</li>
     *     <li>transaction info</li>
     *     <li>{@link System#identityHashCode(Object)}</li>
     * </ul>
     */
    @Override
    String toString();

    /**
     * <p>
     * Get generic transaction option.
     *<br/>
     *
     * @param isolation nullable {@link Isolation},null representing use default transaction isolation level to start transaction.
     * @param readOnly  true : start read-only transaction.
     */
    static TransactionOption option(@Nullable Isolation isolation, boolean readOnly) {
        return JdbdTransactionOption.option(isolation, readOnly);
    }

    /**
     * Create a builder of {@link TransactionOption} for support more transaction option ,for example transaction name.
     */
    static Builder builder() {
        return JdbdTransactionOption.builder();
    }

    interface Builder {

        /**
         * set transaction option.
         *
         * @param key transaction option key,for example :
         *            <ul>
         *                 <li>{@link Option#ISOLATION}</li>
         *                 <li>{@link Option#READ_ONLY}</li>
         *                 <li>{@link Option#NAME} ,transaction name</li>
         *                 <li>{@link Option#WITH_CONSISTENT_SNAPSHOT}</li>
         *                 <li>{@link Option#DEFERRABLE}</li>
         *                 <li>{@link Option#WAIT}</li>
         *                 <li>{@link Option#LOCK_TIMEOUT}</li>
         *            </ul>
         */
        <T> Builder option(Option<T> key, @Nullable T value);

        /**
         * @throws IllegalArgumentException throw when <ul>
         *                                  <li>the value of {@link Option#READ_ONLY} is null</li>
         *                                  <li>{@link Option#IN_TRANSACTION} exists</li>
         *                                  </ul>
         */
        TransactionOption build() throws IllegalArgumentException;


    }//Builder

}
