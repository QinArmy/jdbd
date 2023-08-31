package io.jdbd.statement;

import io.jdbd.JdbdException;
import io.jdbd.lang.Nullable;
import io.jdbd.meta.DataType;
import io.jdbd.session.ChunkOption;
import io.jdbd.session.Option;
import io.jdbd.type.*;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import java.util.function.Function;

/**
 * <p>
 * This interface representing parametrized statement that SQL parameter placeholder must be {@code ?} .
 * </p>
 * <p>
 * This interface is base interface of following :
 *     <ul>
 *         <li>{@link BindStatement}</li>
 *         <li>{@link PreparedStatement}</li>
 *         <li>{@link MultiStatement}</li>
 *     </ul>
 * </p>
 *
 * @since 1.0
 */
public interface ParametrizedStatement extends Statement {


    /**
     * <p>
     * Bind parameter value to statement that exists SQL parameter placeholder and SQL parameter placeholder must be {@code ?}
     * </p>
     * <p>
     * <strong>NOTE</strong> : the driver developer must provide the java doc(html list) in the implementation of this method for explaining :
     * <ul>
     *     <li>the rule of {@link DataType} converting </li>
     *     <li>the rule of {@link DataType} supporting java type</li>
     * </ul>
     *
     * </p>
     *
     * @param indexBasedZero parameter placeholder index based zero, the first value is 0 .
     * @param dataType       parameter type is following type : <ul>
     *                       <li>{@link io.jdbd.meta.JdbdType} generic sql type,this method convert {@link io.jdbd.meta.JdbdType} to appropriate {@link io.jdbd.meta.SQLType},if fail throw {@link  JdbdException}</li>
     *                       <li>{@link io.jdbd.meta.SQLType} driver have known database build-in data type. It is defined by driver developer.</li>
     *                       <li>the {@link DataType} that application developer define type and it's {@link DataType#typeName()} is supported by database.
     *                             <ul>
     *                                 <li>If {@link DataType#typeName()} is database build-in type,this method convert dataType to appropriate {@link io.jdbd.meta.SQLType} . now {@link DataType#isUserDefined()} return false, or throw {@link JdbdException}.</li>
     *                                 <li>Else if database support user_defined type,then use dataType. now {@link DataType#isUserDefined()} should return true,if it's user_defined type</li>
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
     *                                  <li>{@link Text} long text</li>
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
     *                                         <li>{@link io.jdbd.meta.JdbdType#USER_DEFINED}</li>
     *                                         <li>{@link io.jdbd.meta.JdbdType#REF_CURSOR}</li>
     *                                         <li>{@link io.jdbd.meta.JdbdType#ARRAY}</li>
     *                                         <li>{@link io.jdbd.meta.JdbdType#COMPOSITE}</li>
     *                                         <li>{@link io.jdbd.meta.JdbdType#INTERNAL_USE}</li>
     *                                     </ul>
     *                              </li>
     *                              <li>dataType isn't supported by database.</li>
     *                              <li>dataType is {@link io.jdbd.meta.JdbdType#NULL} and value isn't null</li>
     *                              </ul>
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
    ParametrizedStatement setTimeout(int seconds) throws JdbdException;

    /**
     * {@inheritDoc }
     */
    @Override
    ParametrizedStatement setFetchSize(int fetchSize) throws JdbdException;

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
