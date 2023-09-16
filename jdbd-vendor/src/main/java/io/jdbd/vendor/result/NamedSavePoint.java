package io.jdbd.vendor.result;

import io.jdbd.JdbdException;
import io.jdbd.session.SavePoint;
import io.jdbd.vendor.util.JdbdStrings;

/**
 * named {@link SavePoint}
 *
 * @see UnNamedSavePoint
 * @since 1.0
 */
public final class NamedSavePoint implements SavePoint {

    public static SavePoint fromName(String name) {
        return new NamedSavePoint(name);
    }

    private final String name;

    private NamedSavePoint(String name) {
        this.name = name;
    }

    @Override
    public boolean isNamed() {
        return true;
    }

    @Override
    public boolean isIdType() {
        return false;
    }

    @Override
    public int id() throws JdbdException {
        throw new JdbdException("this is named save point");
    }

    @Override
    public String name() throws JdbdException {
        return this.name;
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        final boolean match;
        if (obj == this) {
            match = true;
        } else if (obj instanceof NamedSavePoint) {
            match = ((NamedSavePoint) obj).name.equals(this.name);
        } else {
            match = false;
        }
        return match;
    }

    @Override
    public String toString() {
        return JdbdStrings.builder()
                .append(NamedSavePoint.class.getName())
                .append("[ name : ")
                .append(this.name)
                .append(" , hash : ")
                .append(System.identityHashCode(this))
                .append(" ]")
                .toString();
    }


}//NamedSavePoint
