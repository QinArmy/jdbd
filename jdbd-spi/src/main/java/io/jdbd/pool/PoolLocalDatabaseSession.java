package io.jdbd.pool;

import io.jdbd.session.DatabaseSessionFactory;
import io.jdbd.session.LocalDatabaseSession;
import org.reactivestreams.Publisher;

import java.time.Duration;

/**
 * <p>
 * The instance of this interface is created by {@link DatabaseSessionFactory#localSession()} method.
 * </p>
 *
 * @since 1.0
 */
public interface PoolLocalDatabaseSession extends PoolDatabaseSession, LocalDatabaseSession {

    /**
     * {@inheritDoc}
     */
    @Override
    Publisher<PoolLocalDatabaseSession> reconnect(Duration duration);

    /**
     * {@inheritDoc}
     */
    @Override
    Publisher<PoolLocalDatabaseSession> ping(int timeoutSeconds);

    /**
     * {@inheritDoc}
     */
    Publisher<PoolLocalDatabaseSession> reset();


}
