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
 * This interface representing stmt have only one sql,and no parameter placeholder.
 * This implementation of this interface is used by the implementation of below methods:
 * <ul>
 * <li>{@link io.jdbd.statement.StaticStatement#executeUpdate(String)}</li>
 * <li>{@link io.jdbd.statement.StaticStatement#executeQuery(String)}</li>
 * </ul>
 * <br/>
 */
public interface StaticStmt extends SingleStmt {


}
