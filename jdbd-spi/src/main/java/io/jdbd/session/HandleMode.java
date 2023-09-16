package io.jdbd.session;

/**
 * <p>
 * the handle mode to exists transaction when invoke {@link LocalDatabaseSession#startTransaction(TransactionOption, HandleMode)}.
 * </p>
 *
 * @see LocalDatabaseSession#startTransaction(TransactionOption, HandleMode)
 * @since 1.0
 */
public enum HandleMode {

    ERROR_IF_EXISTS,
    ROLLBACK_IF_EXISTS,
    COMMIT_IF_EXISTS


}
