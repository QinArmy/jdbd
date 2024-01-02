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


import java.util.function.Consumer;
import java.util.function.Function;

/**
 * <p>
 * This interface representing the current row that result set reader have read from database client protocol.
 * This interface is designed for reducing the instance of {@link ResultRow} in following methods:
 * <ul>
 *     <li>{@link io.jdbd.statement.StaticStatementSpec#executeQuery(String, Function, Consumer)}</li>
 *     <li>{@link io.jdbd.statement.BindSingleStatement#executeQuery(Function, Consumer)}</li>
 *     <li>{@link MultiResult#nextQuery(Function, Consumer)}</li>
 *     <li>{@link QueryResults#nextQuery(Function, Consumer)}</li>
 * </ul>
 * <br/>
 * <p>
 * The {@link #getResultNo()} of this interface always return same value with {@link ResultRowMeta} in same query result.
 * See {@link #getRowMeta()}
 * <br/>
 *
 * @see ResultRow
 * @since 1.0
 */
public interface CurrentRow extends DataRow {

    /**
     * see {@link #asResultRow()}
     */
    Function<CurrentRow, ResultRow> AS_RESULT_ROW = CurrentRow::asResultRow;

    /**
     * row number of current row
     *
     * @return the row number of current row, based 1 . The first value is 1 .
     */
    long rowNumber();

    /**
     * <p>
     * Create one {@link ResultRow} with coping all column data.
     * <br/>
     *
     * @return new {@link ResultRow}
     */
    ResultRow asResultRow();


}
