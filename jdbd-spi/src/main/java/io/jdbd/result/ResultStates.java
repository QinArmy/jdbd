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


import io.jdbd.JdbdException;
import io.jdbd.lang.Nullable;
import io.jdbd.session.DatabaseSession;
import io.jdbd.session.Option;
import io.jdbd.session.OptionSpec;
import io.jdbd.statement.Statement;

import java.util.function.Consumer;

/**
 * <p>
 * The interface representing the states of the result of sql statement (eg: SELECT/INSERT/UPDATE/DELETE).
 *     <ul>
 *         <li>If {@link #hasColumn()} is true ,then this instance representing the terminator of query result (eg: SELECT command)</li>
 *         <li>Else this instance representing the update result (eg: INSERT/UPDATE/DELETE command)</li>
 *     </ul>
 * <br/>
 * <p>
 *  The instance of this interface always is the last item of same query result in the {@link OrderedFlux}.
 * <br/>
 * <p>
 * The {@link #resultNo()} of this interface always return same value with {@link ResultRowMeta} in same query result.
 * <br/>
 *
 * @see ResultRowMeta
 * @see ResultRow
 * @since 1.0
 */
public interface ResultStates extends ResultItem, OptionSpec {

    Consumer<ResultStates> IGNORE_STATES = states -> {
    };

    int batchSize();

    /**
     * @return <ul>
     * <li>If {@link #batchSize()} is 0, then 0</li>
     * <li>Else batch No (based 1)</li>
     * </ul>
     */
    int batchNo();

    /**
     * Whether support {@link #lastInsertedId()} method or not.
     * <p>If false ,then database usually support RETURNING clause,it better than lastInsertedId ,for example : PostgreSQL
     *
     * @return true : support
     */
    boolean isSupportInsertId();


    /**
     * <p>The state usually is returned database server by database client protocol.
     * For example :
     * <ul>
     *     <li>MySQL <a href="https://dev.mysql.com/doc/dev/mysql-server/latest/page_protocol_basic_ok_packet.html">Protocol::OK_Packet</a> </li>
     *     <li>MySQL <a href="https://dev.mysql.com/doc/dev/mysql-server/latest/page_protocol_basic_eof_packet.html">Protocol::EOF_Packet</a></li>
     *     <li>PostgreSQL <a href="https://www.postgresql.org/docs/current/protocol-message-formats.html">ReadyForQuery (B)</a></li>
     *     <li>Micro SQL server transactional state</li>
     * </ul>
     * <p>Some database have to parse sql to implement this method.
     *
     * @return true : session in transaction block when statement end.
     * @see DatabaseSession#inTransaction()
     */
    boolean inTransaction();

    /**
     * statement affected rows
     *
     * @return statement affected rows
     */
    long affectedRows();

    /**
     * the last inserted id, a unsigned long .
     * <strong>NOTE</strong>:
     * <ul>
     *     <li>when {@link #isSupportInsertId()} is false,throw {@link JdbdException} . now database usually support RETURNING clause,it better than lastInsertedId ,for example : PostgreSQL</li>
     *     <li>when use multi-row insert syntax ,the last inserted id is the first row id.</li>
     *     <li>If you use multi-row insert syntax and exists conflict clause(e.g : MySQL ON DUPLICATE KEY UPDATE),then database never return correct lastInsertedId</li>
     * </ul>
     *
     * @return the last inserted id
     * @throws JdbdException throw when {@link #isSupportInsertId()} return false
     */
    long lastInsertedId() throws JdbdException;

    /**
     * the info about statement execution.
     *
     * @return empty or  success info(maybe contain warning info)
     */
    String message();


    /**
     * Whether exists more result after this result or not .
     * <ul>
     *     <li>simple single statement always false</li>
     *     <li>multi-result statement,for example stored procedure ,multi-statement ,last result is false,before last result is true</li>
     *     <li>batch statement,for example,batch update/query  ,last batch item is false,before last batch item is true</li>
     * </ul>
     *
     * @return true : exists more result after this result
     */
    boolean hasMoreResult();


    /**
     * exists more fetch
     *
     * @return true representing exists server cursor and the last row don't send.
     * @see Statement#setFetchSize(int)
     */
    boolean hasMoreFetch();

    /**
     * <p>
     * The result whether exists column or not.
     * </p>
     *
     * @return <ul>
     * <li>true : this instance representing the terminator of query result (eg: SELECT command)</li>
     * <li>false : this instance representing the update result (eg: INSERT/UPDATE/DELETE command)</li>
     * </ul>
     */
    boolean hasColumn();

    /**
     * <p>
     * Current result row count.
     * </p>
     *
     * @return the row count.<ul>
     * <li>If use fetch (eg: {@link Statement#setFetchSize(int)} , {@link Cursor}) , then the row count representing only the row count of current fetch result.</li>
     * <li>Else then the row count representing the total row count of query result.</li>
     * </ul>
     */
    long rowCount();


    /**
     * warn info
     *
     * @return nullable warn info
     */
    @Nullable
    Warning warning();

    /**
     * <p>
     * This the implementation <strong>perhaps</strong> support some of  following :
     *     <ul>
     *         <li>{@link Option#IN_TRANSACTION}</li>
     *         <li>{@link Option#READ_ONLY} </li>
     *         <li>{@link Option#AUTO_COMMIT}</li>
     *         <li>{@link Option#CURSOR}</li>
     *     </ul>
     * <br/>
     *
     * @param option option key
     * @param <T>    option value java type
     * @return nullable option value
     */
    @Nullable
    @Override
    <T> T valueOf(Option<T> option);

}
