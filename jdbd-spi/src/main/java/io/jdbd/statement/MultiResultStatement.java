package io.jdbd.statement;

import io.jdbd.JdbdException;
import io.jdbd.lang.Nullable;
import io.jdbd.meta.DataType;
import io.jdbd.result.*;
import io.jdbd.session.ChunkOption;
import io.jdbd.session.Option;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import java.util.function.Function;

/**
 * <p>
 * This interface is base interface of following:
 *     <ul>
 *         <li>{@link BindStatement}</li>
 *         <li>{@link PreparedStatement}</li>
 *         <li>{@link MultiStatement}</li>
 *     </ul>
 * </p>
 *
 * @see BindStatement
 * @see PreparedStatement
 * @see MultiStatement
 */
public interface MultiResultStatement extends Statement {


    /**
     * <p>
     * <strong>NOTE</strong> : driver don't send message to database server before subscribing.
     * Driver developer must guarantee this feature.
     * </p>
     * <p>
     * For example 1 :
     * <pre>
     *         <code><br/>
     *    &#64;Test(dataProvider = "insertDatProvider")
     *    public void executeBatchUpdate(DatabaseSession session) {
     *        final String sql =  "INSERT mysql_types(my_time,my_time1,my_date,my_datetime,my_datetime6,my_text) VALUES( ? , ? , ? , ? , ? , ? )";
     *        BindSingleStatement statement;
     *
     *        // way 1:
     * //        statement =Mono.from( session.prepareStatement(sql))
     * //                .block();
     * //        Assert.assertNotNull(statement);
     * //
     * //        //way 2:
     * //        statement = session.bindStatement(sql,true);
     *
     *        // way 3:
     *        statement = session.bindStatement(sql);
     *
     *
     *        final int batchItemCount = 3;
     *
     *        for (int i = 0; i &lt; batchItemCount; i++) {
     *            statement.bind(0, JdbdType.TIME, LocalTime.now())
     *                    .bind(1, JdbdType.TIME_WITH_TIMEZONE, OffsetTime.now(ZoneOffset.UTC))
     *                    .bind(2, JdbdType.DATE, LocalDate.now())
     *                    .bind(3, JdbdType.TIMESTAMP, LocalDateTime.now())
     *
     *                    .bind(4, JdbdType.TIMESTAMP_WITH_TIMEZONE, OffsetDateTime.now(ZoneOffset.UTC))
     *                    .bind(5, JdbdType.TEXT, "QinArmy's army \\")
     *
     *                    .addBatch();
     *
     *        }
     *
     *        final List&lt;ResultStates> statesList;
     *        statesList = Flux.from(statement.executeBatchUpdate())
     *                .collectList()
     *                .block();
     *        Assert.assertNotNull(statesList);
     *
     *        Assert.assertEquals(statesList.size(), batchItemCount);
     *
     *    }
     *         </code>
     *     </pre>
     * </p>
     * <p>
     * For example 2 :
     * <pre>
     *         <code><br/>
     *    &#64;Test
     *    public void executeBatchUpdate(final DatabaseSession session) {
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
     *                .addStatement("UPDATE mysql_types AS t SET t.my_datetime = ? , t.my_decimal = t.my_decimal + ? WHERE t.my_datetime6 < ? LIMIT ?")
     *
     *                .bind(0, JdbdType.TIMESTAMP, LocalDateTime.now())
     *                .bind(1, JdbdType.DECIMAL, new BigDecimal("88.66"))
     *                .bind(2, JdbdType.TIMESTAMP_WITH_TIMEZONE, OffsetDateTime.now(ZoneOffset.UTC))
     *                .bind(3, JdbdType.INTEGER, 3);
     *
     *
     *        final List&lt;ResultStates> statesList;
     *
     *        statesList = Flux.from(statement.executeBatchUpdate())
     *                .collectList()
     *                .block();
     *
     *        Assert.assertNotNull(statesList);
     *        Assert.assertEquals(statesList.size(), 2);
     *
     *    }
     *         </code>
     *     </pre>
     * </p>
     *
     * @see BindSingleStatement#addBatch()
     * @see MultiStatement#addStatement(String)
     */
    Publisher<ResultStates> executeBatchUpdate();

    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // statement is a instance of {@link MultiResultStatement}
     *              R flux  = fluxFunc.apply(statement.executeBatchUpdate()) ;
     *
     *              // for example ,if use Project reactor , reactor.core.publisher.Flux
     *              statement.executeBatchUpdate(Flux::from)
     *                 .collectList()
     *
     *         </code>
     *     </pre>
     * </p>
     *
     * @param fluxFunc convertor function of Publisher ,for example : {@code reactor.core.publisher.Flux#from(org.reactivestreams.Publisher)}
     * @param <F>      F representing Flux that emit 0-N element or {@link Throwable}.
     * @return Flux that emit just one element or {@link Throwable}.
     * @see #executeBatchAsFlux()
     */
    <F extends Publisher<ResultStates>> F executeBatchUpdate(Function<Publisher<ResultStates>, F> fluxFunc);

    /**
     * <p>
     * <strong>NOTE</strong> : driver don't send message to database server before subscribing.
     * Driver developer must guarantee this feature.
     * </p>
     * <p>
     * For example 1 :
     * <pre>
     *         <code><br/>
     *    &#64;Test(invocationCount = 3, dataProvider = "executeQueryProvider")
     *    public void executeBatchQuery(final DatabaseSession session) {
     *
     *        final String sql = "SELECT t.* FROM mysql_types AS t WHERE t.id > ? AND t.my_text LIKE ? " +
     *                "AND t.my_time < ? AND t.my_time1 < ? AND t.my_date = ? AND t.my_datetime < ? " +
     *                "AND t.my_datetime6 < ? LIMIT ? ";
     *        BindSingleStatement statement;
     *
     *        //        way 1:
     *        //        statement =Mono.from( session.prepareStatement(sql))
     *        //                .block();
     *        //        Assert.assertNotNull(statement);
     *        //
     *        //        //way 2:
     *        //        statement = session.bindStatement(sql,true);
     *
     *        // way 3:
     *        statement = session.bindStatement(sql);
     *
     *        final int batchItemCount = 3;
     *
     *        for (int i = 0; i &lt; batchItemCount; i++) {
     *
     *            statement.bind(0, JdbdType.BIGINT, 1)
     *                    .bind(1, JdbdType.TEXT, "%army%")
     *                    .bind(2, JdbdType.TIME, LocalTime.now())
     *                    .bind(3, JdbdType.TIME_WITH_TIMEZONE, OffsetTime.now())
     *
     *                    .bind(4, JdbdType.DATE, LocalDate.now())
     *                    .bind(5, JdbdType.TIMESTAMP, LocalDateTime.now())
     *                    .bind(6, JdbdType.TIMESTAMP_WITH_TIMEZONE, OffsetDateTime.now())
     *                    .bind(7, JdbdType.INTEGER, 20)
     *
     *                    .addBatch();
     *
     *        }
     *
     *        final BatchQuery batchQuery;
     *        batchQuery = statement.executeBatchQuery();
     *
     *        final Function&lt;CurrentRow, Map&lt;String, ?>> function = this::mapCurrentRowToMap;
     *
     *        final List&lt;Map&lt;String, ?>> rowList;
     *
     *        rowList = Flux.from(batchQuery.nextQuery(function))
     *                .concatWith(batchQuery.nextQuery(function))
     *                .concatWith(batchQuery.nextQuery(function))
     *                .collectList()
     *                .block();
     *
     *        Assert.assertNotNull(rowList);
     *        Assert.assertTrue(rowList.size() > 0);
     *
     *    }
     *
     *         </code>
     *     </pre>
     * </p>
     * <p>
     * For example 2 :
     * <pre>
     *         <code><br/>
     *    &#64;Test
     *    public void executeBatchQuery(final DatabaseSession session) {
     *        final MultiStatement statement;
     *        statement = session.multiStatement();
     *
     *        statement.addStatement("SELECT t.* FROM mysql_types AS t WHERE t.my_datetime6 < ? AND t.my_decimal < ? LIMIT ? ")
     *
     *                .bind(0, JdbdType.TIMESTAMP, OffsetDateTime.now(ZoneOffset.UTC))
     *                .bind(1, JdbdType.DECIMAL, new BigDecimal("88.66"))
     *                .bind(2, JdbdType.INTEGER, 20)
     *
     *                .addStatement("SELECT t.* FROM mysql_types AS t WHERE t.my_datetime6 < ? AND t.my_decimal < ? LIMIT ? ")
     *
     *                .bind(0, JdbdType.TIMESTAMP, LocalDateTime.now().minusDays(2))
     *                .bind(1, JdbdType.DECIMAL, new BigDecimal("999999.66"))
     *                .bind(2, JdbdType.INTEGER, 10);
     *
     *        final QueryResults queryResults;
     *        queryResults = statement.executeBatchQuery();
     *
     *        final Function<CurrentRow, Map<String, ?>> function = this::mapCurrentRowToMap;
     *
     *        final List&lt;Map&lt;String, ?>> rowList;
     *
     *        rowList = Flux.from(queryResults.nextQuery(function))
     *                .concatWith(queryResults.nextQuery(function))
     *                .collectList()
     *                .block();
     *
     *        Assert.assertNotNull(rowList);
     *
     *    }
     *
     *         </code>
     *     </pre>
     * </p>
     *
     * @see BindSingleStatement#addBatch()
     * @see MultiStatement#addStatement(String)
     */
    QueryResults executeBatchQuery();

    /**
     * <p>
     * <strong>NOTE</strong> : driver don't send message to database server before subscribing.
     * Driver developer must guarantee this feature.
     * </p>
     * <p>
     * For example 1 :
     * <pre>
     *         <code><br/>
     *    &#64;Test(dataProvider = "callOutParameterProvider")
     *    public void executeBatchAsMulti(final DatabaseSession session) {
     *        // following MySQL procedure :
     *
     *        //  CREATE PROCEDURE army_inout_out_now(INOUT inoutNow DATETIME, OUT outNow DATETIME)
     *        //    LANGUAGE SQL
     *        //BEGIN
     *        //    SELECT current_timestamp(3), DATE_ADD(inoutNow, INTERVAL 1 DAY) INTO outNow,inoutNow;
     *        //END
     *        final String sql = "CALL army_inout_out_now( ? , ?)";
     *        final BindSingleStatement statement;
     *
     *        //        way 1:
     *        //        statement =Mono.from( session.prepareStatement(sql))
     *        //                .block();
     *        //        Assert.assertNotNull(statement);
     *        //
     *        //        //way 2:
     *        //        statement = session.bindStatement(sql,true);
     *
     *        // way 3:
     *        statement = session.bindStatement(sql);
     *
     *
     *        final int batchItemCount = 3;
     *        for (int i = 0; i &lt; batchItemCount; i++) {
     *            statement.bind(0, JdbdType.TIMESTAMP, LocalDateTime.now())
     *                    .bind(1, JdbdType.TIMESTAMP, OutParameter.out("outNow"))
     *
     *                    .addBatch();
     *        }
     *
     *        // each batch item , MySQL server will produce tow result,
     *        // result 1 : out parameter result set
     *        // result 2 : the result of CALL command
     *        final MultiResult multiResult;
     *        multiResult = statement.executeBatchAsMulti();
     *
     *         // here, don't need thread safe list,because main thread blocking.
     *        final List&lt;Map&lt;String, ?>> outParamMapList = new ArrayList<>(batchItemCount);
     *
     *        // here, don't need thread safe list,because main thread blocking.
     *        final List&lt;ResultStates> statesList = new ArrayList<>(batchItemCount);
     *
     *        // following first batch item results
     *        Flux.from(multiResult.nextQuery(this::mapCurrentRowToMap))
     *                .last()
     *                .doOnSuccess(outParamMapList::add) // here ,due to main thread blocking, so current thread in EventLoop.
     *                .then(Mono.from(multiResult.nextUpdate()))
     *                .doOnSuccess(statesList::add) // here ,due to main thread blocking, so  current thread in EventLoop.
     *
     *                // following second batch item results
     *                .thenMany(multiResult.nextQuery(this::mapCurrentRowToMap))
     *                .last()
     *                .doOnSuccess(outParamMapList::add) // here ,due to main thread blocking, so current thread in EventLoop.
     *                .then(Mono.from(multiResult.nextUpdate()))
     *                .doOnSuccess(statesList::add)// here ,due to main thread blocking, so  current thread in EventLoop.
     *
     *                // following third batch item results
     *                .thenMany(multiResult.nextQuery(this::mapCurrentRowToMap))
     *                .last()
     *                .doOnSuccess(outParamMapList::add) // here ,due to main thread blocking, so current thread in EventLoop.
     *                .then(Mono.from(multiResult.nextUpdate()))
     *                .doOnSuccess(statesList::add)// here ,due to main thread blocking, so  current thread in EventLoop.
     *
     *                .block();
     *
     *
     *        Assert.assertEquals(outParamMapList.size(), batchItemCount);
     *        Assert.assertEquals(statesList.size(), batchItemCount);
     *
     *        LOG.info("executeBatchAsMulti  outParamMapList : \n{}", outParamMapList);
     *
     *    }
     *
     *         </code>
     *     </pre>
     * </p>
     * <p>
     * For example 2 :
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
     *                .addStatement("UPDATE mysql_types AS t SET t.my_datetime = ? , t.my_decimal = t.my_decimal + ? WHERE t.my_datetime6 < ? LIMIT ?")
     *
     *                .bind(0, JdbdType.TIMESTAMP, LocalDateTime.now())
     *                .bind(1, JdbdType.DECIMAL, new BigDecimal("88.66"))
     *                .bind(2, JdbdType.TIMESTAMP_WITH_TIMEZONE, OffsetDateTime.now(ZoneOffset.UTC))
     *                .bind(3, JdbdType.INTEGER, 3)
     *
     *                .addStatement("SELECT t.* FROM mysql_types AS t WHERE t.my_datetime6 < ? AND t.my_decimal < ? LIMIT ? ")
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
     * </p>
     *
     * @see BindSingleStatement#addBatch()
     * @see MultiStatement#addStatement(String)
     */
    MultiResult executeBatchAsMulti();

    /**
     * <p>
     * <strong>NOTE</strong> : driver don't send message to database server before subscribing.
     * Driver developer must guarantee this feature.
     * </p>
     * <p>
     * <pre>
     * For example 1 :
     *         <code><br/>
     *    &#64;Test(dataProvider = "callOutParameterProvider")
     *    public void executeBatchAsFlux(final DatabaseSession session){
     *        //  CREATE PROCEDURE army_inout_out_now(INOUT inoutNow DATETIME, OUT outNow DATETIME)
     *        //    LANGUAGE SQL
     *        //BEGIN
     *        //    SELECT current_timestamp(3), DATE_ADD(inoutNow, INTERVAL 1 DAY) INTO outNow,inoutNow;
     *        //END
     *        final String sql = "CALL army_inout_out_now( ? , ?)";
     *        final BindSingleStatement statement;
     *
     *        //        way 1:
     *        //        statement =Mono.from( session.prepareStatement(sql))
     *        //                .block();
     *        //        Assert.assertNotNull(statement);
     *        //
     *        //        //way 2:
     *        //        statement = session.bindStatement(sql,true);
     *
     *        // way 3:
     *        statement = session.bindStatement(sql);
     *        final int batchItemCount = 3;
     *
     *        for (int i = 0; i &lt; batchItemCount; i++) {
     *            statement.bind(0, JdbdType.TIMESTAMP, LocalDateTime.now())
     *                    .bind(1, JdbdType.TIMESTAMP, OutParameter.out("outNow"))
     *
     *                    .addBatch();
     *        }
     *
     *
     *        final List&lt;? extends Map&lt;String, ?>> rowList;
     *
     *        // each batch item , MySQL server will produce tow result,
     *        // result 1 : out parameter result set , just one row.
     *        // result 2 : the result of CALL command
     *        rowList = Flux.from(statement.executeBatchAsFlux())
     *                .filter(ResultItem::isRowItem)
     *                .map(ResultRow.class::cast)
     *                .map(this::mapCurrentRowToMap)
     *                .collectList()
     *                .block();
     *
     *        Assert.assertNotNull(rowList);
     *        Assert.assertEquals(rowList.size(), batchItemCount);
     *
     *        LOG.info("executeBatchAsFlux out parameter : \n{}", rowList);
     *
     *    }
     *         </code>
     *     </pre>
     * </p>
     * <p>
     * For example 2 :
     * <pre>
     *         <code><br/>
     *    &#64;Test
     *    public void executeBatchAsFlux(final DatabaseSession session){
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
     *                .addStatement("UPDATE mysql_types AS t SET t.my_datetime = ? , t.my_decimal = t.my_decimal + ? WHERE t.my_datetime6 < ? LIMIT ?")
     *
     *                .bind(0, JdbdType.TIMESTAMP, LocalDateTime.now())
     *                .bind(1, JdbdType.DECIMAL, new BigDecimal("88.66"))
     *                .bind(2, JdbdType.TIMESTAMP_WITH_TIMEZONE, OffsetDateTime.now(ZoneOffset.UTC))
     *                .bind(3, JdbdType.INTEGER, 3)
     *
     *                .addStatement("SELECT t.* FROM mysql_types AS t WHERE t.my_datetime6 < ? AND t.my_decimal < ? LIMIT ? ")
     *
     *                .bind(0, JdbdType.TIMESTAMP, OffsetDateTime.now(ZoneOffset.UTC))
     *                .bind(1, JdbdType.DECIMAL, new BigDecimal("88.66"))
     *                .bind(2, JdbdType.INTEGER, 20);
     *
     *
     *         final AtomicReference&lt;ResultStates> insertStatesHolder = new AtomicReference<>(null);
     *
     *        final AtomicReference&lt;ResultStates> updateStatesHolder = new AtomicReference<>(null);
     *
     *        final List&lt;? extends Map&lt;String, ?>> rowList;
     *
     *        rowList =    Flux.from(statement.executeBatchAsFlux())
     *                 .filter(ResultItem::isRowOrStatesItem)
     *                 .doOnNext(item->{
     *                     switch (item.getResultNo()){
     *                         case 1:
     *                             insertStatesHolder.set((ResultStates) item);
     *                             break;
     *                         case 2:
     *                             updateStatesHolder.set((ResultStates) item);
     *                             break;
     *                         default:
     *                             //no-op
     *                     }
     *                 })
     *                 .filter(ResultItem::isRowItem)
     *                 .map(ResultRow.class::cast)
     *                 .map(this::mapCurrentRowToMap)
     *                 .collectList()
     *                 .block();
     *
     *        Assert.assertNotNull(insertStatesHolder.get());
     *        Assert.assertNotNull(updateStatesHolder.get());
     *        Assert.assertNotNull(rowList);
     *
     *
     *    }
     *         </code>
     *     </pre>
     * </p>
     *
     * @see BindSingleStatement#addBatch()
     * @see MultiStatement#addStatement(String)
     */
    OrderedFlux executeBatchAsFlux();

    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // statement is a instance of {@link BindSingleStatement}
     *              R flux  = fluxFunc.apply(statement.executeBatchAsFlux()) ;
     *
     *              // for example ,if use Project reactor , reactor.core.publisher.Flux
     *              statement.executeBatchAsFlux(Flux::from)
     *                 .collectList()
     *
     *         </code>
     *     </pre>
     * </p>
     *
     * @param fluxFunc convertor function of Publisher ,for example : {@code reactor.core.publisher.Flux#from(org.reactivestreams.Publisher)}
     * @param <F>      F representing Flux that emit 0-N element or {@link Throwable}.
     * @return Flux that emit just one element or {@link Throwable}.
     * @see #executeBatchAsFlux()
     */
    <F extends Publisher<ResultItem>> F executeBatchAsFlux(Function<OrderedFlux, F> fluxFunc);

    /**
     * {@inheritDoc }
     */
    @Override
    MultiResultStatement bindStmtVar(String name, DataType dataType, @Nullable Object value) throws JdbdException;


    /**
     * {@inheritDoc }
     */
    @Override
    MultiResultStatement setTimeout(int seconds) throws JdbdException;

    /**
     * {@inheritDoc }
     */
    @Override
    MultiResultStatement setFetchSize(int fetchSize) throws JdbdException;

    /**
     * {@inheritDoc }
     */
    @Override
    MultiResultStatement setImportPublisher(Function<ChunkOption, Publisher<byte[]>> function) throws JdbdException;

    /**
     * {@inheritDoc }
     */
    @Override
    MultiResultStatement setExportSubscriber(Function<ChunkOption, Subscriber<byte[]>> function) throws JdbdException;

    /**
     * {@inheritDoc }
     */
    <T> MultiResultStatement setOption(Option<T> option, @Nullable T value) throws JdbdException;


}
