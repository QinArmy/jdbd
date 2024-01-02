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

import io.jdbd.lang.Nullable;
import io.jdbd.result.Cursor;
import io.jdbd.session.ChunkOption;
import io.jdbd.session.DatabaseSession;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import java.util.List;
import java.util.function.Function;

/**
 * <p>
 * This interface representing option of {@link io.jdbd.statement.Statement},
 * and is used by  the implementation of {@link Stmt} .
 * <br/>
 * <p>
 * This interface is base interface of following :
 *     <ul>
 *         <li>the implementation of {@link io.jdbd.statement.Statement} (here ,perhaps not must),it's up to driver vendor.</li>
 *         <li>{@link Stmt}</li>
 *     </ul>
 * <br/>
 */
public interface StmtOption {

    /**
     * time out millSeconds
     *
     * @return millSeconds
     */
    int getTimeout();

    /**
     * Get fetch size
     *
     * @return fetch size
     */
    int getFetchSize();


    /**
     * Get use frequency for helping developer cache server-prepared statement.
     *
     * @return frequency
     * @see io.jdbd.statement.BindSingleStatement#setFrequency(int)
     */
    int getFrequency();

    /**
     * Get stmt var list
     *
     * @return a unmodified list.
     */
    List<NamedValue> getStmtVarList();

    /**
     * Get import function
     *
     * @return nullable
     */
    @Nullable
    Function<ChunkOption, Publisher<byte[]>> getImportFunction();

    /**
     * Get export function
     *
     * @return nullable
     */
    @Nullable
    Function<ChunkOption, Subscriber<byte[]>> getExportFunction();

    /**
     * <p>
     * This method can be useful in following scenarios :
     *     <ul>
     *         <li>create {@link Cursor} from {@link io.jdbd.result.DataRow#get(int, Class)} , for example : PostgreSQL return cursor name</li>
     *         <li>create {@link Cursor} from {@link io.jdbd.result.ResultStates}, for example : PostgreSQL DECLARE cursor command </li>
     *     </ul>
     * <br/>
     *
     * @return the session that create this stmt.
     * @throws UnsupportedOperationException throw when this instance is {@link Stmt} and {@link Stmt#isSessionCreated()} return false.
     */
    DatabaseSession databaseSession();

}
