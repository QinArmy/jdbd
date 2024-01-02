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


import io.jdbd.session.ChunkOption;
import io.jdbd.session.DatabaseSession;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import java.util.function.Function;

/**
 * <p>
 * This interface representing adapter of:
 *     <ul>
 *         <li>{@link ParamStmt}</li>
 *         <li>{@link ParamBatchStmt}</li>
 *     </ul>
 *     The implementation of this interface is used by underlying implementation of {@link io.jdbd.statement.PreparedStatement}.
 * <br/>
 *
 * @see DatabaseSession#prepareStatement(String)
 */
public interface PrepareStmt extends ParamSingleStmt {

    /**
     * @throws IllegalStateException when no actual {@link ParamSingleStmt}
     */
    ParamSingleStmt getStmt();

    /**
     * @throws IllegalStateException throw when {@link #getStmt()} throw {@link IllegalStateException}.
     */
    @Override
    int getTimeout();

    /**
     * @throws IllegalStateException throw when {@link #getStmt()} throw {@link IllegalStateException}.
     * @see ParamBatchStmt#getFetchSize()
     */
    @Override
    int getFetchSize();

    /**
     * @throws IllegalStateException throw when {@link #getStmt()} throw {@link IllegalStateException}.
     * @see ParamBatchStmt#getImportFunction()
     */
    @Override
    Function<ChunkOption, Publisher<byte[]>> getImportFunction();

    /**
     * @throws IllegalStateException throw when {@link #getStmt()} throw {@link IllegalStateException}.
     * @see ParamBatchStmt#getExportFunction()
     */
    @Override
    Function<ChunkOption, Subscriber<byte[]>> getExportFunction();


}
