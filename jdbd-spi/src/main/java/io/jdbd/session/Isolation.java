package io.jdbd.session;


import io.jdbd.util.JdbdUtils;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

public final class Isolation {

    public static Isolation from(final String value) {
        if (JdbdUtils.hasNoText(value)) {
            throw new IllegalArgumentException("no text");
        }
        return INSTANCE_MAP.computeIfAbsent(value, CONSTRUCTOR);
    }

    private static final Function<String, Isolation> CONSTRUCTOR = Isolation::new;

    private static final ConcurrentMap<String, Isolation> INSTANCE_MAP = JdbdUtils.concurrentHashMap((int) (4 / 0.75f));


    /**
     * A constant indicating that dirty reads, non-repeatable reads and phantom reads
     * can occur. This level allows a row changed by one transaction to be read by
     * another transaction before any changes in that row have been committed
     * (a "dirty read"). If any of the changes are rolled back, the second
     * transaction will have retrieved an invalid row.
     */
    public static final Isolation READ_UNCOMMITTED = from("READ UNCOMMITTED");

    /**
     * A constant indicating that dirty reads are prevented; non-repeatable reads
     * and phantom reads can occur. This level only prohibits a transaction
     * from reading a row with uncommitted changes in it.
     */
    public static final Isolation READ_COMMITTED = from("READ COMMITTED");

    /**
     * A constant indicating that dirty reads and non-repeatable reads are
     * prevented; phantom reads can occur. This level prohibits a transaction
     * from reading a row with uncommitted changes in it, and it also prohibits
     * the situation where one transaction reads a row, a second transaction
     * alters the row, and the first transaction rereads the row, getting
     * different values the second time (a "non-repeatable read").
     */
    public static final Isolation REPEATABLE_READ = from("REPEATABLE READ");

    /**
     * A constant indicating that dirty reads, non-repeatable reads and phantom
     * reads are prevented. This level includes the prohibitions in
     * {@code ISOLATION_REPEATABLE_READ} and further prohibits the situation
     * where one transaction reads all rows that satisfy a {@code WHERE}
     * condition, a second transaction inserts a row that satisfies that
     * {@code WHERE} condition, and the first transaction rereads for the
     * same condition, retrieving the additional "phantom" row in the second read.
     */
    public static final Isolation SERIALIZABLE = from("SERIALIZABLE");


    private final String name;

    /**
     * private constructor
     */
    private Isolation(String name) {
        this.name = name;
    }

    public String name() {
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
        } else if (obj instanceof Isolation) {
            match = ((Isolation) obj).name.equals(this.name);
        } else {
            match = false;
        }
        return match;
    }

    @Override
    public String toString() {
        return String.format("%s[ name : %s , hash : %s]",
                Isolation.class.getName(),
                this.name,
                System.identityHashCode(this)
        );
    }


    /*-------------------below private method -------------------*/

    private void readObject(ObjectInputStream in) throws IOException {
        throw new InvalidObjectException("can't deserialize Isolation");
    }

    private void readObjectNoData() throws ObjectStreamException {
        throw new InvalidObjectException("can't deserialize Isolation");
    }


}
