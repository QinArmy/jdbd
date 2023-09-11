package io.jdbd.result;

import io.jdbd.JdbdException;
import io.jdbd.lang.Nullable;
import org.reactivestreams.Publisher;

import java.nio.file.CopyOption;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.IntFunction;
import java.util.function.Supplier;

/**
 * <p>
 * This interface is base interface of following :
 *     <ul>
 *         <li>{@link ResultRow}</li>
 *         <li>{@link CurrentRow}</li>
 *     </ul>
 * </p>
 * <p>
 * The {@link #getResultNo()} of this interface always return same value with {@link ResultRowMeta} in same query result.
 * See {@link #getRowMeta()}
 * </p>
 * <p>
 *     This interface have following core 'get' methods:
 *     <ul>
 *         <li>{@link #get(int)}</li>
 *         <li>{@link #get(int, Class)}</li>
 *         <li>{@link #getString(int)} ,this method is similar to {@linkplain  #get(int, Class String.class)}, except that binary or blob return normal string not hex string.</li>
 *         <li>{@link #getList(int, Class, IntFunction)}</li>
 *         <li>{@link #getSet(int, Class, IntFunction)}</li>
 *         <li>{@link #getMap(int, Class, Class, IntFunction)}</li>
 *         <li>{@link #getPublisher(int, Class)}</li>
 *     </ul>
 *     other 'get' methods are only the decoration of above methods.
 * </p>
 *
 * @since 1.0
 */
public interface DataRow extends ResultItem, ResultItem.ResultAccessSpec {


    ResultRowMeta getRowMeta();

    /**
     * <p>
     * Bit row means that exists at least one big column. see {@link #isBigColumn(int)}
     * </p>
     *
     * @return true : big row
     */
    boolean isBigRow();

    /**
     * <p>
     * Big column means that byte size is large,possibly is 1GB or more. For example LONG BLOB ,LONG TEXT,GEOMETRY_COLLECTION.
     * Driver cache them to local temp directory.
     * So {@link #get(int)} return :
     *      <ul>
     *          <li>{@link io.jdbd.type.BlobPath}</li>
     *          <li>{@link io.jdbd.type.TextPath}</li>
     *      </ul>
     *      If you need to store them ,then you must move them to other directory by {@link java.nio.file.Files#move(Path, Path, CopyOption...)} method.
     *      Else driver will delete them after result set end.
     * </p>
     *
     * @param indexBasedZero index based zero,the first value is 0 .
     * @return true : big column
     * @throws JdbdException throw when indexBasedZero error
     */
    boolean isBigColumn(int indexBasedZero) throws JdbdException;

    /**
     * @throws JdbdException throw when indexBasedZero error
     */
    boolean isNull(int indexBasedZero) throws JdbdException;

    /**
     * <p>
     * Get the value of output of column.
     * </p>
     *
     * @return must be one of following : <ol>
     * <li>null</li>
     * <li>the instance of {@link ResultRowMeta#getFirstJavaType(int) }</li>
     * <li>the instance of {@link ResultRowMeta#getSecondJavaType(int)}</li>
     * </ol>
     * @see ResultRowMeta#getFirstJavaType(int)
     * @see ResultRowMeta#getSecondJavaType(int)
     * @see #isBigColumn(int)
     */
    @Nullable
    Object get(int indexBasedZero) throws JdbdException;

    /**
     * @param defaultValue must be the instance of column first java type or second java type.
     * @return non-null value
     * @throws JdbdException throw when
     *                       <ul>
     *                           <li>{@link #get(int)} throw {@link JdbdException}</li>
     *                           <li>defaultValue isn't the instance of column first java type or second java type</li>
     *                       </ul>
     * @see ResultRowMeta#getFirstJavaType(int)
     * @see ResultRowMeta#getSecondJavaType(int)
     */
    Object getOrDefault(int indexBasedZero, Object defaultValue) throws JdbdException;

    /**
     * @param supplier must return the instance of column first java type or second java type.
     * @return non-null value
     * @throws JdbdException throw when
     *                       <ul>
     *                           <li>{@link #get(int)} throw {@link JdbdException}</li>
     *                           <li>defaultValue isn't the instance of column first java type or second java type</li>
     *                       </ul>
     * @see ResultRowMeta#getFirstJavaType(int)
     * @see ResultRowMeta#getSecondJavaType(int)
     */
    Object getOrSupplier(int indexBasedZero, Supplier<Object> supplier) throws JdbdException;


    @Nullable
    <T> T get(int indexBasedZero, Class<T> columnClass) throws JdbdException;

    /**
     * <p>
     * This method is similar to {@linkplain  #get(int, Class String.class)}, except that binary or blob return normal string not hex string.
     * </p>
     *
     * @see #get(int, Class)
     */
    @Nullable
    String getString(int indexBasedZero) throws JdbdException;

    String getStringOrDefault(int indexBasedZero, String defaultValue) throws JdbdException;

    String getStringOrSupplier(int indexBasedZero, Supplier<String> supplier) throws JdbdException;

    String getNonNullString(int indexBasedZero) throws JdbdException, NullPointerException;

    /**
     * @param defaultValue must be the instance of column first java type or second java type.
     * @return non-null value
     * @throws JdbdException throw when
     *                       <ul>
     *                           <li>{@link #get(int, Class)} throw {@link JdbdException}</li>
     *                           <li>defaultValue isn't the instance columnClass</li>
     *                       </ul>
     */
    <T> T getOrDefault(int indexBasedZero, Class<T> columnClass, T defaultValue) throws JdbdException;

    /**
     * @param supplier must return the instance of column first java type or second java type.
     * @return non-null value
     * @throws JdbdException throw when
     *                       <ul>
     *                           <li>{@link #get(int)} throw {@link JdbdException}</li>
     *                           <li>supplier return value isn't the instance columnClass</li>
     *                       </ul>
     * @see ResultRowMeta#getFirstJavaType(int)
     * @see ResultRowMeta#getSecondJavaType(int)
     */
    <T> T getOrSupplier(int indexBasedZero, Class<T> columnClass, Supplier<T> supplier) throws JdbdException;


    Object getNonNull(int indexBasedZero) throws NullPointerException, JdbdException;

    <T> T getNonNull(int indexBasedZero, Class<T> columnClass) throws NullPointerException, JdbdException;


    <T> List<T> getList(int indexBasedZero, Class<T> elementClass) throws JdbdException;

    /**
     * @return <ul>
     * <li>null : always {@link Collections#emptyList()}</li>
     * <li>the instance that constructor return</li>
     * </ul>
     */
    <T> List<T> getList(int indexBasedZero, Class<T> elementClass, IntFunction<List<T>> constructor) throws JdbdException;

    <T> Set<T> getSet(int indexBasedZero, Class<T> elementClass) throws JdbdException;

    /**
     * @return <ul>
     * <li>null : always {@link Collections#emptySet()}</li>
     * <li>the instance that constructor return</li>
     * </ul>
     */
    <T> Set<T> getSet(int indexBasedZero, Class<T> elementClass, IntFunction<Set<T>> constructor) throws JdbdException;


    <K, V> Map<K, V> getMap(int indexBasedZero, Class<K> keyClass, Class<V> valueClass) throws JdbdException;

    /**
     * @return <ul>
     * <li>null : always {@link Collections#emptyMap()}</li>
     * <li>the instance that constructor return</li>
     * </ul>
     */
    <K, V> Map<K, V> getMap(int indexBasedZero, Class<K> keyClass, Class<V> valueClass, IntFunction<Map<K, V>> constructor) throws JdbdException;


    <T> Publisher<T> getPublisher(int indexBasedZero, Class<T> valueClass) throws JdbdException;


    boolean isBigColumn(String columnLabel);

    /**
     * @throws JdbdException throw when columnLabel error
     */
    boolean isNull(String columnLabel) throws JdbdException;

    /**
     * <p>
     * This method is equivalent to below:
     * <pre>
     *        final int columnIndex  = this.getRowMeta().getColumnIndex(columnLabel)
     *        return get(columnIndex);
     *     </pre>
     * </p>
     *
     * @see #get(int)
     */
    @Nullable
    Object get(String columnLabel) throws JdbdException;


    Object getOrDefault(String columnLabel, Object defaultValue) throws JdbdException;

    Object getOrSupplier(String columnLabel, Supplier<Object> supplier) throws JdbdException;


    /**
     * <p>
     * This method is equivalent to below:
     * <pre>
     *        final int columnIndex  = this.getRowMeta().getColumnIndex(columnLabel)
     *        return get(columnIndex,columnClass);
     *     </pre>
     * </p>
     *
     * @see #get(int, Class)
     * @see #getString(int)
     */
    @Nullable
    <T> T get(String columnLabel, Class<T> columnClass) throws JdbdException;


    @Nullable
    String getString(String columnLabel) throws JdbdException;

    String getStringOrDefault(String columnLabel, String defaultValue) throws JdbdException;

    String getStringOrSupplier(String columnLabel, Supplier<String> supplier) throws JdbdException;

    String getNonNullString(String columnLabel) throws JdbdException, NullPointerException;

    <T> T getOrDefault(String columnLabel, Class<T> columnClass, T defaultValue) throws JdbdException;

    <T> T getOrSupplier(String columnLabel, Class<T> columnClass, Supplier<T> supplier) throws JdbdException;

    Object getNonNull(String columnLabel) throws NullPointerException, JdbdException;

    <T> T getNonNull(String columnLabel, Class<T> columnClass) throws NullPointerException, JdbdException;


    /**
     * @see #getSet(int, Class)
     */
    <T> List<T> getList(String columnLabel, Class<T> elementClass) throws JdbdException;

    <T> List<T> getList(String columnLabel, Class<T> elementClass, IntFunction<List<T>> constructor) throws JdbdException;

    /**
     * @see #getSet(int, Class)
     */
    <T> Set<T> getSet(String columnLabel, Class<T> elementClass) throws JdbdException;

    <T> Set<T> getSet(String columnLabel, Class<T> elementClass, IntFunction<Set<T>> constructor) throws JdbdException;


    <K, V> Map<K, V> getMap(String columnLabel, Class<K> keyClass, Class<V> valueClass) throws JdbdException;

    /**
     * <p>
     * This can be useful in various scenarios,for example postgre hstore.
     * </p>
     */
    <K, V> Map<K, V> getMap(String columnLabel, Class<K> keyClass, Class<V> valueClass, IntFunction<Map<K, V>> constructor) throws JdbdException;

    <T> Publisher<T> getPublisher(String columnLabel, Class<T> valueClass) throws JdbdException;


}
