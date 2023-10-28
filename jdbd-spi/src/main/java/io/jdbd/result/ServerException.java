package io.jdbd.result;

import io.jdbd.JdbdException;
import io.jdbd.lang.Nullable;
import io.jdbd.session.Option;
import io.jdbd.session.OptionSpec;

import java.util.function.Function;


/**
 * <p>
 * Emit(not throw), when server return error message.
 * <br/>
 *
 * @since 1.0
 */
public abstract class ServerException extends JdbdException implements OptionSpec {

    private final Function<Option<?>, ?> optionFunc;


    protected ServerException(String message, @Nullable String sqlState, int vendorCode,
                              Function<Option<?>, ?> optionFunc) {
        super(message, sqlState, vendorCode);
        this.optionFunc = optionFunc;
    }

    @SuppressWarnings("unchecked")
    @Override
    public final <T> T valueOf(Option<T> option) {
        final Object value;
        value = this.optionFunc.apply(option);
        if (option.javaType().isInstance(value)) {
            return (T) value;
        }
        return null;
    }


}
