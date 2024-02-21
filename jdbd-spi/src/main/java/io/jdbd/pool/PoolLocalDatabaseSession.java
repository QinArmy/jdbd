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
    Publisher<PoolLocalDatabaseSession> softClose();


}
