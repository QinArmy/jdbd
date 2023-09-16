package io.jdbd.vendor.meta;

import io.jdbd.meta.BooleanMode;
import io.jdbd.meta.IndexColumnMeta;
import io.jdbd.meta.NullsSorting;
import io.jdbd.meta.Sorting;
import io.jdbd.session.Option;
import io.jdbd.vendor.VendorOptions;
import io.jdbd.vendor.util.JdbdStrings;

import java.util.function.Function;

public final class VendorIndexColumnMeta implements IndexColumnMeta {


    /**
     * <p>
     * optionFunc must support following option:
     *     <ul>
     *         <li>{@link VendorOptions#CARDINALITY},see {@link #cardinality()}</li>
     *         <li>{@link VendorOptions#SORTING},see {@link #sorting()}</li>
     *         <li>{@link VendorOptions#NULLS_SORTING},see {@link #nullsSorting()}</li>
     *         <li>{@link VendorTableColumnMeta#nullableMode()},see {@link #nullableMode()}</li>
     *         <li>{@link VendorOptions#VISIBLE},see {@link #visibleMode()}</li>
     *     </ul>
     * <br/>
     *
     * @param name column name
     */
    public static VendorIndexColumnMeta from(String name, Function<Option<?>, ?> optionFunc) {
        return new VendorIndexColumnMeta(name, optionFunc);
    }

    private final String name;

    private final Function<Option<?>, ?> optionFunc;

    private VendorIndexColumnMeta(String name, Function<Option<?>, ?> optionFunc) {
        this.name = name;
        this.optionFunc = optionFunc;
    }

    @Override
    public String columnName() {
        return this.name;
    }

    @Override
    public long cardinality() {
        return nonNullOf(VendorOptions.CARDINALITY);
    }

    @Override
    public Sorting sorting() {
        return nonNullOf(VendorOptions.SORTING);
    }

    @Override
    public NullsSorting nullsSorting() {
        return nonNullOf(VendorOptions.NULLS_SORTING);
    }

    @Override
    public BooleanMode nullableMode() {
        return nonNullOf(VendorOptions.NULLABLE_MODE);
    }

    @Override
    public BooleanMode visibleMode() {
        return nonNullOf(VendorOptions.VISIBLE);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T valueOf(Option<T> option) {
        final Object value;
        if (option == Option.NAME) {
            value = this.name;
        } else {
            value = this.optionFunc.apply(option);
        }
        return (T) value;
    }

    @Override
    public String toString() {
        return JdbdStrings.builder(220)
                .append(getClass().getName())
                .append("[ columnName : '")
                .append(this.name)
                .append("' , cardinality ")
                .append(cardinality())
                .append(" , sorting : ")
                .append(sorting())
                .append(" , nullsSorting : ")
                .append(nullsSorting())
                .append(" , nullableMode : ")
                .append(nullableMode())
                .append(" , visibleMode : ")
                .append(visibleMode())
                .append(" , hash : ")
                .append(System.identityHashCode(this))
                .append(" ]")
                .toString();
    }


}
