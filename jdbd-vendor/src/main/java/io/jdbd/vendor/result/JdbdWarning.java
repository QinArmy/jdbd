package io.jdbd.vendor.result;

import io.jdbd.result.Warning;
import io.jdbd.session.Option;
import io.jdbd.vendor.util.JdbdCollections;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

public final class JdbdWarning implements Warning {

    private static final JdbdWarning EMPTY = new JdbdWarning("", Collections.emptyMap());

    public static JdbdWarning create(String message, Map<Option<?>, ?> optionMap) {
        Objects.requireNonNull(message, "message");
        final JdbdWarning warning;
        if (message.equals("")) {
            warning = EMPTY;
        } else {
            warning = new JdbdWarning(message, optionMap);
        }
        return warning;
    }


    private final String message;

    private final Map<Option<?>, ?> optionMap;

    private JdbdWarning(String message, Map<Option<?>, ?> optionMap) {
        this.message = message;
        this.optionMap = JdbdCollections.unmodifiableMap(optionMap); // just wrap
    }


    @Override
    public String message() {
        return this.message;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T valueOf(Option<T> option) {
        return (T) this.optionMap.get(option);
    }


}
