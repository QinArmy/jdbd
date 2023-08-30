package io.jdbd.vendor.result;

import io.jdbd.JdbdException;
import io.jdbd.result.CurrentRow;
import io.jdbd.result.DataRow;
import io.jdbd.result.QueryResults;
import io.jdbd.vendor.task.ITaskAdjutant;
import io.jdbd.vendor.util.JdbdCollections;
import io.jdbd.vendor.util.JdbdExceptions;
import org.reactivestreams.Publisher;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.IntFunction;

/**
 * <p>
 * This class is base class of the implementation of {@link DataRow}.
 * If you use following method:
 * <ul>
 *     <li>{@link MultiResults#asMulti(ITaskAdjutant, Consumer)}</li>
 *     <li>{@link MultiResults#batchQuery(ITaskAdjutant, Consumer)}</li>
 * </ul>
 * then you must extend this class,because {@link #copyCurrentRowIfNeed()} perhaps is invoked.
 * </p>
 *
 * @since 1.0
 */
public abstract class VendorDataRow implements DataRow {


    @Override
    public final int getColumnCount() {
        return this.getRowMeta().getColumnCount();
    }

    @Override
    public final String getColumnLabel(int indexBasedZero) throws JdbdException {
        return this.getRowMeta().getColumnLabel(indexBasedZero);
    }

    @Override
    public final Object getNonNull(final int indexBasedZero) throws NullPointerException, JdbdException {
        final Object value;
        value = this.get(indexBasedZero);
        if (value == null) {
            throw JdbdExceptions.columnIsNull(this.getColumnMeta(indexBasedZero));
        }
        return value;
    }

    @Override
    public final <T> T getNonNull(final int indexBasedZero, final Class<T> columnClass)
            throws NullPointerException, JdbdException {
        final T value;
        value = this.get(indexBasedZero, columnClass);
        if (value == null) {
            throw JdbdExceptions.columnIsNull(this.getColumnMeta(indexBasedZero));
        }
        return value;
    }

    @Override
    public final <T> List<T> getList(int indexBasedZero, Class<T> elementClass) throws JdbdException {
        return this.getList(indexBasedZero, elementClass, JdbdCollections::arrayList);
    }

    @Override
    public final <T> Set<T> getSet(int indexBasedZero, Class<T> elementClass) throws JdbdException {
        return this.getSet(indexBasedZero, elementClass, JdbdCollections::hashSet);
    }

    @Override
    public final <K, V> Map<K, V> getMap(int indexBasedZero, Class<K> keyClass, Class<V> valueClass)
            throws JdbdException {
        return this.getMap(indexBasedZero, keyClass, valueClass, JdbdCollections::hashMap);
    }


    /*-------------------below columnLabel method-------------------*/

    @Override
    public final boolean isBigColumn(String columnLabel) {
        return this.isBigColumn(getRowMeta().getColumnIndex(columnLabel));
    }

    @Override
    public final boolean isNull(String columnLabel) throws JdbdException {
        return this.isNull(getRowMeta().getColumnIndex(columnLabel));
    }

    @Override
    public final int getColumnIndex(String columnLabel) throws JdbdException {
        return this.getRowMeta().getColumnIndex(columnLabel);
    }

    @Override
    public final Object get(String columnLabel) throws JdbdException {
        return this.get(getRowMeta().getColumnIndex(columnLabel));
    }

    @Override
    public final <T> T get(String columnLabel, Class<T> columnClass) throws JdbdException {
        return this.get(getRowMeta().getColumnIndex(columnLabel), columnClass);
    }

    @Override
    public final Object getNonNull(String columnLabel) throws NullPointerException, JdbdException {
        return this.getNonNull(getRowMeta().getColumnIndex(columnLabel));
    }

    @Override
    public final <T> T getNonNull(String columnLabel, Class<T> columnClass) throws NullPointerException, JdbdException {
        return this.getNonNull(getRowMeta().getColumnIndex(columnLabel), columnClass);
    }

    @Override
    public final <T> List<T> getList(String columnLabel, Class<T> elementClass) throws JdbdException {
        return this.getList(getRowMeta().getColumnIndex(columnLabel), elementClass, JdbdCollections::arrayList);
    }

    @Override
    public final <T> List<T> getList(String columnLabel, Class<T> elementClass, IntFunction<List<T>> constructor)
            throws JdbdException {
        return this.getList(getRowMeta().getColumnIndex(columnLabel), elementClass, constructor);
    }

    @Override
    public final <T> Set<T> getSet(String columnLabel, Class<T> elementClass) throws JdbdException {
        return this.getSet(getRowMeta().getColumnIndex(columnLabel), elementClass, JdbdCollections::hashSet);
    }

    @Override
    public final <T> Set<T> getSet(String columnLabel, Class<T> elementClass, IntFunction<Set<T>> constructor)
            throws JdbdException {
        return this.getSet(getRowMeta().getColumnIndex(columnLabel), elementClass, constructor);
    }

    @Override
    public final <K, V> Map<K, V> getMap(String columnLabel, Class<K> keyClass, Class<V> valueClass)
            throws JdbdException {
        return this.getMap(getRowMeta().getColumnIndex(columnLabel), keyClass, valueClass, JdbdCollections::hashMap);
    }

    @Override
    public final <K, V> Map<K, V> getMap(String columnLabel, Class<K> keyClass, Class<V> valueClass, IntFunction<Map<K, V>> constructor)
            throws JdbdException {
        return this.getMap(getRowMeta().getColumnIndex(columnLabel), keyClass, valueClass, constructor);
    }

    @Override
    public final <T> Publisher<T> getPublisher(String columnLabel, Class<T> valueClass) throws JdbdException {
        return this.getPublisher(getRowMeta().getColumnIndex(columnLabel), valueClass);
    }

    @Override
    public final String toString() {
        final StringBuilder builder = new StringBuilder(50);
        builder.append(getClass().getSimpleName())
                .append("[ resultNo : ")
                .append(getResultNo());

        if (this instanceof CurrentRow) {
            builder.append(" , rowNumber : ")
                    .append(((CurrentRow) this).rowNumber());
        }
        return builder.append(" , hash : ")
                .append(System.identityHashCode(this))
                .append(" ]")
                .toString();
    }

    protected abstract ColumnMeta getColumnMeta(int safeIndex);


    /**
     * <p>
     * This method is implemented by the sub class of {@link CurrentRow}.
     * <ul>
     *     This method is designed for the cache (perhaps occur) of following :
     *     <ul>
     *         <li>{@link QueryResults}</li>
     *         <li>{@link io.jdbd.result.MultiResult}</li>
     *     </ul>
     * </ul>
     * <ul>
     *     <li>If this current row is mutable,always copy this instance.</li>
     *     <li>Else return this</li>
     * </ul>
     * </p>
     *
     * @throws UnsupportedOperationException throw when this is the instance of {@link io.jdbd.result.ResultRow}.
     */
    protected CurrentRow copyCurrentRowIfNeed() {
        throw new UnsupportedOperationException();
    }


}
