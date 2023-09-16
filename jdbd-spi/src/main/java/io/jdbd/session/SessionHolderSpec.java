package io.jdbd.session;

/**
 * <p>
 * This interface is base interface of following:
 *     <ul>
 *         <li>{@link io.jdbd.meta.DatabaseMetaData}</li>
 *         <li>{@link io.jdbd.statement.Statement}</li>
 *     </ul>
 * <br/>
 *
 * @since 1.0
 */
public interface SessionHolderSpec {

    /**
     * @return the {@link DatabaseSession} that create this statement instance.
     */
    DatabaseSession getSession();

    /**
     * @return the {@link DatabaseSession} that create this statement instance.
     * @throws ClassCastException throw when cast error
     */
    <T extends DatabaseSession> T getSession(Class<T> sessionClass);
}
