package io.jdbd.session;

import java.util.function.Function;

/**
 * <p>
 * This enum representing An XA transaction progresses states.
 * <br/>
 * <p>
 * Application developer can get {@link XaStates} by {@link RmDatabaseSession#transactionStatus()} <br/>
 * when {@link RmDatabaseSession} in XA transaction block. see {@link TransactionStatus#valueOf(Option)}
 * <p>
 * <br/>
 *
 * @see Option#XA_STATES
 * @see RmDatabaseSession#transactionStatus()
 * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/xa-states.html">XA Transaction States</a>
 * @since 1.0
 */
public enum XaStates {

    /**
     * <p>
     * This instance representing XA transaction started after {@link RmDatabaseSession#start(Xid, int, TransactionOption)} method.
     *<br/>
     */
    ACTIVE,

    /**
     * <p>
     * This instance representing XA transaction IDLE after {@link RmDatabaseSession#end(Xid, int, Function)} method.
     *<br/>
     * <p>
     * This states support one-phase commit.
     *<br/>
     */
    IDLE,

    /**
     * <p>
     * This instance representing XA transaction PREPARED after {@link RmDatabaseSession#prepare(Xid, Function)} method.
     *<br/>
     */
    PREPARED;


    @Override
    public final String toString() {
        return String.format("%s.%s", XaStates.class.getName(), this.name());
    }


}
