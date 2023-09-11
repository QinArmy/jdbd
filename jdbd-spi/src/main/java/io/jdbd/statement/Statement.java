package io.jdbd.statement;


import io.jdbd.JdbdException;
import io.jdbd.lang.Nullable;
import io.jdbd.meta.DataType;
import io.jdbd.session.*;
import io.jdbd.type.*;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;


/**
 * <p>
 * This interface is base interface of following interfaces:
 *     <ul>
 *         <li>{@link StaticStatement}</li>
 *         <li>{@link BindStatement}</li>
 *         <li>{@link PreparedStatement}</li>
 *         <li>{@link MultiStatement}</li>
 *     </ul>
 * </p>
 * <p>
 *     NOTE: {@link Statement} is auto close after you invoke executeXxx() method,or binding occur error,so
 *     {@link Statement} have no close() method.
 * </p>
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
     * </p>
     *
     * @param name     statement variable name,must have text.
     * @param dataType parameter type is following type : <ul>
     *                 <li>{@link io.jdbd.meta.JdbdType}  generic sql type,this method convert {@link io.jdbd.meta.JdbdType} to appropriate {@link io.jdbd.meta.SQLType},if fail throw {@link  JdbdException}</li>
     *                 <li>{@link io.jdbd.meta.SQLType} driver have known database build-in data type. It is defined by driver developer.</li>
     *                 <li>the {@link DataType} that application developer define type and it's {@link DataType#typeName()} is supported by database.
     *                       <ul>
     *                           <li>If {@link DataType#typeName()} is database build-in type,this method convert dataType to appropriate {@link io.jdbd.meta.SQLType} . now {@link DataType#isUserDefined()} return false, or throw {@link JdbdException}.</li>
     *                           <li>Else if database support user_defined type,then use dataType. now {@link DataType#isUserDefined()} should return true,if it's user_defined type</li>
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
     *                                                <li>{@link io.jdbd.meta.JdbdType#USER_DEFINED}</li>
     *                                                <li>{@link io.jdbd.meta.JdbdType#REF_CURSOR}</li>
     *                                                <li>{@link io.jdbd.meta.JdbdType#ARRAY}</li>
     *                                                <li>{@link io.jdbd.meta.JdbdType#COMPOSITE}</li>
     *                                                <li>{@link io.jdbd.meta.JdbdType#INTERNAL_USE}</li>
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
     * @see Text
     * @see BlobPath
     * @see TextPath
     */
    Statement bindStmtVar(String name, DataType dataType, @Nullable Object value) throws JdbdException;


    /**
     * @return true : support {@link PublisherParameter}
     */
    boolean isSupportPublisher();

    /**
     * @return true : support {@link PathParameter}
     */
    boolean isSupportPath();

    boolean isSupportOutParameter();

    boolean isSupportStmtVar();

    /**
     * <p>
     * Set statement timeout seconds,if timeout driver will kill query.
     * </p>
     *
     * @param millSeconds <ul>
     *                    <li>0 : no limit,this is default value</li>
     *                    <li>positive : timeout seconds</li>
     *                    <li>negative : error</li>
     *                    </ul>
     * @return <strong>this</strong>
     * @throws IllegalArgumentException throw when seconds is negative.
     */
    Statement setTimeout(int millSeconds);


    /**
     * <p>
     * Currently,only following methods support this method:
     *     <ul>
     *         <li>{@link BindSingleStatement#executeQuery(Function, Consumer)}</li>
     *         <li>{@link BindSingleStatement#executeAsFlux()}</li>
     *     </ul>
     * </p>
     * <p>
     *     Driver will continue fetch util you cancel subscribing.
     * </p>
     *
     * @param fetchSize <ul>
     *                                                                                                                            <li>0 : fetch all, this is default value</li>
     *                                                                                                                            <li>positive : fetch size</li>
     *                                                                                                                            <li>negative : error</li>
     *                                                                                                                        </ul>
     * @return <strong>this</strong>
     * @throws IllegalArgumentException throw when fetchSize is negative.
     */
    Statement setFetchSize(int fetchSize);

    /**
     * @return <strong>this</strong>
     * @throws JdbdException throw when driver don't this method.
     */
    Statement setImportPublisher(Function<ChunkOption, Publisher<byte[]>> function) throws JdbdException;


    /**
     * @return <strong>this</strong>
     * @throws JdbdException throw when driver don't this method.
     */
    Statement setExportSubscriber(Function<ChunkOption, Subscriber<byte[]>> function) throws JdbdException;

    /**
     * <p>
     * Set dialect statement option.
     * </p>
     *
     * @return <strong>this</strong>
     * @throws JdbdException throw when {@link #supportedOptionList()} is empty.
     * @see #supportedOptionList()
     */
    <T> Statement setOption(Option<T> option, @Nullable T value) throws JdbdException;

    /**
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
     * </p>
     */
    @Nullable
    @Override
    <T> T valueOf(Option<T> option);


}
