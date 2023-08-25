package io.jdbd.statement;

import io.jdbd.type.*;

/**
 * <p>
 * This interface representing some special parameter.
 * This interface is base interface of following :
 *     <ul>
 *         <li>{@link Blob}</li>
 *         <li>{@link Clob}</li>
 *         <li>{@link Text}</li>
 *         <li>{@link BlobPath}</li>
 *         <li>{@link TextPath}</li>
 *     </ul>
 * </p>
 *
 * @since 1.0
 */
public interface ValueParameter extends Parameter {


    /**
     * @return parameter value.
     */
    Object value();

}
