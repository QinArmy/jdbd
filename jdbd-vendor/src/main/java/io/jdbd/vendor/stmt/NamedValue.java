package io.jdbd.vendor.stmt;

/**
 * <p>
 * This interface extends {@link Value},representing a named value that is bound to sql.
 * Currently jdbd don't support named param,but this interface can be extended implementation
 * jdbd spi for special feature ,for example: MySQL query attribute.
 * <br/>
 */
public interface NamedValue extends Value {

    String getName();

}
