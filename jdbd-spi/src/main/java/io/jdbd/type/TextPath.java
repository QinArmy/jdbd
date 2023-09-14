package io.jdbd.type;


import io.jdbd.meta.DataType;
import io.jdbd.result.BigColumnValue;

import java.nio.charset.Charset;
import java.nio.file.Path;

/**
 * <p>
 * This interface representing the holder of text file.
 * </p>
 * <p>
 * Application developer can get the instance of this interface by {@link #from(boolean, Charset, Path)} method.
 * </p>
 * <p>
 * Application developer can create {@link java.io.BufferedReader} by {@link io.jdbd.util.JdbdUtils#newBufferedReader(TextPath)}
 * </p>
 *
 * @see io.jdbd.statement.ParametrizedStatement#bind(int, DataType, Object)
 * @see io.jdbd.result.DataRow#get(int, Class)
 * @since 1.0
 */
public interface TextPath extends PathParameter, BigColumnValue {

    /**
     * @return text file charset
     */
    Charset charset();

    /**
     * create {@link TextPath} instance.
     *
     * @param deleteOnClose true : should delete after close, see {@link java.nio.file.StandardOpenOption#DELETE_ON_CLOSE}.
     */
    static TextPath from(boolean deleteOnClose, Charset charset, Path path) {
        return JdbdTypes.textPathParam(deleteOnClose, charset, path);
    }

}
