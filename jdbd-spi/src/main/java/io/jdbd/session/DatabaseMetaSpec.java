package io.jdbd.session;

import io.jdbd.Driver;
import io.jdbd.JdbdException;
import io.jdbd.meta.DatabaseMetaData;

/**
 * <p>
 * This interface is base interface of following :
 *     <ul>
 *         <li>{@link DatabaseSession}</li>
 *         <li>{@link DatabaseMetaData}</li>
 *     </ul>
 * </p>
 *
 * @since 1.0
 */
public interface DatabaseMetaSpec {

    /**
     * @throws JdbdException throw when session have closed.
     */
    ServerVersion serverVersion() throws JdbdException;

    /**
     * @return session factory vendor,The value returned typically is the package name for this vendor.
     * The session factory vendor possibly is pool vendor.
     */
    String factoryVendor();

    /**
     * @return driver vendor,The value returned typically is the package name for this vendor.
     * @see Driver#vendor()
     */
    String driverVendor();


    /**
     * @see java.sql.DatabaseMetaData#supportsSavepoints()
     */
    boolean isSupportSavePoints() throws JdbdException;

    /**
     * @throws JdbdException throw if session have closed
     */
    boolean isSupportStmtVar() throws JdbdException;

    /**
     * @throws JdbdException throw if session have closed
     */
    boolean isSupportMultiStatement() throws JdbdException;

    /**
     * @throws JdbdException throw if session have closed
     */
    boolean isSupportOutParameter() throws JdbdException;

    /**
     * @throws JdbdException throw if session have closed
     */
    boolean isSupportStoredProcedures() throws JdbdException;

    /**
     * @return true : support {@link io.jdbd.result.RefCursor}
     * @throws JdbdException throw if session have closed
     */
    boolean isSupportRefCursor() throws JdbdException;

    boolean iSupportLocalTransaction();


}
