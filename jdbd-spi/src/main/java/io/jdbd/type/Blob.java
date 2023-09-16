package io.jdbd.type;


import io.jdbd.meta.DataType;
import org.reactivestreams.Publisher;

/**
 * <p>
 * This interface representing the holder of bye[] {@link Publisher}.
 * <br/>
 * <p>
 * Application developer can get the instance of this interface by {@link #from(Publisher)} method.
 * <br/>
 *
 * @see io.jdbd.statement.ParametrizedStatement#bind(int, DataType, Object)
 * @see io.jdbd.result.DataRow#get(int, Class)
 * @since 1.0
 */
public interface Blob extends PublisherParameter {

    /**
     * @return bye[] {@link Publisher}
     */
    Publisher<byte[]> value();

    /**
     * create {@link Blob} instance.
     */
    static Blob from(Publisher<byte[]> source) {
        return JdbdTypes.blobParam(source);
    }

}
