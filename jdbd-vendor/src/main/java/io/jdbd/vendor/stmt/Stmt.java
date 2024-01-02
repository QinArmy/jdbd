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

package io.jdbd.vendor.stmt;

/**
 * <p>
 * This interface representing object that wrap sql and parameter and option(eg: timeout).
 * The implementation of this interface is used by the implementation of {@link io.jdbd.statement.Statement}
 * <br/>
 * <p>
 * This interface is a base interface of :
 * <ul>
 *     <li>{@link StaticStmt}</li>
 *     <li>{@link StaticBatchStmt}</li>
 *     <li>{@link ParamStmt}</li>
 *     <li>{@link ParamBatchStmt}</li>
 *     <li>{@link ParamMultiStmt}</li>
 * </ul>
 *
 * @since 1.0
 */
public interface Stmt extends StmtOption {


    /**
     * this instance whether is created {@link io.jdbd.session.DatabaseSession} or not.
     *
     * @return false : {@link #databaseSession()} always throw {@link UnsupportedOperationException}
     */
    boolean isSessionCreated();

}
