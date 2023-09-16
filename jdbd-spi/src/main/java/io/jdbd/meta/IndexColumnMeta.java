package io.jdbd.meta;

import io.jdbd.session.OptionSpec;

/**
 * <p>
 * This interface representing the index column.
 * <br/>
 *
 * @see TableIndexMeta
 * @since 1.0
 */
public interface IndexColumnMeta extends OptionSpec {

    String columnName();

    /**
     * @return an estimate of the number of unique values in the index,
     */
    long cardinality();

    /**
     * @return the value explain how the column is sorted in the index.
     */
    Sorting sorting();

    /**
     * @return the value explain how the column nulls is sorted in the index.
     */
    NullsSorting nullsSorting();

    /**
     * @return {@link BooleanMode#TRUE} representing the column may contain NULL values
     */
    BooleanMode nullableMode();

    /**
     * @return {@link BooleanMode#TRUE} representing whether the index is visible or not to something (eg : the optimizer)
     */
    BooleanMode visibleMode();

    /**
     * override {@link Object#toString()}
     *
     * @return index column info, contain : <ol>
     * <li>implementation class name</li>
     * <li>{@link #columnName()}</li>
     * <li>{@link #cardinality()}</li>
     * <li>{@link #sorting()}</li>
     * <li>{@link #nullsSorting()}</li>
     * <li>{@link #nullableMode()}</li>
     * <li>{@link #visibleMode()}</li>
     * <li>{@link System#identityHashCode(Object)}</li>
     * </ol>
     */
    @Override
    String toString();

}
