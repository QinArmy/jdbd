package io.jdbd.pool;

import io.jdbd.session.DatabaseSessionFactory;
import io.jdbd.session.Option;

import java.util.function.Function;


/**
 * <p>This interface representing routing {@link DatabaseSessionFactory} for Read/Write Splitting.
 *
 * @since 1.0
 */
public interface ReadWriteSplittingFactory extends DatabaseSessionFactory {


    /**
     * <p>Select one writable DataSource
     * <p>The implementation of this method perhaps support some of following :
     * <ul>
     *     <li>{@link Option#TIMEOUT_MILLIS} : transaction timeout</li>
     *     <li>{@link Option#LOCK_TIMEOUT_MILLIS : max lock time}</li>
     * </ul>
     * above options can help application developer to select optimal DataSource.
     * <p>Above options is passed by application developer
     *
     * @param func option function, default {@link Option#EMPTY_OPTION_FUNC}.If don't support then ignore.
     * @return a DataSource that can support read/write
     */
    DatabaseSessionFactory readWriteFactory(Function<Option<?>, ?> func);


    /**
     * <p>Select one readonly DataSource
     * <p>The implementation of this method perhaps support some of following :
     * <ul>
     *     <li>{@link Option#TIMEOUT_MILLIS} : transaction timeout</li>
     *     <li>{@link Option#LOCK_TIMEOUT_MILLIS : max lock time}</li>
     * </ul>
     * above options can help application to select optimal DataSource.
     * <p>Above options is passed by application developer
     *
     * @param func option function, default {@link Option#EMPTY_OPTION_FUNC}.If don't support then ignore.
     * @return a readonly DataSource
     */
    DatabaseSessionFactory readOnlyFactory(final Function<Option<?>, ?> func);


}
