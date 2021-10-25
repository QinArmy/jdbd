package io.jdbd.session;

import java.sql.Connection;

public enum Isolation {

    /**
     * A constant indicating that dirty reads, non-repeatable reads and phantom reads
     * can occur. This level allows a row changed by one transaction to be read by
     * another transaction before any changes in that row have been committed
     * (a "dirty read"). If any of the changes are rolled back, the second
     * transaction will have retrieved an invalid row.
     *
     * @see java.sql.Connection#TRANSACTION_READ_UNCOMMITTED
     */
    READ_UNCOMMITTED(Connection.TRANSACTION_READ_UNCOMMITTED, "READ UNCOMMITTED"),

    /**
     * A constant indicating that dirty reads are prevented; non-repeatable reads
     * and phantom reads can occur. This level only prohibits a transaction
     * from reading a row with uncommitted changes in it.
     *
     * @see java.sql.Connection#TRANSACTION_READ_COMMITTED
     */
    READ_COMMITTED(Connection.TRANSACTION_READ_COMMITTED, "READ COMMITTED"),

    /**
     * A constant indicating that dirty reads and non-repeatable reads are
     * prevented; phantom reads can occur. This level prohibits a transaction
     * from reading a row with uncommitted changes in it, and it also prohibits
     * the situation where one transaction reads a row, a second transaction
     * alters the row, and the first transaction rereads the row, getting
     * different values the second time (a "non-repeatable read").
     *
     * @see java.sql.Connection#TRANSACTION_REPEATABLE_READ
     */
    REPEATABLE_READ(Connection.TRANSACTION_REPEATABLE_READ, "REPEATABLE READ"),

    /**
     * A constant indicating that dirty reads, non-repeatable reads and phantom
     * reads are prevented. This level includes the prohibitions in
     * {@code ISOLATION_REPEATABLE_READ} and further prohibits the situation
     * where one transaction reads all rows that satisfy a {@code WHERE}
     * condition, a second transaction inserts a row that satisfies that
     * {@code WHERE} condition, and the first transaction rereads for the
     * same condition, retrieving the additional "phantom" row in the second read.
     *
     * @see java.sql.Connection#TRANSACTION_SERIALIZABLE
     */
    SERIALIZABLE(Connection.TRANSACTION_SERIALIZABLE, "SERIALIZABLE");


    public final int level;

    public final String command;

    Isolation(int level, String command) {
        this.level = level;
        this.command = command;
    }

    /**
     * for method reference
     */
    public int getLevel() {
        return this.level;
    }

    /**
     * for method reference
     */
    public String getCommand() {
        return this.command;
    }


}
