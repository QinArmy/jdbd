package io.jdbd.pool;

import io.jdbd.session.DatabaseSessionFactory;
import io.jdbd.session.LocalDatabaseSession;
import org.reactivestreams.Publisher;

/**
 * <p>
 * The instance of this interface is created by {@link DatabaseSessionFactory#localSession()} method.
 * <br/>
 *
 * @since 1.0
 */
public interface PoolLocalDatabaseSession extends PoolDatabaseSession, LocalDatabaseSession {


    /**
     * {@inheritDoc}
     */
    @Override
    Publisher<PoolLocalDatabaseSession> ping();

    /**
     * {@inheritDoc}
     */
    Publisher<PoolLocalDatabaseSession> reset();

    /**
     * {@inheritDoc}
     */
    @Override
    Publisher<PoolLocalDatabaseSession> logicallyClose();


}
