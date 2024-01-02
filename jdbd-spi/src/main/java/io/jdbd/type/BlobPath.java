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

package io.jdbd.type;


import io.jdbd.meta.DataType;
import io.jdbd.result.BigColumnValue;

import java.nio.file.Path;

/**
 * <p>
 * This interface representing the holder of binary file.
 * <br/>
 * <p>
 * Application developer can get the instance of this interface by {@link #from(boolean, Path)} method.
 * <br/>
 *
 * @see io.jdbd.statement.ParametrizedStatement#bind(int, DataType, Object)
 * @see io.jdbd.result.DataRow#get(int, Class)
 * @since 1.0
 */
public interface BlobPath extends PathParameter, BigColumnValue {

    /**
     * create {@link BlobPath} instance.
     *
     * @param deleteOnClose true : should delete after close, see {@link java.nio.file.StandardOpenOption#DELETE_ON_CLOSE}.
     * @param path          non-null
     * @return non-null
     */
    static BlobPath from(boolean deleteOnClose, Path path) {
        return JdbdTypes.blobPathParam(deleteOnClose, path);
    }


}
