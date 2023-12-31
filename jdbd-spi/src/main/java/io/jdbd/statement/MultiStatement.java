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

package io.jdbd.statement;

import io.jdbd.JdbdException;
import io.jdbd.lang.Nullable;
import io.jdbd.meta.DataType;
import io.jdbd.session.ChunkOption;
import io.jdbd.session.DatabaseSession;
import io.jdbd.session.Option;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import java.util.function.Function;

/**
 * <p>
 * This interface representing the multi sql statement (separated by semicolons {@code ;})
 * that support sql parameter placeholder({@code ?}) with client-prepare.
 *<br/>
 * <p>
 * If you invoke only once {@link #addStatement(String)} ,then this interface is similar to {@link BindStatement},
 * except that don't support server-prepare.
 *<br/>
 * <p>
 * This interface is similar to {@link StaticStatementSpec#executeMultiStmt(String)} method,
 * except that support sql parameter placeholder({@code ?}) with client-prepare.
 *<br/>
 * <p>
 * The instance of this interface is created by {@link DatabaseSession#multiStatement()} method.
 *<br/>
 * <p>
 * For example 1 :
 * <pre>
 *         <code><br/>
 *    &#64;Test
 *    public void executeBatchAsMulti(final DatabaseSession session) {
 *        final MultiStatement statement;
 *        statement = session.multiStatement();
 *
 *        statement.addStatement("INSERT mysql_types(my_time,my_time1,my_date,my_datetime,my_datetime6,my_text) VALUES( ? , ? , ? , ? , ? , ? )")
 *
 *                .bind(0, JdbdType.TIME, LocalTime.now())
 *                .bind(1, JdbdType.TIME_WITH_TIMEZONE, OffsetTime.now(ZoneOffset.UTC))
 *                .bind(2, JdbdType.DATE, LocalDate.now())
 *                .bind(3, JdbdType.TIMESTAMP, LocalDateTime.now())
 *
 *                .bind(4, JdbdType.TIMESTAMP_WITH_TIMEZONE, OffsetDateTime.now(ZoneOffset.UTC))
 *                .bind(5, JdbdType.TEXT, "QinArmy's army \\")
 *
 *                .addStatement("UPDATE mysql_types AS t SET t.my_datetime = ? , t.my_decimal = t.my_decimal + ? WHERE t.my_datetime6 &lt; ? LIMIT ?")
 *
 *                .bind(0, JdbdType.TIMESTAMP, LocalDateTime.now())
 *                .bind(1, JdbdType.DECIMAL, new BigDecimal("88.66"))
 *                .bind(2, JdbdType.TIMESTAMP_WITH_TIMEZONE, OffsetDateTime.now(ZoneOffset.UTC))
 *                .bind(3, JdbdType.INTEGER, 3)
 *
 *                .addStatement("SELECT t.* FROM mysql_types AS t WHERE t.my_datetime6 &lt; ? AND t.my_decimal &lt; ? LIMIT ? ")
 *
 *                .bind(0, JdbdType.TIMESTAMP, OffsetDateTime.now(ZoneOffset.UTC))
 *                .bind(1, JdbdType.DECIMAL, new BigDecimal("88.66"))
 *                .bind(2, JdbdType.INTEGER, 20);
 *
 *
 *        final MultiResult multiResult;
 *        multiResult = statement.executeBatchAsMulti();
 *
 *
 *        Mono.from(multiResult.nextUpdate())
 *                .then(Mono.from(multiResult.nextUpdate()))
 *                .thenMany(multiResult.nextQuery(this::mapCurrentRowToMap))
 *                .collectList()
 *                .block();
 *
 *    }
 *         </code>
 *     </pre>
 *<br/>
 * @since 1.0
 */
public interface MultiStatement extends MultiResultStatement, ParametrizedStatement {

    /**
     * @param sql must have text.
     * @return <strong>this</strong>
     * @throws IllegalArgumentException when sql has no text.
     * @throws JdbdException            when reuse this instance after invoke below method:
     *                                  <ul>
     *                                      <li>{@link #executeBatchUpdate()}</li>
     *                                      <li>{@link #executeBatchQuery()}</li>
     *                                      <li>{@link #executeBatchAsMulti()}</li>
     *                                      <li>{@link #executeBatchAsFlux()}</li>
     *                                  </ul>
     */
    MultiStatement addStatement(String sql) throws JdbdException;

    /**
     * {@inheritDoc }
     */
    @Override
    MultiStatement bind(int indexBasedZero, DataType dataType, @Nullable Object value) throws JdbdException;

    /**
     * {@inheritDoc }
     */
    @Override
    MultiStatement bindStmtVar(String name, DataType dataType, @Nullable Object value) throws JdbdException;


    /**
     * {@inheritDoc }
     */
    @Override
    MultiStatement setTimeout(int millSeconds) throws IllegalArgumentException;

    /**
     * {@inheritDoc }
     */
    @Override
    MultiStatement setFetchSize(int fetchSize) throws IllegalArgumentException;

    /**
     * {@inheritDoc }
     */
    @Override
    MultiStatement setImportPublisher(Function<ChunkOption, Publisher<byte[]>> function) throws JdbdException;

    /**
     * {@inheritDoc }
     */
    @Override
    MultiStatement setExportSubscriber(Function<ChunkOption, Subscriber<byte[]>> function) throws JdbdException;


    /**
     * {@inheritDoc }
     */
    <T> MultiStatement setOption(Option<T> option, @Nullable T value) throws JdbdException;


}
