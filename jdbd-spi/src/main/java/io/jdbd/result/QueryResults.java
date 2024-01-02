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

package io.jdbd.result;


import io.jdbd.statement.MultiResultStatement;
import io.jdbd.statement.StaticStatementSpec;

import java.util.List;

/**
 * <p>
 * <strong>NOTE</strong> : driver don't send message to database server before first subscribing.
 * <br/>
 * <p>This interface instance is crated by following methods:
 * <ul>
 *     <li>{@link MultiResultStatement#executeBatchQuery()}</li>
 *     <li>{@link StaticStatementSpec#executeBatchQuery(List)}</li>
 * </ul>
 *
 * @since 1.0
 */
public interface QueryResults extends MultiResultSpec {


}
