package io.jdbd.meta;

/**
 * <p>
 * This enum representing key type
 * </p>
 *
 * @see TableIndexMeta
 * @since 1.0
 */
public enum KeyType {

    PRIMARY_KEY,
    UNIQUE_KEY,

    INDEX_KEY,

    FULL_TEXT_KEY,

    SPATIAL_KEY,

    NONE,

    UNKNOWN;

    public final boolean isUnique() {
        return this == PRIMARY_KEY || this == UNIQUE_KEY;
    }
}
