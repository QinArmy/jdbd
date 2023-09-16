package io.jdbd.type;

import java.nio.file.Path;

/**
 * <p>
 * This interface is only base interface of following :
 *     <ul>
 *         <li>{@link BlobPath}</li>
 *         <li>{@link TextPath}</li>
 *     </ul>
 * <br/>
 *
 * @since 1.0
 */
public interface PathParameter extends LongParameter {

    /**
     * Whether delete file or not when close.
     *
     * @return true : delete file  when close.
     * @see java.nio.file.StandardOpenOption#DELETE_ON_CLOSE
     */
    boolean isDeleteOnClose();

    /**
     * file path
     *
     * @return file path
     */
    @Override
    Path value();


}
