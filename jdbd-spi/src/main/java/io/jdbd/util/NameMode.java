package io.jdbd.util;

/**
 * <p>
 * This enum representing the handle mode to name text.
 * <br/>
 *
 * @see io.jdbd.session.DatabaseSession#appendTableName(String, NameMode, StringBuilder)
 * @see io.jdbd.session.DatabaseSession#appendColumnName(String, NameMode, StringBuilder)
 * @since 1.0
 */
public enum NameMode {

    LOWER_CASE,

    UPPER_CASE,

    DEFAULT


}
