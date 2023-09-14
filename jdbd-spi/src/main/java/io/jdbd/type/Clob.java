package io.jdbd.type;

import io.jdbd.meta.DataType;
import org.reactivestreams.Publisher;

/**
 * <p>
 * This interface representing the holder of {@link String} {@link Publisher}.
 * </p>
 * <p>
 * Application developer can get the instance of this interface by {@link #from(Publisher)} method.
 * </p>
 *
 * @see io.jdbd.statement.ParametrizedStatement#bind(int, DataType, Object)
 * @see io.jdbd.result.DataRow#get(int, Class)
 * @since 1.0
 */
public interface Clob extends PublisherParameter {

    /**
     * @return {@link String} {@link Publisher}
     */
    Publisher<String> value();

    /**
     * create {@link Blob} instance.
     */
    static Clob from(Publisher<String> source) {
        return JdbdTypes.clobParam(source);
    }

}
