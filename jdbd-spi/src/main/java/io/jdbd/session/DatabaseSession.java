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

import io.jdbd.JdbdException;
import io.jdbd.lang.Nullable;
import io.jdbd.meta.DataType;
import io.jdbd.meta.DatabaseMetaData;
import io.jdbd.result.Cursor;
import io.jdbd.result.ResultStates;
import io.jdbd.statement.*;
import io.jdbd.util.EscapeMode;
import io.jdbd.util.NameMode;
import org.reactivestreams.Publisher;

import java.util.Map;
import java.util.function.Function;

/**
 * <p>
 * This interface representing database session.
 * <br/>
 * <p>
 * This interface is is similar to {@code java.sql.Connection} or {@code javax.sql.XAConnection}, except that this interface is reactive.
 * <br/>
 * <p>
 * The instance of this interface is created by {@link DatabaseSessionFactory}.
 * <br/>
 * <p>
 * This interface extends {@link StaticStatementSpec} , so this interface can execute static statement without any statement option. eg: timeout.
 * <br/>
 * <p>
 * This interface is base interface of following :
 *     <ul>
 *         <li>{@link LocalDatabaseSession}</li>
 *         <li>{@link RmDatabaseSession}</li>
 *         <li>{@link io.jdbd.pool.PoolLocalDatabaseSession}</li>
 *         <li>{@link io.jdbd.pool.PoolRmDatabaseSession}</li>
 *     </ul>
 * <br/>
 * <p>
 *     Application developer can create statement by following methods :
 *     <ul>
 *         <li>{@link #statement()} ,create static statement.</li>
 *         <li>{@link #prepareStatement(String)} , create server-prepare statement</li>
 *         <li>{@link #bindStatement(String)} , create the adaptor of client-prepared statement and server-prepared statement.</li>
 *         <li>{@link #bindStatement(String, boolean)}, create the adaptor of client-prepared statement and server-prepared statement.</li>
 *         <li>{@link #multiStatement()}, create multi-statement</li>
 *     </ul>
 * <br/>
 *
 * @since 1.0
 */
public interface DatabaseSession extends StaticStatementSpec, DatabaseMetaSpec, OptionSpec, Closeable {

    String name();

    /**
     * session factory name
     *
     * @return {@link DatabaseSessionFactory#name()}
     * @see DatabaseSessionFactory#name()
     */
    String factoryName();


    /**
     * <p>
     * Session identifier(non-unique, for example : database server cluster),probably is following :
     *     <ul>
     *         <li>server process id</li>
     *         <li>server thread id</li>
     *         <li>other identifier</li>
     *     </ul>
     *     <strong>NOTE</strong>: identifier will probably be updated if reconnect.
     * <br/>
     *
     * @return session identifier
     * @throws JdbdException throw when session have closed.
     */
    long sessionIdentifier() throws JdbdException;

    /**
     * <p>
     * This method create one {@link DatabaseMetaData} instance.
     * <br/>
     *
     * @return {@link DatabaseMetaData} instance
     * @throws JdbdException throw when session have closed.
     */
    DatabaseMetaData databaseMetaData() throws JdbdException;


    /**
     * <p>This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // session is instance of DatabaseSession
     *             session.transactionInfo(Option.EMPTY_OPTION_FUNC) ;
     *         </code>
     * </pre>
     */
    Publisher<TransactionInfo> transactionInfo();


    /**
     * <p>Get current transaction info,If session in transaction block,then return current transaction info; else equivalent to {@link #sessionTransactionCharacteristics(Function)}.
     *
     * @param optionFunc dialect option,always support {@link Option#SQL_LOGGER}. see {@link Option#EMPTY_OPTION_FUNC}
     * @return the {@link Publisher} emit just one {@link TransactionInfo} or {@link Throwable}, Like {@code reactor.core.publisher.Mono} .
     * @throws JdbdException emit(not throw) when
     *                        <ul>
     *                           <li>network error</li>
     *                           <li>sever response error message,see {@link io.jdbd.result.ServerException}</li>
     *                           <li>satisfy following all conditions:
     *                               <ol>
     *                                   <li>{@link #inTransaction()} return true</li>
     *                                   <li>you don't use jdbd spi control transaction</li>
     *                                   <li>you use sql control transaction</li>
     *                                   <li>database don't support to get current some transaction characteristic,for example:
     *                                      <ul>
     *                                          <li>MySQL don't support to get current transaction isolation level and WITH CONSISTENT SNAPSHOT</li>
     *                                          <li>MySQL don't support to get current xa transaction states,see {@link XaStates}</li>
     *                                          <li>PostgreSQL don't support to get current xa transaction states,see {@link XaStates}</li>
     *                                      </ul>
     *                                   </li>
     *                               </ol>
     *                           </li>
     *                       </ul>
     */
    Publisher<TransactionInfo> transactionInfo(Function<Option<?>, ?> optionFunc);


    /**
     * <p>This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // session is instance of DatabaseSession
     *             session.sessionTransactionCharacteristics(Option.EMPTY_OPTION_FUNC) ;
     *         </code>
     * </pre>
     */
    Publisher<TransactionInfo> sessionTransactionCharacteristics();


    /**
     * <p>Query session-level transaction characteristics info
     * <p><strong>NOTE</strong> : driver don't send message to database server before subscribing.
     *
     * @param optionFunc dialect option,always support {@link Option#SQL_LOGGER}. see {@link Option#EMPTY_OPTION_FUNC}
     * @return the {@link Publisher} emit just one {@link TransactionInfo} or {@link Throwable}, Like {@code reactor.core.publisher.Mono} .
     * <p><strong>NOTE</strong> : the {@link TransactionInfo#inTransaction()} always is false,even if session in transaction block.
     * @throws JdbdException emit(not throw) when
     *                        <ul>
     *                           <li>network error</li>
     *                           <li>sever response error message,see {@link io.jdbd.result.ServerException}</li>
     *                       </ul>
     * @see #setTransactionCharacteristics(TransactionOption)
     */
    Publisher<TransactionInfo> sessionTransactionCharacteristics(Function<Option<?>, ?> optionFunc);


    /**
     * <p>
     * Crete one static statement instance.
     * <br/>
     * <p>
     * This method don't check session whether open or not.
     * <br/>
     *
     * @return {@link StaticStatement} instance
     */
    StaticStatement statement();

    /**
     * <p>
     * This method create one server-prepared statement.
     * This method is similarly to {@code java.sql.Connection#prepareStatement(String)}
     * except that is async emit a {@link PreparedStatement}.
     * <br/>
     * <p>
     * {@link PreparedStatement} is designed for providing following methods:
     *     <ul>
     *         <li>{@link PreparedStatement#paramTypeList()}</li>
     *         <li>{@link PreparedStatement#resultRowMeta()}</li>
     *         <li>{@link PreparedStatement#waring()}</li>
     *     </ul>
     *     , so if you don't need above methods, then you can use {@link #bindStatement(String, boolean)}.
     * <br/>
     * <p>
     * <strong>NOTE</strong> : driver don't send message to database server before subscribing.
     * <br/>
     *
     * @param sql must have text
     * @return the {@link Publisher} emit just one {@link PreparedStatement} instance or {@link Throwable}. Like {@code reactor.core.publisher.Mono}
     * @throws JdbdException emit(not throw)
     *                       <ul>
     *                           <li>sql error</li>
     *                           <li>session have closed</li>
     *                           <li>network error</li>
     *                           <li>database server response error message , see {@link io.jdbd.result.ServerException}</li>
     *                       </ul>
     */
    Publisher<PreparedStatement> prepareStatement(String sql);


    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // session is instance of DatabaseSession
     *             session.bindStatement(sql,false) ;
     *         </code>
     *     </pre>
     * <br/>
     *
     * @param sql must have text
     * @return {@link BindStatement} instance
     * @see #bindStatement(String, boolean)
     */
    BindStatement bindStatement(String sql);

    /**
     * <p>
     * Create the statement that is the adaptor of client-prepared statement and server-prepared statement.
     * <br/>
     * <p>
     * This method don't check session whether open or not.
     * <br/>
     *
     * @param sql                 have text sql.
     * @param forceServerPrepared <ul>
     *                            <li>true :  must use server-prepared.</li>
     *                            <li>false : use client-prepared if can  </li>
     *                            </ul>
     * @return {@link BindStatement} instance
     * @throws IllegalArgumentException throw when only sql have no text.
     * @see BindStatement#isForcePrepare()
     */
    BindStatement bindStatement(String sql, boolean forceServerPrepared);

    /**
     * <p>
     * Create one multi statement.
     * <br/>
     * <p>
     * This method don't check session whether open or not.
     * <br/>
     *
     * @return {@link MultiStatement} instance
     * @throws JdbdException throw when : {@link #isSupportMultiStatement()} return false.
     */
    MultiStatement multiStatement() throws JdbdException;

    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // session is instance of DatabaseSession
     *             session.refCursor(name,option -> null) ;
     *         </code>
     *     </pre>
     * <br/>
     *
     * @param name see {@link #refCursor(String, Function)}
     * @return {see {@link #refCursor(String, Function)}
     * @throws JdbdException see {@link #refCursor(String, Function)}
     * @see #refCursor(String, Function)
     */
    Cursor refCursor(String name) throws JdbdException;

    /**
     * <p>
     * Create a instance of {@link Cursor}. This method don't check session open ,don't check name whether exists or not in database.
     * <br/>
     * <p>
     * If {@link #isSupportRefCursor()} return true,then driver must support following :
     *     <ul>
     *         <li>{@link Option#AUTO_CLOSE_ON_ERROR}</li>
     *     </ul>
     * <br/>
     *
     * @param name       must have text
     * @param optionFunc dialect option,always support {@link Option#SQL_LOGGER}. see {@link Option#EMPTY_OPTION_FUNC}
     * @return {@link Cursor}  instance
     * @throws IllegalArgumentException throw when name have no text.
     * @throws JdbdException            throw when {@link #isSupportRefCursor()} return false.
     */
    Cursor refCursor(String name, Function<Option<?>, ?> optionFunc) throws JdbdException;


    /**
     * <p>Just set the characteristics of session transaction, don't start transaction.
     * <p>Sets the default transaction characteristics for subsequent transactions of this session.<br/>
     * These defaults can be overridden by {@link LocalDatabaseSession#startTransaction(TransactionOption, HandleMode)}
     * for an individual transaction.
     * <p>The transaction options (eg: {@link Isolation}) can apply all transaction of session.
     * <p>The implementation of this method <strong>always</strong> support {@link Option#SQL_LOGGER}.
     * <p>The implementation of this method <strong>perhaps</strong> support some of following :
     *     <ul>
     *         <li>{@link Option#WITH_CONSISTENT_SNAPSHOT}</li>
     *         <li>{@link Option#DEFERRABLE}</li>
     *         <li>{@link Option#NAME}</li>
     *     </ul>
     * <br/>
     *
     * @param option non-null transaction option, driver perhaps support dialect transaction option by {@link TransactionOption#valueOf(Option)}.
     * @return emit <strong>this</strong> or {@link Throwable}. Like {@code reactor.core.publisher.Mono}.
     * @throws JdbdException emit(not throw) when
     *                       <ul>
     *                           <li>appropriate {@link Isolation} isn't supported</li>
     *                           <li>session have closed, see {@link SessionCloseException}</li>
     *                           <li>network error</li>
     *                           <li>server response error message, see {@link io.jdbd.result.ServerException}</li>
     *                       </ul>
     * @see LocalDatabaseSession#startTransaction(TransactionOption, HandleMode)
     * @see #transactionInfo()
     * @see #sessionTransactionCharacteristics(Function)
     */
    Publisher<? extends DatabaseSession> setTransactionCharacteristics(TransactionOption option);


    /**
     * <p>Test session whether in transaction block or not.
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
     * @return true : when
     * <ul>
     *     <li>{@link LocalDatabaseSession}  in local transaction block after last statement executing.</li>
     *     <li>{@link RmDatabaseSession}'s {@link XaStates} is one of
     *          <ul>
     *              <li>{@link XaStates#ACTIVE}</li>
     *              <li>{@link XaStates#IDLE}</li>
     *          </ul>
     *           after last statement executing.
     *     </li>
     * </ul>
     * @throws JdbdException throw when session have closed.
     * @see ResultStates#inTransaction()
     */
    boolean inTransaction() throws JdbdException;


    /**
     * <p>This method is equivalent to following :
     * <pre><br/>
     *     <code>
     *         // session is the instance of {@link DatabaseSession}
     *         return session.setSavePoint(option-> null)
     *     </code>
     * </pre>
     *
     * @return see {@link #setSavePoint(Function)}
     */
    Publisher<SavePoint> setSavePoint();

    /**
     * Driver create one save point identifier and set a save point.
     * <p><strong>NOTE</strong> : driver don't send message to database server before subscribing.
     *
     * @param optionFunc dialect option,always support :
     *                   <ul>
     *                       <li>{@link Option#NAME} , save point name</li>
     *                       <li>{@link Option#SQL_LOGGER}</li>
     *                   </ul>
     * @return {@link Publisher} emit just one {@link SavePoint},like {@code reactor.core.publisher.Mono}
     * @throws JdbdException emit(not throw) when
     *                       <ul>
     *                           <li>name error</li>
     *                           <li>session have closed,see {@link SessionCloseException}</li>
     *                           <li>server response error message,see {@link io.jdbd.result.ServerException}</li>
     *                       </ul>
     */
    Publisher<SavePoint> setSavePoint(Function<Option<?>, ?> optionFunc);


    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // session is instance of {@link DatabaseSession}
     *             session.releaseSavePoint(savepoint,option->null) ;
     *         </code>
     *     </pre>
     *
     * @param savepoint see {@link #releaseSavePoint(SavePoint, Function)}
     * @return see {@link #releaseSavePoint(SavePoint, Function)}
     * @see #releaseSavePoint(SavePoint, Function)
     */
    Publisher<? extends DatabaseSession> releaseSavePoint(SavePoint savepoint);


    /**
     * <p>
     * <strong>NOTE</strong> : driver don't send message to database server before subscribing.
     * <br/>
     *
     * @param savepoint  non-null
     *@param optionFunc dialect option,always support {@link Option#SQL_LOGGER}. see {@link Option#EMPTY_OPTION_FUNC}
     * @return the {@link Publisher} that completes successfully by
     * emitting an element(<strong>this</strong>), or with an error. Like {@code  reactor.core.publisher.Mono}
     * @throws JdbdException throw when
     *                       <ul>
     *                           <li>driver couldn't recognise savepoint</li>
     *                           <li>session have closed,see {@link SessionCloseException}</li>
     *                           <li>server response error message,see {@link io.jdbd.result.ServerException}</li>
     *                       </ul>
     */
    Publisher<? extends DatabaseSession> releaseSavePoint(SavePoint savepoint, Function<Option<?>, ?> optionFunc);


    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // session is instance of {@link DatabaseSession}
     *             session.rollbackToSavePoint(savepoint,option->null) ;
     *         </code>
     *     </pre>
     *
     * @param savepoint {@link #rollbackToSavePoint(SavePoint, Function)}
     * @return {@link #rollbackToSavePoint(SavePoint, Function)}
     */
    Publisher<? extends DatabaseSession> rollbackToSavePoint(SavePoint savepoint);

    /**
     * rollback save point
     * <p>
     * <strong>NOTE</strong> : driver don't send message to database server before subscribing.
     * <br/>
     *
     * @param savepoint  non-null
     *@param optionFunc dialect option,always support {@link Option#SQL_LOGGER}. see {@link Option#EMPTY_OPTION_FUNC}
     * @return the {@link Publisher} that completes successfully by
     * emitting an element(<strong>this</strong>), or with an error. Like {@code  reactor.core.publisher.Mono}
     * @throws JdbdException throw when
     *                       <ul>
     *                           <li>driver couldn't recognise savepoint</li>
     *                           <li>session have closed,see {@link SessionCloseException}</li>
     *                           <li>server response error message,see {@link io.jdbd.result.ServerException}</li>
     *                       </ul>
     */
    Publisher<? extends DatabaseSession> rollbackToSavePoint(SavePoint savepoint, Function<Option<?>, ?> optionFunc);

    /**
     * <p>
     * append text literal to builder,driver will quote and escape if need.
     * <br/>
     * <p>
     * <strong>NOTE</strong>: driver don't append space before literal.
     * <br/>
     * @param type data type of value
     * @param value    nullable value
     * @param builder target builder
     * @return <strong>this</strong>
     * @throws JdbdException throw when the implementation of this method need session is open and session have closed
     */
    DatabaseSession appendLiteral(DataType type, @Nullable Object value, EscapeMode mode, StringBuilder builder) throws JdbdException;

    /**
     * <p>
     * append text identifier to builder,driver will quote and escape if need.
     * <br/>
     * <p>
     * <strong>NOTE</strong>: driver don't append space before identifier.
     * <br/>
     *
     * @param identifier non-null,for example : column label
     * @param builder    target builder
     * @return <strong>this</strong>
     * @throws JdbdException throw when
     *                       <ul>
     *                           <li>the implementation of this method need session is open and session have closed</li>
     *                           <li>identifier have no text</li>
     *                           <li>identifier contain identifier quote char and driver couldn't escape.</li>
     *                       </ul>
     */
    DatabaseSession appendIdentifier(String identifier, StringBuilder builder) throws JdbdException;

    /**
     * <p>
     * append tableName to builder,driver will quote and escape if need.
     * <br/>
     * <p>
     * <strong>NOTE</strong>: driver don't append space before tableName.
     * <br/>
     *
     * @param tableName non-null
     * @param mode      tableName handle mode,if tableName must be quoted and escaped,then mode will be ignored ,else :
     *                  <ul>
     *                       <li>{@link NameMode#LOWER_CASE} append lower case tableName</li>
     *                       <li>{@link NameMode#UPPER_CASE} append upper case tableName</li>
     *                       <li>{@link NameMode#DEFAULT} append tableName</li>
     *                  </ul>
     * @param builder   target builder
     * @return <strong>this</strong>
     * @throws JdbdException throw when
     *                       <ul>
     *                           <li>the implementation of this method need session is open and session have closed</li>
     *                           <li>tableName contain identifier quote char and driver couldn't escape.</li>
     *                           <li>tableName is illegal</li>
     *                       </ul>
     */
    DatabaseSession appendTableName(String tableName, NameMode mode, StringBuilder builder) throws JdbdException;

    /**
     * <p>
     * append columnName to builder,driver will quote and escape if need.
     * <br/>
     * <p>
     * <strong>NOTE</strong>: driver don't append space before columnName.
     * <br/>
     *
     * @param columnName non-null
     * @param mode       columnName handle mode,if columnName must be quoted and escaped,then mode will be ignored ,else :
     *                   <ul>
     *                        <li>{@link NameMode#LOWER_CASE} append lower case columnName</li>
     *                        <li>{@link NameMode#UPPER_CASE} append upper case columnName</li>
     *                        <li>{@link NameMode#DEFAULT} append columnName</li>
     *                   </ul>
     * @param builder    target builder
     * @return <strong>this</strong>
     * @throws JdbdException throw when
     *                       <ul>
     *                           <li>the implementation of this method need session is open and session have closed</li>
     *                           <li>columnName contain identifier quote char and driver couldn't escape.</li>
     *                           <li>columnName is illegal</li>
     *                       </ul>
     */
    DatabaseSession appendColumnName(String columnName, NameMode mode, StringBuilder builder) throws JdbdException;

    /**
     * <p>
     * If return true , then the pool vendor developer must guarantee session and <strong>this</strong> both are created <br/>
     * by same pool {@link DatabaseSessionFactory} instance,and both underlying driver session instance are created by <br/>
     * same driver {@link DatabaseSessionFactory} instance.<br/>
     * <br/>
     * <p>
     * This method can be useful , application developer can know session and <strong>this</strong> belong to
     * same resource manager in XA transaction.
     * <br/>
     *
     * @param session non-null session
     * @return true : session and this both are created by same {@link DatabaseSessionFactory} instance.
     */
    boolean isSameFactory(DatabaseSession session);


    /**
     * <p>
     * This method should provide the access of some key(<strong>NOTE</strong> : is key,not all) properties of url ,but {@link io.jdbd.Driver#PASSWORD},
     * see {@link io.jdbd.Driver#forDeveloper(String, Map)}.<br/>
     * <br/>
     *
     * <p>
     * The implementation of this method must provide java doc(html list) for explaining supporting {@link Option} list.
     * <br/>
     * <p>
     * The implementation of this method always support :
     * <ul>
     *     <li>{@link Option#PREPARE_THRESHOLD}</li>
     * </ul>
     * <br/>
     * <p>
     * The implementation of this method perhaps support some of following :
     *     <ul>
     *         <li>{@link Option#AUTO_COMMIT}</li>
     *         <li>{@link Option#IN_TRANSACTION}</li>
     *         <li>{@link Option#READ_ONLY},true :  representing exists transaction and is read only.</li>
     *         <li>{@link Option#CLIENT_ZONE}</li>
     *         <li>{@link Option#SERVER_ZONE}</li>
     *         <li>{@link Option#CLIENT_CHARSET}</li>
     *         <li>{@link Option#BACKSLASH_ESCAPES}</li>
     *         <li>{@link Option#BINARY_HEX_ESCAPES}</li>
     *     </ul>
     * <br/>
     *
     * @param option option key
     * @param <T>    option value java type
     * @return null or the value of option.
     * @throws JdbdException throw when option need session open and session have closed.
     */
    @Nullable
    @Override
    <T> T valueOf(Option<T> option) throws JdbdException;

    /**
     * override {@link Object#toString()}
     *
     * @return driver info, contain : <ol>
     * <li>class name</li>
     * <li>session name</li>
     * <li>session identifier</li>
     * <li>factory name</li>
     * <li>{@link #factoryVendor()}</li>
     * <li>{@link #driverVendor()}</li>
     * <li>server version string</li>
     * <li>driver version string</li>
     * <li>cache current transaction  if exists</li>
     * <li>{@link System#identityHashCode(Object)}</li>
     * </ol>
     */
    @Override
    String toString();


}
