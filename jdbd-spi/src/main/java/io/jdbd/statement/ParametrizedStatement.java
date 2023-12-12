package io.jdbd.statement;

import io.jdbd.JdbdException;
import io.jdbd.lang.Nullable;
import io.jdbd.meta.DataType;
import io.jdbd.meta.JdbdType;
import io.jdbd.session.ChunkOption;
import io.jdbd.session.Option;
import io.jdbd.type.*;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import java.time.LocalDate;
import java.util.function.Function;

/**
 * <p>
 * This interface representing parametrized statement that SQL parameter placeholder must be {@code ?} .
 * <br/>
 * <p>
 * This interface is base interface of following :
 *     <ul>
 *         <li>{@link BindStatement}</li>
 *         <li>{@link PreparedStatement}</li>
 *         <li>{@link MultiStatement}</li>
 *     </ul>
 * <br/>
 *
 * @since 1.0
 */
public interface ParametrizedStatement extends Statement {


    /**
     * <p>
     * Bind parameter value to statement that exists SQL parameter placeholder and SQL parameter placeholder must be {@code ?}
     * <br/>
     * <p>
     * <strong>NOTE</strong> : the driver developer must provide the java doc(html list) in the implementation of this method for explaining :
     * <ul>
     *     <li>the rule of {@link DataType} converting </li>
     *     <li>the rule of {@link DataType} supporting java type</li>
     * </ul>
     * <br/>
     * <p>
     *     Each {@link JdbdType} instance support java type rule:
     *     <ul>
     *         <li>{@link JdbdType#NULL} support only {@code null}</li>
     *         <li>{@link JdbdType#BOOLEAN} support following java types :
     *             <ol>
     *                 <li>{@code null}</li>
     *                 <li>{@link Boolean}</li>
     *                 <li>{@link Number} , zero value : convert to {@link Boolean#FALSE} ; non-zero value : convert to {@link Boolean#TRUE}</li>
     *                 <li>{@link String},ignore case 'TRUE','T','ON','YES' :  convert to {@link Boolean#TRUE} ; ignore case 'FALSE','F','OFF','NO' :  convert to {@link Boolean#FALSE}; other : the executeXxx() method emit(not throw) {@link JdbdException}</li>
     *                 <li>{@link OutParameter}</li>
     *             </ol>
     *              if overflow ,the executeXxx() method emit(not throw) {@link JdbdException}
     *         </li>
     *         <li>{@link JdbdType#BIT} and {@link JdbdType#VARBIT}  support following java types :
     *            <ol>
     *                <li>{@code null}</li>
     *                <li>{@link Byte}</li>
     *                <li>{@link Short}</li>
     *                <li>{@link Integer}</li>
     *                <li>{@link Long}</li>
     *                <li>{@link java.util.BitSet} , convert to binary string</li>
     *                <li>{@link String} the string only '0' and '1'</li>
     *                <li>{@link OutParameter}</li>
     *            </ol>
     *             if overflow ,the executeXxx() method emit(not throw) {@link JdbdException}
     *         </li>
     *         <li> integer number types :
     *              <ul>
     *                  <li>{@link JdbdType#TINYINT}</li>
     *                  <li>{@link JdbdType#SMALLINT}</li>
     *                  <li>{@link JdbdType#MEDIUMINT}</li>
     *                  <li>{@link JdbdType#INTEGER}</li>
     *                  <li>{@link JdbdType#BIGINT}</li>
     *                  <li>{@link JdbdType#TINYINT_UNSIGNED}</li>
     *                  <li>{@link JdbdType#SMALLINT_UNSIGNED}</li>
     *                  <li>{@link JdbdType#MEDIUMINT_UNSIGNED}</li>
     *                  <li>{@link JdbdType#INTEGER_UNSIGNED}</li>
     *                  <li>{@link JdbdType#BIGINT_UNSIGNED}</li>
     *              </ul>
     *                support following java types :
     *                <ol>
     *                    <li>{@code null}</li>
     *                    <li>{@link Byte}</li>
     *                    <li>{@link Short}</li>
     *                    <li>{@link Integer}</li>
     *                    <li>{@link Long}</li>
     *                    <li>{@link java.math.BigInteger}</li>
     *                    <li>{@link java.math.BigDecimal} , fractional part must be 0 </li>
     *                    <li>{@link String}</li>
     *                    <li>{@link Boolean} , true : 1 ; false : 0</li>
     *                    <li>{@link OutParameter}</li>
     *                </ol>
     *                if overflow ,the executeXxx() method emit(not throw) {@link JdbdException}
     *         </li>
     *         <li>{@link JdbdType#DECIMAL} and {@link JdbdType#DECIMAL_UNSIGNED} and {@link JdbdType#NUMERIC} support following java types :
     *              <ol>
     *                    <li>{@code null}</li>
     *                    <li>{@link Byte}</li>
     *                    <li>{@link Short}</li>
     *                    <li>{@link Integer}</li>
     *                    <li>{@link Long}</li>
     *                    <li>{@link java.math.BigInteger}</li>
     *                    <li>{@link java.math.BigDecimal} </li>
     *                    <li>{@link Float}, use {@link java.math.BigDecimal#BigDecimal(String)} constructor</li>
     *                    <li>{@link Double}, use {@link java.math.BigDecimal#BigDecimal(String)} constructor</li>
     *                    <li>{@link String}</li>
     *                    <li>{@link Boolean} , true : {@link java.math.BigDecimal#ONE} ; false : {@link java.math.BigDecimal#ZERO}</li>
     *                    <li>{@link OutParameter}</li>
     *              </ol>
     *               if overflow ,the executeXxx() method emit(not throw) {@link JdbdException}
     *         </li>
     *         <li>{@link JdbdType#FLOAT} and {@link JdbdType#DOUBLE} and {@link JdbdType#FLOAT}  support following java types :
     *              <ol>
     *                  <li>{@code null}</li>
     *                  <li>{@link Float}</li>
     *                  <li>{@link Double}</li>
     *                  <li>{@link String}</li>
     *                  <li>{@link Boolean} , true : 1.0 ; false : 0.0</li>
     *                  <li>{@link OutParameter}</li>
     *              </ol>
     *               if overflow ,the executeXxx() method emit(not throw) {@link JdbdException}
     *         </li>
     *         <li>text string types
     *              <ul>
     *                  <li>{@link JdbdType#CHAR}</li>
     *                  <li>{@link JdbdType#VARCHAR}</li>
     *                  <li>{@link JdbdType#TINYTEXT}</li>
     *                  <li>{@link JdbdType#TEXT}</li>
     *                  <li>{@link JdbdType#MEDIUMTEXT}</li>
     *                  <li>{@link JdbdType#LONGTEXT}</li>
     *                  <li>{@link JdbdType#XML}</li>
     *                  <li>{@link JdbdType#JSON}</li>
     *                  <li>{@link JdbdType#JSONB}</li>
     *              </ul>
     *                support following java types :
     *                <ol>
     *                    <li>{@code null}</li>
     *                    <li>{@link String}</li>
     *                    <li>{@link Clob}</li>
     *                    <li>{@link TextPath}</li>
     *                    <li>{@link Byte}</li>
     *                    <li>{@link Short}</li>
     *                    <li>{@link Integer}</li>
     *                    <li>{@link Long}</li>
     *                    <li>{@link java.math.BigInteger}</li>
     *                    <li>{@link java.math.BigDecimal} </li>
     *                    <li>{@link Float}</li>
     *                    <li>{@link Double}</li>
     *                    <li>{@link Boolean}</li>
     *                    <li>{@link Enum#name()}</li>
     *                    <li>{@link LocalDate}</li>
     *                    <li>{@link java.time.LocalTime}</li>
     *                    <li>{@link java.time.LocalDateTime}</li>
     *                    <li>{@link java.time.OffsetTime}</li>
     *                    <li>{@link java.time.OffsetDateTime}</li>
     *                    <li>{@link java.time.ZonedDateTime}</li>
     *                    <li>{@link java.time.YearMonth}</li>
     *                    <li>{@link java.time.MonthDay}</li>
     *                    <li>{@link java.util.BitSet}, convert to binary string</li>
     *                    <li>{@link OutParameter}</li>
     *                </ol>
     *                if overflow ,the executeXxx() method emit(not throw) {@link JdbdException}
     *         </li>
     *         <li>binary types
     *              <ul>
     *                  <li>{@link JdbdType#BINARY}</li>
     *                  <li>{@link JdbdType#VARBINARY}</li>
     *                  <li>{@link JdbdType#TINYBLOB}</li>
     *                  <li>{@link JdbdType#BLOB}</li>
     *                  <li>{@link JdbdType#MEDIUMBLOB}</li>
     *                  <li>{@link JdbdType#LONGBLOB}</li>
     *              </ul>
     *               support following java types :
     *               <ol>
     *                  <li>{@code null}</li>
     *                  <li>{@code byte[]}</li>
     *                  <li>{@link Blob}</li>
     *                  <li>{@link BlobPath}</li>
     *                  <li>{@link OutParameter}</li>
     *               </ol>
     *               if overflow ,the executeXxx() method emit(not throw) {@link JdbdException}
     *         </li>
     *         <li>{@link JdbdType#TIME}  support following java types :
     *               <ol>
     *                  <li>{@code null}</li>
     *                  <li>{@link java.time.LocalTime}</li>
     *                  <li>{@link String}</li>
     *                  <li>{@link java.time.OffsetTime},if database don't support time with timezone,then convert, like MySQL,else (eg: PostgreSQL) overflow</li>
     *                  <li>{@link OutParameter}</li>
     *               </ol>
     *               if overflow ,the executeXxx() method emit(not throw) {@link JdbdException}
     *         </li>
     *         <li>{@link JdbdType#TIME_WITH_TIMEZONE}  support following java types :
     *               <ol>
     *                  <li>{@code null}</li>
     *                  <li>{@link java.time.OffsetTime}</li>
     *                  <li>{@link String}</li>
     *                  <li>{@link OutParameter}</li>
     *               </ol>
     *               if overflow ,the executeXxx() method emit(not throw) {@link JdbdException}
     *         </li>
     *         <li>{@link JdbdType#DATE} support following java types :
     *               <ol>
     *                  <li>{@code null}</li>
     *                  <li>{@link java.time.LocalDate}</li>
     *                  <li>{@link java.time.YearMonth}</li>
     *                  <li>{@link java.time.MonthDay}</li>
     *                  <li>{@link String}</li>
     *                  <li>{@link OutParameter}</li>
     *               </ol>
     *               if overflow ,the executeXxx() method emit(not throw) {@link JdbdException}
     *         </li>
     *         <li>{@link JdbdType#TIMESTAMP}  support following java types :
     *               <ol>
     *                  <li>{@code null}</li>
     *                  <li>{@link java.time.LocalDateTime}</li>
     *                  <li>{@link String}</li>
     *                  <li>{@link java.time.OffsetTime},if database don't support timestamp with timezone,then convert, like MySQL,else (eg: PostgreSQL) overflow</li>
     *                  <li>{@link java.time.ZonedDateTime},if database don't support timestamp with timezone,then convert, like MySQL,else (eg: PostgreSQL) overflow</li>
     *                  <li>{@link OutParameter}</li>
     *               </ol>
     *               if overflow ,the executeXxx() method emit(not throw) {@link JdbdException}
     *         </li>
     *         <li>{@link JdbdType#TIMESTAMP_WITH_TIMEZONE}  support following java types :
     *               <ol>
     *                  <li>{@code null}</li>
     *                  <li>{@link String}</li>
     *                  <li>{@link java.time.OffsetTime}</li>
     *                  <li>{@link java.time.ZonedDateTime}</li>
     *                  <li>{@link OutParameter}</li>
     *               </ol>
     *               if overflow ,the executeXxx() method emit(not throw) {@link JdbdException}
     *         </li>
     *         <li>{@link JdbdType#YEAR_MONTH} support following java types :
     *               <ol>
     *                  <li>{@code null}</li>
     *                  <li>{@link java.time.YearMonth}</li>
     *                  <li>{@link String}</li>
     *                  <li>{@link OutParameter}</li>
     *               </ol>
     *               if overflow ,the executeXxx() method emit(not throw) {@link JdbdException}
     *         </li>
     *         <li>{@link JdbdType#MONTH_DAY} support following java types :
     *               <ol>
     *                  <li>{@code null}</li>
     *                  <li>{@link java.time.MonthDay}</li>
     *                  <li>{@link String}</li>
     *                  <li>{@link OutParameter}</li>
     *               </ol>
     *               if overflow ,the executeXxx() method emit(not throw) {@link JdbdException}
     *         </li>
     *         <li>{@link JdbdType#YEAR} support following java types :
     *               <ol>
     *                  <li>{@code null}</li>
     *                  <li>{@link java.time.Year}</li>
     *                  <li>{@link Short}</li>
     *                  <li>{@link Integer}</li>
     *                  <li>{@link String}</li>
     *                  <li>{@link OutParameter}</li>
     *               </ol>
     *               if overflow ,the executeXxx() method emit(not throw) {@link JdbdException}
     *         </li>
     *         <li>{@link JdbdType#DURATION} support following java types :
     *               <ol>
     *                  <li>{@code null}</li>
     *                  <li>{@link java.time.Duration}</li>
     *                  <li>{@link String}</li>
     *                  <li>{@link OutParameter}</li>
     *               </ol>
     *               if overflow ,the executeXxx() method emit(not throw) {@link JdbdException}
     *         </li>
     *         <li>{@link JdbdType#PERIOD} support following java types :
     *               <ol>
     *                  <li>{@code null}</li>
     *                  <li>{@link java.time.Period}</li>
     *                  <li>{@link String}</li>
     *                  <li>{@link OutParameter}</li>
     *               </ol>
     *               if overflow ,the executeXxx() method emit(not throw) {@link JdbdException}
     *         </li>
     *         <li>{@link JdbdType#INTERVAL} support following java types :
     *               <ol>
     *                  <li>{@code null}</li>
     *                  <li>{@link String}</li>
     *                  <li>{@link OutParameter}</li>
     *               </ol>
     *               if overflow ,the executeXxx() method emit(not throw) {@link JdbdException}
     *         </li>
     *         <li>{@link JdbdType#ROWID} support following java types :
     *                <ol>
     *                    <li>{@code null}</li>
     *                    <li>{@link Byte}</li>
     *                    <li>{@link Short}</li>
     *                    <li>{@link Integer}</li>
     *                    <li>{@link Long}</li>
     *                    <li>{@link java.math.BigInteger}</li>
     *                    <li>{@link String}</li>
     *                    <li>{@link OutParameter}</li>
     *                </ol>
     *                if overflow ,the executeXxx() method emit(not throw) {@link JdbdException}
     *         </li>
     *         <li>{@link JdbdType#GEOMETRY}  support following java types :
     *                <ol>
     *                    <li>{@code null}</li>
     *                    <li>{@link Point} WKB or WKT</li>
     *                    <li>{@link String} WKT</li>
     *                    <li>{@code  byte[]} WKB</li>
     *                    <li>{@link Blob} WKB</li>
     *                    <li>{@link Clob} WKT</li>
     *                    <li>{@link BlobPath} WKB</li>
     *                    <li>{@link TextPath} WKT</li>
     *                    <li>{@link OutParameter}</li>
     *                </ol>
     *                if overflow ,the executeXxx() method emit(not throw) {@link JdbdException}
     *         </li>
     *     </ul>
     * <br/>
     *
     * @param indexBasedZero parameter placeholder index based zero, the first value is 0 .
     * @param dataType       parameter type is following type : <ul>
     *                       <li>{@link io.jdbd.meta.JdbdType} generic sql type,this method convert {@link io.jdbd.meta.JdbdType} to appropriate {@link io.jdbd.meta.SQLType},if fail throw {@link  JdbdException}</li>
     *                       <li>{@link io.jdbd.meta.SQLType} driver have known database build-in data type. It is defined by driver developer.</li>
     *                       <li>the {@link DataType} that application developer define type and it's {@link DataType#typeName()} is supported by database. see {@link DataType#buildIn(String)} and {@link DataType#userDefined(String)}
     *                             <ul>
     *                                 <li>If {@link DataType#typeName()} is database build-in type,this method convert dataType to appropriate {@link io.jdbd.meta.SQLType} .</li>
     *                                 <li>Else if database support user_defined type,then use dataType.</li>
     *                                 <li>Else throw {@link JdbdException}</li>
     *                             </ul>
     *                       </li>
     *                       </ul>
     * @param value          nullable the parameter value; is following type :
     *                       <ul>
     *                          <li>generic java type,for example : {@link Boolean} , {@link Integer} , {@link String} ,{@link Enum} ,byte[],{@code Integer[]} ,{@link java.time.LocalDateTime} , {@link java.time.Duration} ,{@link java.time.YearMonth} ,{@link java.util.BitSet},{@link java.util.List}</li>
     *                          <li>{@link Point} spatial point type,spatial have two format : WKB and WKT, see the java doc of the implementation of this method. </li>
     *                          <li>{@link Parameter} :
     *                              <ol>
     *                                  <li>{@link OutParameter} that representing OUT parameter of stored procedure/function</li>
     *                                  <li>{@link Blob} long binary</li>
     *                                  <li>{@link Clob} long string</li>
     *                                  <li>{@link BlobPath} long binary,if {@link BlobPath#isDeleteOnClose()} is true , driver will delete file on close,see {@link java.nio.file.StandardOpenOption#DELETE_ON_CLOSE}</li>
     *                                  <li>{@link TextPath} long text,if {@link TextPath#isDeleteOnClose()} is true , driver will delete file on close,see {@link java.nio.file.StandardOpenOption#DELETE_ON_CLOSE}</li>
     *                              </ol>
     *                          </li>
     *                       </ul>
     * @return <strong>this</strong>
     * @throws NullPointerException throw when dataType is null.
     * @throws JdbdException        throw when : <ul>
     *                              <li>this statement instance is reused.Because jdbd is reactive and multi-thread and jdbd provide :
     *                                     <ol>
     *                                         <li>{@link MultiResultStatement#executeBatchUpdate()}</li>
     *                                         <li>{@link MultiResultStatement#executeBatchQuery()} </li>
     *                                         <li>{@link MultiResultStatement#executeBatchAsMulti()}</li>
     *                                         <li>{@link MultiResultStatement#executeBatchAsFlux()}</li>
     *                                     </ol>
     *                                     ,so you don't need to reuse statement instance.
     *                              </li>
     *                              <li>indexBasedZero error</li>
     *                              <li>dataType is one of following :
     *                                     <ul>
     *                                         <li>{@link io.jdbd.meta.JdbdType#UNKNOWN}</li>
     *                                         <li>{@link io.jdbd.meta.JdbdType#DIALECT_TYPE}</li>
     *                                         <li>{@link io.jdbd.meta.JdbdType#REF_CURSOR}</li>
     *                                         <li>{@link io.jdbd.meta.JdbdType#ARRAY}</li>
     *                                         <li>{@link io.jdbd.meta.JdbdType#COMPOSITE}</li>
     *                                     </ul>
     *                              </li>
     *                              <li>dataType isn't supported by database.</li>
     *                              <li>dataType is {@link io.jdbd.meta.JdbdType#NULL} and value isn't null</li>
     *                              </ul>
     * @see DataType#buildIn(String)
     * @see DataType#userDefined(String)
     */
    ParametrizedStatement bind(int indexBasedZero, DataType dataType, @Nullable Object value) throws JdbdException;


    /**
     * {@inheritDoc }
     */
    @Override
    ParametrizedStatement bindStmtVar(String name, DataType dataType, @Nullable Object value) throws JdbdException;


    /**
     * {@inheritDoc }
     */
    @Override
    ParametrizedStatement setTimeout(int millSeconds) throws IllegalArgumentException;

    /**
     * {@inheritDoc }
     */
    @Override
    ParametrizedStatement setFetchSize(int fetchSize) throws IllegalArgumentException;

    /**
     * {@inheritDoc }
     */
    @Override
    ParametrizedStatement setImportPublisher(Function<ChunkOption, Publisher<byte[]>> function) throws JdbdException;

    /**
     * {@inheritDoc }
     */
    @Override
    ParametrizedStatement setExportSubscriber(Function<ChunkOption, Subscriber<byte[]>> function) throws JdbdException;


    /**
     * {@inheritDoc }
     */
    <T> ParametrizedStatement setOption(Option<T> option, @Nullable T value) throws JdbdException;


}
