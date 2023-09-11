package io.jdbd.vendor.meta;

import io.jdbd.meta.BooleanMode;
import io.jdbd.meta.DataType;
import io.jdbd.meta.TableColumnMeta;
import io.jdbd.meta.TableMeta;
import io.jdbd.session.Option;
import io.jdbd.vendor.util.JdbdStrings;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Set;
import java.util.function.Function;

public final class VendorTableColumnMeta implements TableColumnMeta {

    public static final Option<Integer> COLUMN_POSITION = Option.from("POSITION", Integer.class);

    public static final Option<Integer> COLUMN_SCALE = Option.from("SCALE", Integer.class);

    public static final Option<DataType> COLUMN_DATA_TYPE = Option.from("DATA TYPE", DataType.class);

    public static final Option<BooleanMode> COLUMN_NULLABLE_MODE = Option.from("NULLABLE MODE", BooleanMode.class);

    public static final Option<BooleanMode> COLUMN_AUTO_INCREMENT_MODE = Option.from("AUTO INCREMENT MODE", BooleanMode.class);

    public static final Option<BooleanMode> COLUMN_GENERATED_MODE = Option.from("GENERATED MODE", BooleanMode.class);

    public static final Option<String> COLUMN_DEFAULT = Option.from("DEFAULT VALUE", String.class);

    public static final Option<String> COLUMN_COMMENT = Option.from("COMMENT", String.class);


    /**
     * <p>
     * optionFunc must support following option:
     *     <ul>
     *         <li>{@link Option#NAME} column name,see {@link #columnName()}</li>
     *         <li>{@link #COLUMN_DATA_TYPE},see {@link #dataType()}</li>
     *         <li>{@link Option#PRECISION} column precision,see {@link #precision()}</li>
     *         <li>{@link #COLUMN_SCALE} column scale,see {@link #scale()}</li>
     *         <li>{@link #COLUMN_NULLABLE_MODE},see {@link #nullableMode()}</li>
     *         <li>{@link #COLUMN_AUTO_INCREMENT_MODE},see {@link #autoincrementMode()}</li>
     *         <li>{@link #COLUMN_GENERATED_MODE},see {@link #generatedMode()}</li>
     *         <li>{@link #COLUMN_DEFAULT},see {@link #defaultValue()}</li>
     *         <li>{@link #COLUMN_COMMENT},see {@link #comment()}</li>
     *     </ul>
     * </p>
     * <p>
     *      optionFunc optionally support following option:
     *      <ul>
     *          <li>{@link Option#PRIVILEGE},see {@link #privilegeSet()}</li>
     *          <li>{@link Option#CHARSET}</li>
     *          <li>{@link Option#COLLATION}</li>
     *      </ul>
     * </p>
     *
     * @param enumSetFunc the function must follow {@link TableColumnMeta#enumElementSet(Class)}.
     */
    public static VendorTableColumnMeta from(TableMeta tableMeta, Function<Class<?>, Set<?>> enumSetFunc,
                                             Function<Option<?>, ?> optionFunc) {
        return new VendorTableColumnMeta(tableMeta, enumSetFunc, optionFunc);
    }


    private final TableMeta tableMeta;

    private final Function<Class<?>, Set<?>> enumSetFunc;
    private final Function<Option<?>, ?> optionFunc;


    private VendorTableColumnMeta(TableMeta tableMeta, Function<Class<?>, Set<?>> enumSetFunc,
                                  Function<Option<?>, ?> optionFunc) {
        this.tableMeta = tableMeta;
        this.enumSetFunc = enumSetFunc;
        this.optionFunc = optionFunc;
    }

    @Override
    public TableMeta tableMeta() {
        return this.tableMeta;
    }

    @Override
    public String columnName() {
        return nonNullOf(Option.NAME);
    }

    @Override
    public DataType dataType() {
        return nonNullOf(COLUMN_DATA_TYPE);
    }

    @Override
    public int position() {
        return nonNullOf(COLUMN_POSITION);
    }

    @Override
    public long precision() {
        return nonNullOf(Option.PRECISION);
    }

    @Override
    public int scale() {
        return nonNullOf(COLUMN_SCALE);
    }


    @Override
    public BooleanMode nullableMode() {
        return nonNullOf(COLUMN_NULLABLE_MODE);
    }

    @Override
    public BooleanMode autoincrementMode() {
        return nonNullOf(COLUMN_AUTO_INCREMENT_MODE);
    }

    @Override
    public BooleanMode generatedMode() {
        return nonNullOf(COLUMN_GENERATED_MODE);
    }

    @Override
    public String defaultValue() {
        return nonNullOf(COLUMN_DEFAULT);
    }

    @Override
    public String comment() {
        return nonNullOf(COLUMN_COMMENT);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E> Set<E> enumElementSet(Class<E> elementClass) {
        return (Set<E>) this.enumSetFunc.apply(elementClass);
    }

    @Override
    public Set<String> privilegeSet() {
        final String privilegeString;
        privilegeString = valueOf(Option.PRIVILEGE);
        if (privilegeString == null) {
            return Collections.emptySet();
        }
        return JdbdStrings.spitAsSet(privilegeString, ",", true);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T valueOf(Option<T> option) {
        return (T) this.optionFunc.apply(option);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder(256);
        builder.append(getClass().getName())
                .append("[ catalog : '")
                .append(this.tableMeta.schemaMeta().catalog())
                .append("' , schema : '")
                .append(this.tableMeta.schemaMeta().schema())
                .append("' , tableName : '")
                .append(this.tableMeta.tableName())
                .append("' , columnName : '")
                .append(this.columnName())
                .append("' , dataType : '")
                .append(this.dataType().typeName())
                .append("' , precision : ")
                .append(this.precision())
                .append(" , scale : ")
                .append(this.scale())
                .append(" , nullableMode : ")
                .append(this.nullableMode())
                .append(" , autoincrementMode : ")
                .append(this.autoincrementMode())
                .append(" , generatedMode : ")
                .append(this.generatedMode())
                .append(" , defaultValue : '")
                .append(this.defaultValue())
                .append("' , comment : '")
                .append(this.comment())
                .append('\'');

        Object optionValue;
        optionValue = this.optionFunc.apply(Option.CHARSET);
        if (optionValue instanceof Charset) {
            builder.append(" , charset : ")
                    .append(((Charset) optionValue).name());
        }

        optionValue = this.optionFunc.apply(Option.COLLATION);
        if (optionValue instanceof String) {
            builder.append(" , collation : '")
                    .append(optionValue)
                    .append('\'');
        }

        optionValue = this.optionFunc.apply(Option.PRIVILEGE);
        if (optionValue instanceof String) {
            builder.append(" , privilege : '")
                    .append(optionValue)
                    .append('\'');
        }


        return builder.append(" , hash : ")
                .append(System.identityHashCode(this))
                .append(" ]")
                .toString();
    }


}
