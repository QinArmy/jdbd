package io.jdbd.pool;

import io.jdbd.session.DatabaseSession;
import io.jdbd.session.DatabaseSessionFactory;
import org.reactivestreams.Publisher;

/**
 * <p>
 * This interface representing {@link DatabaseSession} than can be pooled.
 * This interface is base interface of following :
 *     <ul>
 *         <li>{@link PoolLocalDatabaseSession}</li>
 *         <li>{@link PoolRmDatabaseSession}</li>
 *     </ul>
 * <br/>
 * <p>
 * The instance of this interface is created by {@link DatabaseSessionFactory}.
 * <br/>
 * <p>
 *     This interface is used by pool vendor,application developer shouldn't use this interface.
 *     Driver developer create the instance of this interface,but driver developer don't use this interface,
 *     because driver developer are not responsible for pooling.
 * <p>
 * <br/>
 *
 * @since 1.0
 */
public interface PoolDatabaseSession extends DatabaseSession {


    Publisher<? extends PoolDatabaseSession> ping();


    /**
     * <p>
     * Reset session :
     * <ul>
     *     <li>reset key session variable , for example :  </li>
     *          <ul>
     *              <li>transaction isolation level</li>
     *              <li>auto commit</li>
     *              <li>server zone</li>
     *              <li>charset</li>
     *              <li>data type output format</li>
     *          </ul>
     *     <li>if database support user-defined data type,then should check whether exists new data type or not.</li>
     * </ul>
     *<br/>
     *
     * @return {@link Publisher} that emit <strong>this</strong> when success.
     */
    Publisher<? extends PoolDatabaseSession> reset();

    /**
     * <p>
     * cancel all have not executed statement,emit {@link io.jdbd.session.SessionCloseException} to the downstream of statement.
     *<br/>
     */
    Publisher<? extends PoolDatabaseSession> logicallyClose();

}
