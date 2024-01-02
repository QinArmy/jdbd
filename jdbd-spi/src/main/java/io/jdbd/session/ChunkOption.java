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

import java.nio.charset.Charset;
import java.util.function.Function;

/**
 * <p>
 * This interface is designed for following :
 *     <ul>
 *         <li>{@link io.jdbd.statement.Statement#setImportPublisher(Function)}</li>
 *         <li>{@link io.jdbd.statement.Statement#setExportSubscriber(Function)}</li>
 *     </ul>
 * <br/>
 * <p>
 *      Currently only one method,maybe add new method in the future.
 * <br/>
 *
 * @since 1.0
 */
public interface ChunkOption extends OptionSpec {

    /**
     * <p>
     * If chunk is binary , the {@link Charset} should be {@link java.nio.charset.StandardCharsets#ISO_8859_1}
     * <br/>
     *
     * @return non-null
     */
    Charset charset();


}
