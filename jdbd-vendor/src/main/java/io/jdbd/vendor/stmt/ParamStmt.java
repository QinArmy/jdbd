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


import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * <p>
 * This interface representing {@link Stmt} have only one sql that has parameter placeholder and isn't batch.
 * The implementation of this interface is used by the implementation of below methods:
 * <ul>
 * <li>{@link io.jdbd.statement.PreparedStatement#executeUpdate()}</li>
 * <li>{@link io.jdbd.statement.PreparedStatement#executeQuery()}</li>
 * <li>{@link io.jdbd.statement.PreparedStatement#executeQuery(Function, Consumer)}</li>
 * <li>{@link io.jdbd.statement.BindStatement#executeUpdate()}</li>
 * <li>{@link io.jdbd.statement.BindStatement#executeQuery()}</li>
 * <li>{@link io.jdbd.statement.BindStatement#executeQuery(Function, Consumer)}</li>
 * </ul>
 * <br/>
 */
public interface ParamStmt extends ParamSingleStmt, SingleStmt {

    /**
     * Get parameter group
     *
     * @return a unmodifiable list
     */
    List<ParamValue> getParamGroup();


}
