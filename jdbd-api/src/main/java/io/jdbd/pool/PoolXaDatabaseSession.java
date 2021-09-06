package io.jdbd.pool;

import org.reactivestreams.Publisher;

public interface PoolXaDatabaseSession extends PoolDatabaseSession {

    @Override
    Publisher<PoolXaDatabaseSession> ping(int timeoutSeconds);

    /**
     * @return Publisher that emit this when success.
     */
    @Override
    Publisher<PoolXaDatabaseSession> reset();

}