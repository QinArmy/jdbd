package io.jdbd.type;


import io.jdbd.meta.DataType;
import io.jdbd.result.BigColumnValue;

import java.nio.file.Path;

/**
 * <p>
 * This interface representing the holder of binary file.
 * <br/>
 * <p>
 * Application developer can get the instance of this interface by {@link #from(boolean, Path)} method.
 * <br/>
 *
 * @see io.jdbd.statement.ParametrizedStatement#bind(int, DataType, Object)
 * @see io.jdbd.result.DataRow#get(int, Class)
 * @since 1.0
 */
public interface BlobPath extends PathParameter, BigColumnValue {

    /**
     * create {@link BlobPath} instance.
     *
     * @param deleteOnClose true : should delete after close, see {@link java.nio.file.StandardOpenOption#DELETE_ON_CLOSE}.
     */
    static BlobPath from(boolean deleteOnClose, Path path) {
        return JdbdTypes.blobPathParam(deleteOnClose, path);
    }


}
