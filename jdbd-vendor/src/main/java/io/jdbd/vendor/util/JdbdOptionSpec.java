package io.jdbd.vendor.util;

import io.jdbd.session.Option;
import io.jdbd.session.OptionSpec;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public abstract class JdbdOptionSpec implements OptionSpec {


    protected final Function<Option<?>, ?> optionFunc;

    protected final Set<Option<?>> optionSet;

    protected JdbdOptionSpec(Map<Option<?>, ?> optionMap) {
        if (optionMap.size() == 0) {
            this.optionFunc = Option.EMPTY_OPTION_FUNC;
            this.optionSet = Collections.emptySet();
        } else {
            this.optionFunc = optionMap::get;
            this.optionSet = Collections.unmodifiableSet(optionMap.keySet());
        }
    }


    @SuppressWarnings("unchecked")
    @Override
    public final <T> T valueOf(final Option<T> option) {
        final Object value;
        value = this.optionFunc.apply(option);
        if (option.javaType().isInstance(value)) {
            return (T) value;
        }
        return null;
    }

    @Override
    public final <T> T nonNullOf(Option<T> option) {
        return OptionSpec.super.nonNullOf(option);
    }

    @Override
    public final Set<Option<?>> optionSet() {
        return this.optionSet;
    }


}
