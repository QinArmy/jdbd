package io.jdbd.vendor;

import io.jdbd.meta.*;
import io.jdbd.session.Option;

public abstract class VendorOptions {

    private VendorOptions() {
        throw new UnsupportedOperationException();
    }


    public static final Option<Integer> POSITION = Option.from("POSITION", Integer.class);
    public static final Option<Integer> SCALE = Option.from("SCALE", Integer.class);
    public static final Option<DataType> DATA_TYPE = Option.from("DATA TYPE", DataType.class);
    public static final Option<BooleanMode> NULLABLE_MODE = Option.from("NULLABLE MODE", BooleanMode.class);
    public static final Option<BooleanMode> AUTO_INCREMENT_MODE = Option.from("AUTO INCREMENT MODE", BooleanMode.class);
    public static final Option<BooleanMode> GENERATED_MODE = Option.from("GENERATED MODE", BooleanMode.class);
    public static final Option<String> DEFAULT_VALUE = Option.from("DEFAULT VALUE", String.class);
    public static final Option<String> COMMENT = Option.from("COMMENT", String.class);


    public static final Option<Long> CARDINALITY = Option.from("CARDINALITY", Long.class);
    public static final Option<Sorting> SORTING = Option.from("SORTING", Sorting.class);
    public static final Option<NullsSorting> NULLS_SORTING = Option.from("NULLS SORTING", NullsSorting.class);
    public static final Option<BooleanMode> VISIBLE = Option.from("VISIBLE", BooleanMode.class);

    public static final Option<String> INDEX_TYPE = Option.from("INDEX TYPE", String.class);

    public static final Option<KeyType> KEY_TYPE = Option.from("KEY TYPE", KeyType.class);


}
