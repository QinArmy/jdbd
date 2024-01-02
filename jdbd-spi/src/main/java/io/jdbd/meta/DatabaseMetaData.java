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

import io.jdbd.DriverVersion;
import io.jdbd.JdbdException;
import io.jdbd.session.DatabaseMetaSpec;
import io.jdbd.session.Option;
import io.jdbd.session.OptionSpec;
import io.jdbd.session.SessionHolderSpec;
import org.reactivestreams.Publisher;

import java.util.Map;
import java.util.function.Function;

/**
 * <p>
 * This interface provider the methods for database meta data.
 * <br/>
 *
 * @since 1.0
 */
public interface DatabaseMetaData extends DatabaseMetaSpec, SessionHolderSpec, OptionSpec {

    Option<String> CATALOG = Option.from("CATALOG", String.class);


    /**
     * A possible return value for the method
     * {@link  DatabaseMetaData#sqlStateType()} which is used to indicate
     * whether the value returned by the method
     * {@link JdbdException#getSqlState()} is an
     * X/Open (now know as Open Group) SQL CLI SQLSTATE value.
     */
    byte SQL_STATE_X_OPEN = 1;

    /**
     * A possible return value for the method
     * {@link  DatabaseMetaData#sqlStateType()} which is used to indicate
     * whether the value returned by the method
     * {@link JdbdException#getSqlState()} is an SQLSTATE value.
     */
    byte SQL_STATE_SQL = 2;


    /**
     * <ul>
     *     <li>this method return value probably equals {@link #productName()} , For example : MySQL</li>
     *     <li>this method return value probably not equals {@link #productName()} , For example : SQL Server</li>
     * </ul>
     * <strong>NOTE</strong> : this method always return itself product family name,see {@link #supportProductFamily()}
     *
     * @return database product family name. For example : OceanBase, MySQL , PostgreSQL , SQL Server .
     * @see #productName()
     * @see #supportProductFamily()
     */
    String productFamily();


    /**
     * @return database product name,For example :  MySQL , PostgreSQL ,MySQL-ENTERPRISE, PostgreSQL-ENTERPRISE
     * @see #productFamily()
     */
    String productName();

    DriverVersion driverVersion();

    /**
     * <p>
     * Get current schema info of {@link #getSession()}
     * <br/>
     * <p>
     * <strong>NOTE</strong> : driver don't send message to database server before subscribing.
     * <br/>
     *
     * @return the {@link Publisher} emit just one {@link SchemaMeta} or {@link Throwable}, Like {@code reactor.core.publisher.Mono} .
     * @throws JdbdException emit(not throw) when
     *                       <ul>
     *                           <li>session have closed</li>
     *                           <li>network error</li>
     *                           <li>server response error message,see {@link io.jdbd.result.ServerException}</li>
     *                       </ul>
     */
    Publisher<SchemaMeta> currentSchema();

    /**
     * <p>
     * Get schemas info of database.
     * <br/>
     * <p>
     * <strong>NOTE</strong> : driver don't send message to database server before subscribing.
     * <br/>
     * <p>
     * optionFunc at least support following options :
     *     <ul>
     *         <li>{@link #CATALOG},representing catalog name,catalog name can be following format:
     *             <ul>
     *                 <li>contain comma(,) : representing catalog name set,driver will use IN operator</li>
     *                 <li>contain '%' : driver will use LIKE operator.</li>
     *                 <li>simple catalog name : driver will use '=' operator</li>
     *             </ul>
     *         </li>
     *         <li>{@link Option#NAME},representing schema name,schema name can be following format:
     *             <ul>
     *                 <li>contain comma(,) : representing schema name set,driver will use IN operator</li>
     *                 <li>contain '%' : driver will use LIKE operator.</li>
     *                 <li>simple schema name : driver will use '=' operator</li>
     *             </ul>
     *         </li>
     *     </ul>
     * <br/>
     *
     * @param optionFunc func can always return {@code null}
     * @return the {@link Publisher} emit 0-N {@link SchemaMeta} or {@link Throwable}, Like {@code reactor.core.publisher.Flux} .
     * @throws JdbdException emit(not throw) when
     *                       <ul>
     *                           <li>session have closed</li>
     *                           <li>network error</li>
     *                           <li>server response error message,see {@link io.jdbd.result.ServerException}</li>
     *                       </ul>
     */
    Publisher<SchemaMeta> schemas(Function<Option<?>, ?> optionFunc);

    /**
     * <p>
     * Get tables info of current schema.
     * <br/>
     * <p>
     * <strong>NOTE</strong> : driver don't send message to database server before subscribing.
     * <br/>
     * <p>
     * optionFunc at least support following options :
     *     <ul>
     *         <li>{@link Option#NAME},representing table name,table name can be following format:
     *             <ul>
     *                 <li>contain comma(,) : representing table name set,driver will use IN operator</li>
     *                 <li>contain '%' : driver will use LIKE operator.</li>
     *                 <li>simple table name : driver will use '=' operator</li>
     *             </ul>
     *         </li>
     *         <li>{@link Option#TYPE_NAME},representing table type name,table type name can be following format:
     *             <ul>
     *                 <li>contain comma(,) : representing table type name set,driver will use IN operator</li>
     *                 <li>contain '%' : driver will use LIKE operator.</li>
     *                 <li>simple table type name : driver will use '=' operator</li>
     *             </ul>
     *             table type name see {@link TableMeta#valueOf(Option)}
     *         </li>
     *     </ul>
     * <br/>
     *
     * @param optionFunc func can always return {@code null}
     * @return the {@link Publisher} emit 0-N {@link TableMeta} or {@link Throwable}, Like {@code reactor.core.publisher.Flux} .
     * @throws JdbdException emit(not throw) when
     *                       <ul>
     *                           <li>session have closed</li>
     *                           <li>network error</li>
     *                           <li>server response error message,see {@link io.jdbd.result.ServerException}</li>
     *                       </ul>
     */
    Publisher<TableMeta> tablesOfCurrentSchema(Function<Option<?>, ?> optionFunc);

    /**
     * <p>
     * Get tables info of schemaMeta.
     * <br/>
     * <p>
     * <strong>NOTE</strong> : driver don't send message to database server before subscribing.
     * <br/>
     * <p>
     * optionFunc at least support following options :
     *     <ul>
     *         <li>{@link Option#NAME},representing table name,table name can be following format:
     *             <ul>
     *                 <li>contain comma(,) : representing table name set,driver will use IN operator</li>
     *                 <li>contain '%' : driver will use LIKE operator.</li>
     *                 <li>simple table name : driver will use '=' operator</li>
     *             </ul>
     *         </li>
     *         <li>{@link Option#TYPE_NAME},representing table type name,table type name can be following format:
     *             <ul>
     *                 <li>contain comma(,) : representing table type name set,driver will use IN operator</li>
     *                 <li>contain '%' : driver will use LIKE operator.</li>
     *                 <li>simple table type name : driver will use '=' operator</li>
     *             </ul>
     *             table type name see {@link TableMeta#valueOf(Option)}
     *         </li>
     *     </ul>
     * <br/>
     *
     * @param optionFunc func can always return {@code null}
     * @return the {@link Publisher} emit 0-N {@link TableMeta} or {@link Throwable}, Like {@code reactor.core.publisher.Flux} .
     * @throws JdbdException emit(not throw) when
     *                       <ul>
     *                           <li>session have closed</li>
     *                           <li>network error</li>
     *                           <li>server response error message,see {@link io.jdbd.result.ServerException}</li>
     *                       </ul>
     */
    Publisher<TableMeta> tablesOfSchema(SchemaMeta schemaMeta, Function<Option<?>, ?> optionFunc);

    /**
     * <p>
     * Get column info of tableMeta.
     * <br/>
     * <p>
     * <strong>NOTE</strong> : driver don't send message to database server before subscribing.
     * <br/>
     * <p>
     * optionFunc at least support following options :
     *     <ul>
     *         <li>{@link Option#NAME},representing column name,column name can be following format:
     *             <ul>
     *                 <li>contain comma(,) : representing column name set,driver will use IN operator</li>
     *                 <li>contain '%' : driver will use LIKE operator.</li>
     *                 <li>simple column name : driver will use '=' operator</li>
     *             </ul>
     *         </li>
     *     </ul>
     * <br/>
     *
     * @param tableMeta  non-null
     * @param optionFunc func can always return {@code null}
     * @return the {@link Publisher} emit 0-N {@link TableColumnMeta} or {@link Throwable}, Like {@code reactor.core.publisher.Flux} .
     * @throws JdbdException emit(not throw) when
     *                       <ul>
     *                           <li>session have closed</li>
     *                           <li>network error</li>
     *                           <li>server response error message,see {@link io.jdbd.result.ServerException}</li>
     *                       </ul>
     */
    Publisher<TableColumnMeta> columnsOfTable(TableMeta tableMeta, Function<Option<?>, ?> optionFunc);

    /**
     * <p>
     * Get index info of tableMeta.
     * <br/>
     * <p>
     * <strong>NOTE</strong> : driver don't send message to database server before subscribing.
     * <br/>
     * <p>
     * optionFunc at least support following options :
     *     <ul>
     *         <li>{@link Option#NAME},representing index name,index name can be following format:
     *             <ul>
     *                 <li>contain comma(,) : representing index name set,driver will use IN operator</li>
     *                 <li>contain '%' : driver will use LIKE operator.</li>
     *                 <li>simple index name : driver will use '=' operator</li>
     *             </ul>
     *         </li>
     *     </ul>
     * <br/>
     *
     * @param tableMeta  non-null
     * @param optionFunc func can always return {@code null}
     * @return the {@link Publisher} emit 0-N {@link TableIndexMeta} or {@link Throwable}, Like {@code reactor.core.publisher.Flux} .
     * @throws JdbdException emit(not throw) when
     *                       <ul>
     *                           <li>session have closed</li>
     *                           <li>network error</li>
     *                           <li>server response error message,see {@link io.jdbd.result.ServerException}</li>
     *                       </ul>
     */
    Publisher<TableIndexMeta> indexesOfTable(TableMeta tableMeta, Function<Option<?>, ?> optionFunc);


    /**
     * <p>
     * Get the info of option
     * <br/>
     * <p>
     * <strong>NOTE</strong> : driver don't send message to database server before subscribing.
     * <br/>
     * <p>
     * This implementation of this method at least support following :
     *     <ul>
     *         <li>{@link Option#USER} : representing current user info of session,now the {@link Publisher} emit just one element or {@link Throwable}, Like {@code reactor.core.publisher.Mono} . </li>
     *     </ul>
     * <br/>
     *
     * @param option option key
     * @param <R>    option value java type
     * @return non-null
     * @throws JdbdException emit(not throw) when
     *                       <ul>
     *                           <li>session have closed</li>
     *                           <li>network error</li>
     *                           <li>server response error message,see {@link io.jdbd.result.ServerException}</li>
     *                       </ul>
     */
    <R> Publisher<R> queryOption(Option<R> option);

    /**
     * <p>
     * Get database key words
     * <br/>
     * <p>
     * <strong>NOTE</strong> : driver don't send message to database server before subscribing.
     * <br/>
     *
     * @param onlyReserved true : just only query reserved key words.
     * @return the {@link Publisher} emit just one {@link SchemaMeta} or {@link Throwable}, Like {@code reactor.core.publisher.Mono}.
     * The map is unmodified, key : upper case key word ; value : true representing reserved.
     * @throws JdbdException emit(not throw) when
     *                       <ul>
     *                           <li>session have closed</li>
     *                           <li>network error</li>
     *                           <li>server response error message,see {@link io.jdbd.result.ServerException}</li>
     *                       </ul>
     */
    Publisher<Map<String, Boolean>> sqlKeyWords(boolean onlyReserved);

    /**
     * quoting string or a space
     *
     * @return the quoting string or a space if quoting is not supported
     */
    String identifierQuoteString();


    /**
     * Indicates whether the SQLSTATE returned by {@link  JdbdException#getSqlState()}
     * is X/Open (now known as Open Group) SQL CLI or SQL:2003.
     *
     * @return the type of SQLSTATE; one of:
     * <ul>
     *     <li>{@link #SQL_STATE_X_OPEN}</li>
     *     <li>{@link #SQL_STATE_SQL}</li>
     * </ul>
     *        sqlStateXOpen or
     *        sqlStateSQL
     * @since 1.4
     */
    int sqlStateType() throws JdbdException;


}
