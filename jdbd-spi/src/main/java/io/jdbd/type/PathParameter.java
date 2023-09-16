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
     * @see java.nio.file.StandardOpenOption#DELETE_ON_CLOSE
     */
    boolean isDeleteOnClose();


    @Override
    Path value();


}
