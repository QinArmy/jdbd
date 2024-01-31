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
import io.jdbd.util.NameMode;
import org.reactivestreams.Publisher;

import java.util.Optional;
import java.util.function.Function;

/**
 * <p>
 * This interface representing database session that support local transaction.
 * <br/>
 * <p>
 * This interface is is similar to {@code java.sql.Connection} , except that this interface is reactive.
 * <br/>
 * <p>
 * This interface is base interface of {@link io.jdbd.pool.PoolLocalDatabaseSession}.
 * <br/>
 * <p>
 * The instance of this interface is created by {@link DatabaseSessionFactory#localSession()} method.
 * <br/>
 * <p>
 * Application developer can create statement by following methods :
 *     <ul>
 *         <li>{@link #statement()} ,create static statement.</li>
 *         <li>{@link #prepareStatement(String)} , create server-prepare statement</li>
 *         <li>{@link #bindStatement(String)} , create the adaptor of client-prepared statement and server-prepared statement.</li>
 *         <li>{@link #bindStatement(String, boolean)}, create the adaptor of client-prepared statement and server-prepared statement.</li>
 *         <li>{@link #multiStatement()}, create multi-statement</li>
 *     </ul>
 * <br/>
 * <p>
 * Application developer can control local transaction by following :
 *     <ul>
 *         <li>{@link #startTransaction()}</li>
 *         <li>{@link #startTransaction(TransactionOption)}</li>
 *         <li>{@link #startTransaction(TransactionOption, HandleMode)}</li>
 *         <li>{@link #setTransactionCharacteristics(TransactionOption)}</li>
 *         <li>{@link #transactionInfo()}</li>
 *         <li>{@link #inTransaction()}</li>
 *         <li>{@link #commit()}</li>
 *         <li>{@link #commit(Function)}</li>
 *         <li>{@link #rollback()}</li>
 *         <li>{@link #rollback(Function)}</li>
 *         <li>{@link #setSavePoint()}</li>
 *         <li>{@link #releaseSavePoint(SavePoint)}</li>
 *         <li>{@link #releaseSavePoint(SavePoint, Function)}</li>
 *         <li>{@link #rollbackToSavePoint(SavePoint)}</li>
 *         <li>{@link #rollbackToSavePoint(SavePoint, Function)}</li>
 *     </ul>
 * <br/>
 *
 * @since 1.0
 */
public interface LocalDatabaseSession extends DatabaseSession {


    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // session is instance of {@link LocalDatabaseSession}
     *             session.startTransaction(TransactionOption.option(null,false),HandleMode.ERROR_IF_EXISTS) ;
     *         </code>
     *     </pre>
     * <br/>
     *
     * @see #startTransaction(TransactionOption, HandleMode)
     */
    Publisher<TransactionInfo> startTransaction();


    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // session is instance of {@link LocalDatabaseSession}
     *             session.startTransaction(option,HandleMode.ERROR_IF_EXISTS) ;
     *         </code>
     *     </pre>
     * <br/>
     *
     * @see #startTransaction(TransactionOption, HandleMode)
     */
    Publisher<TransactionInfo> startTransaction(TransactionOption option);

    /**
     * <p>Start one local transaction with option.
     * <p>Driver developer should guarantee that transaction option (eg: {@link Isolation}) applies only this new transaction.
     * <p>The implementation of this method <strong>always</strong> support {@link Option#SQL_LOGGER}
     * <p>The implementation of this method <strong>perhaps</strong> support some of following :
     *     <ul>
     *         <li>{@link Option#WITH_CONSISTENT_SNAPSHOT}</li>
     *         <li>{@link Option#DEFERRABLE}</li>
     *         <li>{@link Option#NAME}</li>
     *     </ul>
     * <p><strong>NOTE</strong>:
     * <ul>
     *     <li>{@link TransactionInfo#valueOf(Option)} with {@link Option#START_MILLIS} always non-null.</li>
     *     <li>{@link TransactionInfo#valueOf(Option)} with {@link Option#DEFAULT_ISOLATION} always non-null.</li>
     *     <li>{@link TransactionInfo#valueOf(Option)} with {@link Option#TIMEOUT_MILLIS} always same with option</li>
     *     <li>{@link TransactionInfo#valueOf(Option)} with {@link Option#NAME} always same with option</li>
     *     <li>{@link TransactionInfo#valueOf(Option)} with {@link Option#LABEL} always same with option</li>
     * </ul>
     *
     * @param option non-null transaction option, driver perhaps support dialect transaction option by {@link TransactionOption#valueOf(Option)}.
     * @param mode   the handle mode when have existed local transaction :
     *               <ul>
     *                  <li>{@link HandleMode#ERROR_IF_EXISTS} see {@link #inTransaction()} : emit(not throw) {@link JdbdException}</li>
     *                  <li>{@link HandleMode#COMMIT_IF_EXISTS} : commit existed transaction before new transaction.</li>
     *                  <li>{@link HandleMode#ROLLBACK_IF_EXISTS} : rollback existed transaction before new transaction.</li>
     *               </ul>
     * @return emit one {@link TransactionInfo} or {@link Throwable}. Like {@code reactor.core.publisher.Mono} .
     * @throws JdbdException emit(not throw) when
     *                       <ul>
     *                           <li>appropriate {@link Isolation} isn't supported</li>
     *                           <li>have existed transaction and mode is {@link HandleMode#ERROR_IF_EXISTS},see {@link #inTransaction()}</li>
     *                           <li>session have closed, see {@link SessionCloseException}</li>
     *                           <li>network error</li>
     *                           <li>server response error message, see {@link io.jdbd.result.ServerException}</li>
     *                       </ul>
     * @see #setTransactionCharacteristics(TransactionOption)
     */
    Publisher<TransactionInfo> startTransaction(TransactionOption option, HandleMode mode);


    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // session is instance of {@link LocalDatabaseSession}
     *             session.commit(Collections.emptyMap()) ;
     *         </code>
     *     </pre>
     * <br/>
     *
     * @return {@link Publisher} emit <strong>this</strong> or {@link Throwable},like {@code reactor.core.publisher.Mono}
     * @see #commit(Function)
     */
    Publisher<LocalDatabaseSession> commit();

    /**
     * <p>COMMIT current local transaction of this session.
     * <p>The implementation of this method <strong>always</strong> support {@link Option#SQL_LOGGER} .
     * <p>The implementation of this method <strong>perhaps</strong> support some of following :
     *     <ul>
     *         <li>{@link Option#CHAIN}</li>
     *         <li>{@link Option#RELEASE}</li>
     *     </ul>
     * <br/>
     * <p><strong>NOTE</strong> :
     * <ul>
     *     <li>driver don't send message to database server before subscribing.</li>
     *     <li>this method don't check session whether in transaction block or not.</li>
     *     <li>when {@link Option#CHAIN} is true and driver support,{@link TransactionInfo#valueOf(Option)} with {@link Option#START_MILLIS} always be updated</li>
     *     <li>{@link TransactionInfo#valueOf(Option)} with {@link Option#TIMEOUT_MILLIS} always same with option</li>
     * </ul>
     *
     * <p>If return non-null {@link TransactionInfo} <strong>NOTE</strong>:
     * <ul>
     *     <li>{@link TransactionInfo#valueOf(Option)} with {@link Option#START_MILLIS} always is new value.</li>
     *     <li>{@link TransactionInfo#valueOf(Option)} with {@link Option#DEFAULT_ISOLATION} always same with {@link #startTransaction(TransactionOption, HandleMode)}.</li>
     *     <li>{@link TransactionInfo#valueOf(Option)} with {@link Option#TIMEOUT_MILLIS} always same with {@link #startTransaction(TransactionOption, HandleMode)}</li>
     *     <li>{@link TransactionInfo#valueOf(Option)} with {@link Option#NAME} always same with {@link #startTransaction(TransactionOption, HandleMode)}</li>
     *     <li>{@link TransactionInfo#valueOf(Option)} with {@link Option#LABEL} always same with {@link #startTransaction(TransactionOption, HandleMode)}</li>
     * </ul>
     *
     * @param optionFunc dialect option,always support {@link Option#SQL_LOGGER}. see {@link Option#EMPTY_OPTION_FUNC}
     * @return emit 0-1 {@link TransactionInfo} or {@link Throwable}. Like {@code reactor.core.publisher.Mono}.
     * If emit {@link TransactionInfo}, then driver support {@link Option#CHAIN} and you specified {@link Option#CHAIN}.
     * @throws JdbdException emit(not throw) when
     *                       <ul>
     *                          <li>driver don't support appropriate {@link Option}</li>
     *                          <li>network error</li>
     *                          <li>session have closed,see {@link SessionCloseException}</li>
     *                          <li>serer response error message, see {@link io.jdbd.result.ServerException}</li>
     *                       </ul>
     */
    Publisher<Optional<TransactionInfo>> commit(Function<Option<?>, ?> optionFunc);

    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // session is instance of {@link LocalDatabaseSession}
     *             session.rollback(Collections.emptyMap()) ;
     *         </code>
     *     </pre>
     * <br/>
     *
     * @return {@link Publisher} emit <strong>this</strong> or {@link Throwable},like {@code reactor.core.publisher.Mono}
     * @see #rollback(Function)
     */
    Publisher<LocalDatabaseSession> rollback();

    /**
     * <p>ROLLBACK current local transaction of this session.
     * <p>The implementation of this method <strong>always</strong> support {@link Option#SQL_LOGGER} .
     * <p>The implementation of this method <strong>perhaps</strong> support some of following :
     *     <ul>
     *         <li>{@link Option#CHAIN}</li>
     *         <li>{@link Option#RELEASE}</li>
     *     </ul>
     * <p><strong>NOTE</strong> :
     * <ul>
     *     <li>driver don't send message to database server before subscribing.</li>
     *     <li>this method don't check session whether in transaction block or not.</li>
     *     <li>when {@link Option#CHAIN} is true and driver support,{@link TransactionInfo#valueOf(Option)} with {@link Option#START_MILLIS} always be updated</li>
     *     <li>{@link TransactionInfo#valueOf(Option)} with {@link Option#TIMEOUT_MILLIS} always same with option</li>
     * </ul>
     * <p>If return non-null {@link TransactionInfo} <strong>NOTE</strong>:
     * <ul>
     *     <li>{@link TransactionInfo#valueOf(Option)} with {@link Option#START_MILLIS} always is new value.</li>
     *     <li>{@link TransactionInfo#valueOf(Option)} with {@link Option#DEFAULT_ISOLATION} always same with {@link #startTransaction(TransactionOption, HandleMode)}.</li>
     *     <li>{@link TransactionInfo#valueOf(Option)} with {@link Option#TIMEOUT_MILLIS} always same with {@link #startTransaction(TransactionOption, HandleMode)}</li>
     *     <li>{@link TransactionInfo#valueOf(Option)} with {@link Option#NAME} always same with {@link #startTransaction(TransactionOption, HandleMode)}</li>
     *     <li>{@link TransactionInfo#valueOf(Option)} with {@link Option#LABEL} always same with {@link #startTransaction(TransactionOption, HandleMode)}</li>
     * </ul>
     *
     * @param optionFunc dialect option,always support {@link Option#SQL_LOGGER}. see {@link Option#EMPTY_OPTION_FUNC}
     * @return emit 0-1 {@link TransactionInfo} or {@link Throwable}. Like {@code reactor.core.publisher.Mono}.
     * If emit {@link TransactionInfo}, then driver support {@link Option#CHAIN} and you specified {@link Option#CHAIN}.
     * @throws JdbdException emit(not throw) when
     *                       <ul>
     *                          <li>driver don't support appropriate {@link Option}</li>
     *                          <li>network error</li>
     *                          <li>session have closed,see {@link SessionCloseException}</li>
     *                          <li>serer response error message, see {@link io.jdbd.result.ServerException}</li>
     *                       </ul>
     */
    Publisher<Optional<TransactionInfo>> rollback(Function<Option<?>, ?> optionFunc);


    /**
     * {@inheritDoc}
     */
    @Override
    Publisher<LocalDatabaseSession> setTransactionCharacteristics(TransactionOption option);


    /**
     * {@inheritDoc}
     */
    @Override
    Publisher<LocalDatabaseSession> releaseSavePoint(SavePoint savepoint);

    /**
     * {@inheritDoc}
     */
    @Override
    Publisher<LocalDatabaseSession> releaseSavePoint(SavePoint savepoint, Function<Option<?>, ?> optionFunc);


    /**
     * {@inheritDoc}
     */
    @Override
    Publisher<LocalDatabaseSession> rollbackToSavePoint(SavePoint savepoint);

    /**
     * {@inheritDoc}
     */
    @Override
    Publisher<LocalDatabaseSession> rollbackToSavePoint(SavePoint savepoint, Function<Option<?>, ?> optionFunc);

    /**
     * {@inheritDoc}
     */
    @Override
    LocalDatabaseSession appendLiteral(@Nullable String text, StringBuilder builder) throws JdbdException;

    /**
     * {@inheritDoc}
     */
    @Override
    LocalDatabaseSession appendIdentifier(String identifier, StringBuilder builder) throws JdbdException;

    /**
     * {@inheritDoc}
     */
    @Override
    LocalDatabaseSession appendTableName(String tableName, NameMode mode, StringBuilder builder) throws JdbdException;

    /**
     * {@inheritDoc}
     */
    @Override
    LocalDatabaseSession appendColumnName(String columnName, NameMode mode, StringBuilder builder) throws JdbdException;


}
