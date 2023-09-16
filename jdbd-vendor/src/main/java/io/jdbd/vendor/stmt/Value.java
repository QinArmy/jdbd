package io.jdbd.vendor.stmt;


import io.jdbd.lang.Nullable;
import io.jdbd.meta.DataType;
import io.jdbd.statement.Parameter;

/**
 * <p>
 * This interface representing a value that is bound to sql.
 * This is a base interface of below interface:
 * <p>
 * <UL>
 * <li>{@link ParamValue}</li>
 * <li>{@link NamedValue }</li>
 * </UL>
 * <br/>
 * <br/>
 *
 * @see ParamValue
 * @see NamedValue
 */
public interface Value {

    /**
     * Get parameter value
     *
     * @return <ul>
     * <li>null</li>
     * <li>non-{@link Parameter} instance</li>
     * <li>{@link Parameter instance}</li>
     * </ul>
     */
    @Nullable
    Object get();

    /**
     * Get parameter value,throw {@link NullPointerException} if value is null
     * @return <ul>
     * <li>non-{@link Parameter} instance</li>
     * <li>{@link Parameter instance}</li>
     * </ul>
     * @throws NullPointerException throw when param is null
     */
    Object getNonNull() throws NullPointerException;

    /**
     * Get parameter type
     * @return sql type of bind value.
     */
    DataType getType();

}
