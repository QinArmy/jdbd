package io.jdbd.result;

/**
 * <p> This enum representing cursor
 *
 * @see RefCursor
 */
public enum CursorDirection {

    /**
     * Fetch the next row.
     */
    NEXT,

    /**
     * Fetch the prior row.
     */
    PRIOR,

    /**
     * Fetch the first row of the query (same as ABSOLUTE 1).
     */
    FIRST,

    /**
     * Fetch the last row of the query (same as ABSOLUTE -1).
     */
    LAST,

    /**
     * must specified count
     */
    ABSOLUTE,

    /**
     * must specified count
     */
    RELATIVE,

    /**
     * Fetch the next row (same as NEXT).
     * must specified count
     */
    FORWARD,

    /**
     * Fetch all remaining rows.
     */
    FORWARD_ALL,

    /**
     * must specified count
     */
    BACKWARD,

    /**
     * Fetch all prior rows (scanning backwards).
     */
    BACKWARD_ALL;


    @Override
    public final String toString() {
        return String.format("%s.%s", CursorDirection.class.getName(), this.name());
    }


}
