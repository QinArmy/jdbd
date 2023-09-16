package io.jdbd.meta;

/**
 * This enum is alternative of {@link Boolean} to avoid {@link NullPointerException}
 *
 * @since 1.p
 */
public enum BooleanMode {

    /**
     * see {@link Boolean#TRUE}
     */
    TRUE,

    /**
     * see {@link Boolean#FALSE}
     */
    FALSE,

    /**
     * representing unknown
     */
    UNKNOWN
}
