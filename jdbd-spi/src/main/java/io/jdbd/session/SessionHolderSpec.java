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

package io.jdbd.session;

/**
 * <p>
 * This interface is base interface of following:
 *     <ul>
 *         <li>{@link io.jdbd.meta.DatabaseMetaData}</li>
 *         <li>{@link io.jdbd.statement.Statement}</li>
 *     </ul>
 * <br/>
 *
 * @since 1.0
 */
public interface SessionHolderSpec {

    /**
     * Get hold session
     *
     * @return the {@link DatabaseSession} that create this statement instance.
     */
    DatabaseSession getSession();

    /**
     * Get hold session
     * @param sessionClass  session java class
     * @param <T> session java type
     * @return the {@link DatabaseSession} that create this statement instance.
     * @throws ClassCastException throw when cast error
     */
    <T extends DatabaseSession> T getSession(Class<T> sessionClass);
}
