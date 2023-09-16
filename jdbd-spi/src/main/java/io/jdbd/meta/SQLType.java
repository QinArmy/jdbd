package io.jdbd.meta;

import io.jdbd.lang.Nullable;

/**
 * <p>
 * This interface representing driver have known database build-in / internal-use data type.
 * <br/>
 *
 * @since 1.0
 */
public interface SQLType extends DataType {

    JdbdType jdbdType();


    Class<?> firstJavaType();

    @Nullable
    Class<?> secondJavaType();


    /**
     * <p>
     * For example:
     *    <ul>
     *        <li>one dimension BIGINT_ARRAY return BIGINT</li>
     *        <li>tow dimension BIGINT_ARRAY return BIGINT too</li>
     *    </ul>
     *<br/>
     *
     * @return element type of array(1-n dimension)
     */
    @Nullable
    SQLType elementType();

    String vendor();


}
