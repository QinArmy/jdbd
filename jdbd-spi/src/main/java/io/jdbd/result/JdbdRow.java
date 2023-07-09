package io.jdbd.result;

import io.jdbd.JdbdException;
import io.jdbd.lang.Nullable;
import io.jdbd.meta.SQLType;
import org.reactivestreams.Publisher;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 *     This interface is base interface of following :
 *     <ul>
 *         <li>{@link ResultRow}</li>
 *         <li>{@link CurrentRow}</li>
 *     </ul>
 * </p>
 * @since 1.0
 */
public interface JdbdRow extends Result {


    ResultRowMeta getRowMeta();

    /**
     * @see SQLType#outputJavaType()
     */
    @Nullable
    Object get(int indexBasedZero) throws JdbdException;

    @Nullable
    <T> T get(int indexBasedZero, Class<T> columnClass) throws JdbdException;

    Object getNonNull(int indexBasedZero) throws NullPointerException, JdbdException;

    <T> T getNonNull(int indexBasedZero, Class<T> columnClass) throws NullPointerException, JdbdException;

    <T> List<T> getList(int indexBasedZero, Class<T> elementClass) throws JdbdException;


    <T> Set<T> getSet(int indexBasedZero, Class<T> elementClass) throws JdbdException;


    <K, V> Map<K, V> getMap(int indexBasedZero, Class<K> keyClass, Class<V> valueClass) throws JdbdException;


    <T> Publisher<T> getPublisher(int indexBasedZero, Class<T> valueClass) throws JdbdException;


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
    Object get(String columnLabel)throws JdbdException;



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
     */
    @Nullable
    <T> T get(String columnLabel, Class<T> columnClass)throws JdbdException;


    Object getNonNull(String columnLabel)throws NullPointerException,JdbdException;

    <T> T getNonNull(String columnLabel, Class<T> columnClass) throws NullPointerException,JdbdException;


    /**
     * @see #getSet(int, Class)
     */
    <T> List<T> getList(String columnLabel, Class<T> elementClass)throws JdbdException;


    /**
     * @see #getSet(int, Class)
     */
    <T> Set<T> getSet(String columnLabel, Class<T> elementClass)throws JdbdException;


    <K, V> Map<K, V> getMap(String columnLabel, Class<K> keyClass, Class<V> valueClass)throws JdbdException;

    <T> Publisher<T> getPublisher(String columnLabel, Class<T> valueClass)throws JdbdException;


}
