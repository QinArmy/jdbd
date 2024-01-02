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

import java.nio.file.Path;

/**
 * <p>
 * This interface is only base interface of following :
 *     <ul>
 *         <li>{@link BlobPath}</li>
 *         <li>{@link TextPath}</li>
 *     </ul>
 * <br/>
 *
 * @since 1.0
 */
public interface PathParameter extends LongParameter {

    /**
     * Whether delete file or not when close.
     *
     * @return true : delete file  when close.
     * @see java.nio.file.StandardOpenOption#DELETE_ON_CLOSE
     */
    boolean isDeleteOnClose();

    /**
     * file path
     *
     * @return file path
     */
    @Override
    Path value();


}
