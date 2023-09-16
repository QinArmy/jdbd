package io.jdbd.vendor.meta;

import io.jdbd.meta.BooleanMode;
import io.jdbd.meta.DataType;
import io.jdbd.meta.TableColumnMeta;
import io.jdbd.meta.TableMeta;
import io.jdbd.session.Option;
import io.jdbd.vendor.VendorOptions;
import io.jdbd.vendor.util.JdbdStrings;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Set;
import java.util.function.Function;

public final class VendorTableColumnMeta implements TableColumnMeta {

    public static final Function<Class<?>, Set<?>> EMPTY_ENUMS_FUNC = clazz -> Collections.emptySet();


    /**
     * <p>
     * optionFunc must support following option:
     *     <ul>
     *         <li>{@link Option#NAME} column name,see {@link #columnName()}</li>
     *         <li>{@link VendorOptions#DATA_TYPE},see {@link #dataType()}</li>
     *         <li>{@link Option#PRECISION} column precision,see {@link #precision()}</li>
     *         <li>{@link VendorOptions#SCALE} column scale,see {@link #scale()}</li>
     *         <li>{@link VendorOptions#NULLABLE_MODE},see {@link #nullableMode()}</li>
     *         <li>{@link VendorOptions#AUTO_INCREMENT_MODE},see {@link #autoincrementMode()}</li>
     *         <li>{@link VendorOptions#GENERATED_MODE},see {@link #generatedMode()}</li>
     *         <li>{@link VendorOptions#DEFAULT_VALUE},see {@link #defaultValue()}</li>
     *         <li>{@link VendorOptions#COMMENT},see {@link #comment()}</li>
     *     </ul>
     *<br/>
     * <p>
     *      optionFunc optionally support following option:
     *      <ul>
     *          <li>{@link Option#PRIVILEGE},see {@link #privilegeSet()}</li>
     *          <li>{@link Option#CHARSET}</li>
     *          <li>{@link Option#COLLATION}</li>
     *      </ul>
     *<br/>
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
        return nonNullOf(VendorOptions.DATA_TYPE);
    }

    @Override
    public int position() {
        return nonNullOf(VendorOptions.POSITION);
    }

    @Override
    public long precision() {
        return nonNullOf(Option.PRECISION);
    }

    @Override
    public int scale() {
        return nonNullOf(VendorOptions.SCALE);
    }


    @Override
    public BooleanMode nullableMode() {
        return nonNullOf(VendorOptions.NULLABLE_MODE);
    }

    @Override
    public BooleanMode autoincrementMode() {
        return nonNullOf(VendorOptions.AUTO_INCREMENT_MODE);
    }

    @Override
    public BooleanMode generatedMode() {
        return nonNullOf(VendorOptions.GENERATED_MODE);
    }

    @Override
    public String defaultValue() {
        return valueOf(VendorOptions.DEFAULT_VALUE);
    }

    @Override
    public String comment() {
        return valueOf(VendorOptions.COMMENT);
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
                .append(" , defaultValue : ");

        String textValue;

        textValue = defaultValue();
        if (textValue != null) {
            builder.append('\'');
        }
        builder.append(textValue);
        if (textValue != null) {
            builder.append('\'');
        }

        builder.append(" , comment : ");
        textValue = comment();
        if (textValue != null) {
            builder.append('\'');
        }
        builder.append(textValue);
        if (textValue != null) {
            builder.append('\'');
        }

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

        final Set<String> enumSet;
        enumSet = enumElementSet(String.class);
        if (enumSet.size() > 0) {
            builder.append(" , enumElementSet : [");
            int index = 0;
            for (String s : enumSet) {
                if (index > 0) {
                    builder.append(',');
                }
                builder.append('"')
                        .append(s)
                        .append('"');
                index++;
            }
            builder.append(']');
        }


        return builder.append(" , hash : ")
                .append(System.identityHashCode(this))
                .append(" ]")
                .toString();
    }


}
