package io.jdbd.result;

import io.jdbd.lang.NonNull;
import io.jdbd.type.BlobPath;

import java.nio.file.Path;

/**
 * This interface is base interface of following:
 *     <ul>
 *         <li>{@link BlobPath}</li>
 *         <li>{@link io.jdbd.type.TextPath}</li>
 *     </ul>
 * <br/>
 *
 * @since 1.0
 */
public interface BigColumnValue {

    /**
     * should delete after close
     *
     * @return true : should delete after close
     * @see java.nio.file.StandardOpenOption#DELETE_ON_CLOSE
     */
    boolean isDeleteOnClose();

    /**
     * file path
     *
     * @return file path
     */
    @NonNull
    Path value();


}
