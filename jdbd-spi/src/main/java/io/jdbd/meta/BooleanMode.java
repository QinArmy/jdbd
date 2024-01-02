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

package io.jdbd.meta;

/**
 * This enum is alternative of {@link Boolean} to avoid {@link NullPointerException}
 *
 * @since 1.p
 */
public enum BooleanMode {

    /**
     * see {@link Boolean#TRUE}
     */
    TRUE,

    /**
     * see {@link Boolean#FALSE}
     */
    FALSE,

    /**
     * representing unknown
     */
    UNKNOWN
}
