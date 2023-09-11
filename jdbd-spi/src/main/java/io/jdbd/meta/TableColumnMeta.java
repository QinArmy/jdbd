package io.jdbd.meta;


import io.jdbd.lang.Nullable;
import io.jdbd.session.Option;
import io.jdbd.session.OptionSpec;

import java.util.Set;
import java.util.function.Function;

/**
 * <p>
 * This interface representing table column meta data.
 * </p>
 *
 * @see DatabaseMetaData#columnsOfTable(TableMeta, Function)
 * @since 1.0
 */
public interface TableColumnMeta extends OptionSpec {

    TableMeta tableMeta();

    String columnName();

    DataType dataType();

    int position();

    long precision();

    int scale();

    BooleanMode nullableMode();

    BooleanMode autoincrementMode();

    BooleanMode generatedMode();

    /**
     * @return 'null' representing default is null
     */
    @Nullable
    String defaultValue();

    @Nullable
    String comment();

    /**
     * @param elementClass the java class enum element
     * @param <E>          the java type enum element,typical java type is following:
     *                     <ul>
     *                        <li>{@link String}</li>
     *                        <li>{@link Enum}</li>
     *                     </ul>
     * @return empty set or enum element set.
     * @throws IllegalArgumentException throw when don't support elementClass.
     */
    <E> Set<E> enumElementSet(Class<E> elementClass);

    /**
     * <p>
     * The privilege set you have for the table,if driver don't support this method,then always empty.
     * </p>
     *
     * @return a unmodified set ,empty set or privilege set.
     */
    Set<String> privilegeSet();


    /**
     * <p>
     * The implementation of this method perhaps support some of following :
     *     <ul>
     *         <li>{@link Option#CHARSET}</li>
     *         <li>{@link Option#COLLATION}</li>
     *         <li>{@link Option#PRIVILEGE}</li>
     *     </ul>
     * </p>
     */
    @Override
    <T> T valueOf(Option<T> option);

    /**
     * override {@link Object#toString()}
     *
     * @return column info, contain : <ol>
     * <li>implementation class name</li>
     * <li>{@link SchemaMeta#catalog()}</li>
     * <li>{@link SchemaMeta#schema()}</li>
     * <li>{@link TableMeta#tableName()}</li>
     * <li>{@link #columnName()}</li>
     * <li>{@link #dataType()}</li>
     * <li>{@link #precision()}</li>
     * <li>{@link #scale()}</li>
     * <li>{@link #nullableMode()}</li>
     * <li>{@link #autoincrementMode()}</li>
     * <li>{@link #generatedMode()}</li>
     * <li>{@link #defaultValue()}</li>
     * <li>{@link #comment()}</li>
     * <li>{@link System#identityHashCode(Object)}</li>
     * </ol>
     */
    @Override
    String toString();


}
