package io.jdbd.pool;

import io.jdbd.session.DatabaseSessionFactory;
import io.jdbd.session.RmDatabaseSession;
import org.reactivestreams.Publisher;

/**
 * <p>
 * The instance of this interface is created by {@link DatabaseSessionFactory#rmSession()} method.
 * </p>
 *
 * @since 1.0
 */
public interface PoolRmDatabaseSession extends RmDatabaseSession, PoolDatabaseSession {


    /**
     * {@inheritDoc}
     */
    @Override
    Publisher<PoolRmDatabaseSession> ping(int timeoutSeconds);

    /**
     * {@inheritDoc}
     */
    @Override
    Publisher<PoolRmDatabaseSession> reset();


}
