package io.jdbd.statement;


import io.jdbd.JdbdException;
import io.jdbd.lang.Nullable;
import io.jdbd.meta.DataType;
import io.jdbd.meta.JdbdType;
import io.jdbd.session.*;
import io.jdbd.type.*;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;


/**
 * <p>This interface is base interface of following interfaces:
 *     <ul>
 *         <li>{@link StaticStatement}</li>
 *         <li>{@link BindStatement}</li>
 *         <li>{@link PreparedStatement}</li>
 *         <li>{@link MultiStatement}</li>
 *     </ul>
 * <p>
 *     NOTE: {@link Statement} is auto close after you invoke executeXxx() method,or binding occur error,so
 *     {@link Statement} have no close() method.
 * <br/>
 *
 * @see StaticStatement
 * @see BindStatement
 * @see PreparedStatement
 * @see MultiStatement
 */
public interface Statement extends SessionHolderSpec, OptionSpec {


    /**
     * <p>
     * Bind value to statement variable : <ul>
     * <li>statement variable is send with the statement.</li>
     * <li>statement variable exist until statement execution ends, at which point the statement variable set is cleared.</li>
     * <li>While statement variable exist, they can be accessed on the server side.</li>
     * </ul>
     * <br/>
     * <p>
     * Each {@link JdbdType} instance support java type rule:
     *     <ul>
     *         <li>{@link JdbdType#NULL} support only {@code null}</li>
     *         <li>{@link JdbdType#BOOLEAN} support following java types :
     *             <ol>
     *                 <li>{@code null}</li>
     *                 <li>{@link Boolean}</li>
     *                 <li>{@link Number} , zero value : convert to {@link Boolean#FALSE} ; non-zero value : convert to {@link Boolean#TRUE}</li>
     *                 <li>{@link String},ignore case 'TRUE','T','ON','YES' :  convert to {@link Boolean#TRUE} ; ignore case 'FALSE','F','OFF','NO' :  convert to {@link Boolean#FALSE}; other : the executeXxx() method emit(not throw) {@link JdbdException}</li>
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
     *              </ol>
     *               if overflow ,the executeXxx() method emit(not throw) {@link JdbdException}
     *         </li>
     *         <li>{@link JdbdType#FLOAT} and {@link JdbdType#DOUBLE} and {@link JdbdType#REAL}  support following java types :
     *              <ol>
     *                  <li>{@code null}</li>
     *                  <li>{@link Float}</li>
     *                  <li>{@link Double}</li>
     *                  <li>{@link String}</li>
     *                  <li>{@link Boolean} , true : 1.0 ; false : 0.0</li>
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
     *               </ol>
     *               if overflow ,the executeXxx() method emit(not throw) {@link JdbdException}
     *         </li>
     *         <li>{@link JdbdType#TIME}  support following java types :
     *               <ol>
     *                  <li>{@code null}</li>
     *                  <li>{@link java.time.LocalTime}</li>
     *                  <li>{@link String}</li>
     *                  <li>{@link java.time.OffsetTime},if database don't support time with timezone,then convert, like MySQL,else (eg: PostgreSQL) overflow</li>
     *               </ol>
     *               if overflow ,the executeXxx() method emit(not throw) {@link JdbdException}
     *         </li>
     *         <li>{@link JdbdType#TIME_WITH_TIMEZONE}  support following java types :
     *               <ol>
     *                  <li>{@code null}</li>
     *                  <li>{@link java.time.OffsetTime}</li>
     *                  <li>{@link String}</li>
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
     *               </ol>
     *               if overflow ,the executeXxx() method emit(not throw) {@link JdbdException}
     *         </li>
     *         <li>{@link JdbdType#TIMESTAMP_WITH_TIMEZONE}  support following java types :
     *               <ol>
     *                  <li>{@code null}</li>
     *                  <li>{@link String}</li>
     *                  <li>{@link java.time.OffsetTime}</li>
     *                  <li>{@link java.time.ZonedDateTime}</li>
     *               </ol>
     *               if overflow ,the executeXxx() method emit(not throw) {@link JdbdException}
     *         </li>
     *         <li>{@link JdbdType#YEAR_MONTH} support following java types :
     *               <ol>
     *                  <li>{@code null}</li>
     *                  <li>{@link java.time.YearMonth}</li>
     *                  <li>{@link String}</li>
     *               </ol>
     *               if overflow ,the executeXxx() method emit(not throw) {@link JdbdException}
     *         </li>
     *         <li>{@link JdbdType#MONTH_DAY} support following java types :
     *               <ol>
     *                  <li>{@code null}</li>
     *                  <li>{@link java.time.MonthDay}</li>
     *                  <li>{@link String}</li>
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
     *               </ol>
     *               if overflow ,the executeXxx() method emit(not throw) {@link JdbdException}
     *         </li>
     *         <li>{@link JdbdType#DURATION} support following java types :
     *               <ol>
     *                  <li>{@code null}</li>
     *                  <li>{@link java.time.Duration}</li>
     *                  <li>{@link String}</li>
     *               </ol>
     *               if overflow ,the executeXxx() method emit(not throw) {@link JdbdException}
     *         </li>
     *         <li>{@link JdbdType#PERIOD} support following java types :
     *               <ol>
     *                  <li>{@code null}</li>
     *                  <li>{@link java.time.Period}</li>
     *                  <li>{@link String}</li>
     *               </ol>
     *               if overflow ,the executeXxx() method emit(not throw) {@link JdbdException}
     *         </li>
     *         <li>{@link JdbdType#INTERVAL} support following java types :
     *               <ol>
     *                  <li>{@code null}</li>
     *                  <li>{@link String}</li>
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
     *                </ol>
     *                if overflow ,the executeXxx() method emit(not throw) {@link JdbdException}
     *         </li>
     *     <li>{@link JdbdType#GEOMETRY}  support following java types :
     *                <ol>
     *                    <li>{@code null}</li>
     *                    <li>{@link Point} WKB or WKT</li>
     *                    <li>{@link String} WKT</li>
     *                    <li>{@code  byte[]} WKB</li>
     *                    <li>{@link Blob} WKB</li>
     *                    <li>{@link Clob} WKT</li>
     *                    <li>{@link BlobPath} WKB</li>
     *                    <li>{@link TextPath} WKT</li>
     *                </ol>
     *                if overflow ,the executeXxx() method emit(not throw) {@link JdbdException}
     *     </li>
     *     </ul>
     * <p>
     * <br/>
     *
     * @param name     statement variable name,must have text.
     * @param dataType parameter type is following type : <ul>
     *                 <li>{@link io.jdbd.meta.JdbdType}  generic sql type,this method convert {@link io.jdbd.meta.JdbdType} to appropriate {@link io.jdbd.meta.SQLType},if fail throw {@link  JdbdException}</li>
     *                 <li>{@link io.jdbd.meta.SQLType} driver have known database build-in data type. It is defined by driver developer.</li>
     *                 <li>the {@link DataType} that application developer define type and it's {@link DataType#typeName()} is supported by database.
     *                       <ul>
     *                           <li>If {@link DataType#typeName()} is database build-in type,this method convert dataType to appropriate {@link io.jdbd.meta.SQLType} .</li>
     *                           <li>Else if database support user_defined type,then use dataType.</li>
     *                           <li>Else throw {@link JdbdException}</li>
     *                       </ul>
     *                 </li>
     *                 </ul>
     * @param value    nullable the parameter value; be following type :
     *                 <ul>
     *                    <li>generic java type,for example : {@link Boolean} , {@link Integer} , {@link String} , {@code byte[]},{@code Integer[]} ,{@link java.time.LocalDateTime} , {@link java.time.Duration} ,{@link java.time.YearMonth} ,{@link java.util.BitSet},{@link java.util.List}</li>
     *                     <li>{@link Point} spatial point type,spatial have two format : WKB and WKT, see the java doc of the implementation of this method. </li>
     *                    <li>{@link Parameter} :
     *                        <ol>
     *                            <li>{@link Blob} long binary</li>
     *                            <li>{@link Clob} long string</li>
     *                            <li>{@link BlobPath} long binary,if {@link BlobPath#isDeleteOnClose()} is true , driver will delete file on close,see {@link java.nio.file.StandardOpenOption#DELETE_ON_CLOSE}</li>
     *                            <li>{@link TextPath} long text,if {@link TextPath#isDeleteOnClose()} is true , driver will delete file on close,see {@link java.nio.file.StandardOpenOption#DELETE_ON_CLOSE}</li>
     *                        </ol>
     *                    </li>
     *                 </ul>
     * @return <strong>this</strong>
     * @throws NullPointerException throw when dataType is null.
     * @throws JdbdException        throw when : <ul>
     *                              <li>{@link DatabaseSession#isSupportStmtVar()} or {@link #isSupportStmtVar()} return false</li>
     *                              <li>this statement instance is reused.Because jdbd is reactive and multi-thread and jdbd provide :
     *                                            <ol>
     *                                                <li>{@link MultiResultStatement#executeBatchUpdate()}</li>
     *                                                <li>{@link MultiResultStatement#executeBatchQuery()} </li>
     *                                                <li>{@link MultiResultStatement#executeBatchAsMulti()}</li>
     *                                                <li>{@link MultiResultStatement#executeBatchAsFlux()}</li>
     *                                            </ol>
     *                                            ,so you don't need to reuse statement instance.
     *                              </li>
     *                              <li>name have no text</li>
     *                              <li>name duplication</li>
     *                              <li>indexBasedZero error</li>
     *                              <li>dataType is one of following :
     *                                   <ul>
     *                                                <li>{@link io.jdbd.meta.JdbdType#UNKNOWN}</li>
     *                                                <li>{@link io.jdbd.meta.JdbdType#DIALECT_TYPE}</li>
     *                                                <li>{@link io.jdbd.meta.JdbdType#REF_CURSOR}</li>
     *                                                <li>{@link io.jdbd.meta.JdbdType#ARRAY}</li>
     *                                                <li>{@link io.jdbd.meta.JdbdType#COMPOSITE}</li>
     *                                     </ul>
     *                              </li>
     *                              <li>dataType isn't supported by database.</li>
     *                              <li>dataType is {@link io.jdbd.meta.JdbdType#NULL} and value isn't null</li>
     *                              </ul>
     * @see DatabaseSession#isSupportStmtVar()
     * @see #isSupportStmtVar()
     * @see io.jdbd.meta.JdbdType
     * @see io.jdbd.meta.SQLType
     * @see Point
     * @see Blob
     * @see Clob
     * @see BlobPath
     * @see TextPath
     */
    Statement bindStmtVar(String name, DataType dataType, @Nullable Object value) throws JdbdException;


    /**
     * This statement whether support {@link PublisherParameter} or not
     *
     * @return true : support {@link PublisherParameter}
     */
    boolean isSupportPublisher();

    /**
     * This statement whether support {@link PathParameter} or not
     *
     * @return true : support {@link PathParameter}
     */
    boolean isSupportPath();

    /**
     * This statement whether support {@link OutParameter} or not
     *
     * @return true : support {@link OutParameter}
     */
    boolean isSupportOutParameter();

    /**
     * This statement whether support {@link #bindStmtVar(String, DataType, Object)} or not
     *
     * @return true : support {@link #bindStmtVar(String, DataType, Object)}
     */
    boolean isSupportStmtVar();

    /**
     * This statement whether support {@link #setImportPublisher(Function)} or not.
     *
     * @return true : support
     */
    boolean isSupportImportPublisher();

    /**
     * This statement whether support {@link #setExportSubscriber(Function)} or not.
     *
     * @return true : support
     */
    boolean isSupportExportSubscriber();

    /**
     * <p>
     * Set statement timeout seconds,if timeout driver will kill query.
     * <br/>
     *
     * @param millSeconds <ul>
     *                    <li>0 : no limit,this is default value</li>
     *                    <li>positive : timeout seconds</li>
     *                    <li>negative : error</li>
     *                    </ul>
     * @return <strong>this</strong>
     * @throws IllegalArgumentException throw when seconds is negative.
     */
    Statement setTimeout(int millSeconds) throws IllegalArgumentException;


    /**
     * Currently,only following methods support this method:
     *
     * <ul>
     *    <li>{@link BindSingleStatement#executeQuery(Function, Consumer)}</li>
     *    <li>{@link BindSingleStatement#executeAsFlux()}</li>
     * </ul>
     *
     * <p>Driver will continue fetch util you cancel subscribing.
     *
     * @param fetchSize non-negative,0 : return all row.
     * @return <strong>this</strong>
     * @throws IllegalArgumentException throw when fetchSize is negative.
     */
    Statement setFetchSize(int fetchSize) throws IllegalArgumentException;

    /**
     * Some database allows high-speed bulk data transfer to or from the server. For example : PostgreSQL COPY command
     *
     * @param function map Publisher function
     * @return <strong>this</strong>
     * @throws JdbdException throw when
     *                       <ul>
     *                           <li>{@link DatabaseSession#isSupportImportPublisher()} return false</li>
     *                           <li>{@link #isSupportImportPublisher()} return false</li>
     *                       </ul>
     */
    Statement setImportPublisher(Function<ChunkOption, Publisher<byte[]>> function) throws JdbdException;


    /**
     * Some database allows high-speed bulk data transfer to or from the server. For example : PostgreSQL COPY command
     *
     * @param function map Subscriber function
     * @return <strong>this</strong>
     * @throws JdbdException throw when
     *                       <ul>
     *                           <li>{@link DatabaseSession#isSupportExportSubscriber()} return false</li>
     *                           <li>{@link #isSupportExportSubscriber()}  return false</li>
     *                       </ul>
     */
    Statement setExportSubscriber(Function<ChunkOption, Subscriber<byte[]>> function) throws JdbdException;

    /**
     * <p>
     * Set dialect statement option.
     * <br/>
     *
     * @param option statement option key
     * @param value  statement option value
     * @param <T>    option value java type
     * @return <strong>this</strong>
     * @throws JdbdException throw when {@link #supportedOptionList()} is empty.
     * @see #supportedOptionList()
     */
    <T> Statement setOption(Option<T> option, @Nullable T value) throws JdbdException;

    /**
     * The option list of {@link #setOption(Option, Object)} supporting.
     *
     * @return empty or the list that {@link #setOption(Option, Object)} support option list.
     */
    List<Option<?>> supportedOptionList();

    /**
     * <p>
     * This implementation of this method perhaps support some of following :
     *     <ul>
     *         <li>{@link Option#BACKSLASH_ESCAPES}</li>
     *         <li>{@link Option#BINARY_HEX_ESCAPES}</li>
     *         <li>{@link Option#CLIENT_CHARSET}</li>
     *         <li>{@link Option#CLIENT_ZONE} if database build-in time and datetime don't support zone</li>
     *     </ul>
     * <br/>
     */
    @Nullable
    @Override
    <T> T valueOf(Option<T> option);


}
