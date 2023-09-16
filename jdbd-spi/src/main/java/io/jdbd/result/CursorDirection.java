package io.jdbd.result;

/**
 * <p> This enum representing cursor
 *
 * @see RefCursor
 */
public enum CursorDirection {

    NEXT,
    PRIOR,
    FIRST,
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
     * must specified count
     */
    FORWARD,
    FORWARD_ALL,

    /**
     * must specified count
     */
    BACKWARD,
    BACKWARD_ALL;


    @Override
    public final String toString() {
        return String.format("%s.%s", CursorDirection.class.getName(), this.name());
    }


}
