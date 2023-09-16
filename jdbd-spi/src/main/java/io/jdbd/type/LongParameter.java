package io.jdbd.type;

import io.jdbd.statement.Parameter;

/**
 * <p>
 * This interface is base interface of following :
 *     <ul>
 *         <li>{@link PublisherParameter}</li>
 *         <li>{@link PathParameter}</li>
 *     </ul>
 * <br/>
 *
 * @since 1.0
 */
public interface LongParameter extends Parameter {


    Object value();
}
