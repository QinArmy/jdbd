package io.jdbd.session;

import io.jdbd.Driver;
import io.jdbd.JdbdException;
import io.jdbd.meta.DataType;
import io.jdbd.meta.DatabaseMetaData;
import io.jdbd.result.Cursor;

import java.util.function.Function;

/**
 * <p>
 * This interface is base interface of following :
 *     <ul>
 *         <li>{@link DatabaseSession}</li>
 *         <li>{@link DatabaseMetaData}</li>
 *     </ul>
 * <br/>
 *
 * @since 1.0
 */
public interface DatabaseMetaSpec {

    /**
     * server version
     *
     * @return server version
     * @throws JdbdException throw when session have closed
     */
    ServerVersion serverVersion() throws JdbdException;

    /**
     * factory vendor name
     *
     * @return session factory vendor,The value returned typically is the package name for this vendor.
     * The session factory vendor possibly is pool vendor.
     */
    String factoryVendor();

    /**
     * driver vendor name
     * @return driver vendor,The value returned typically is the package name for this vendor.
     * @see Driver#vendor()
     */
    String driverVendor();


    /**
     * The session whether support save points or not.
     * @return true : support save points
     * @throws JdbdException throw when need session is open and session have closed
     * @see java.sql.DatabaseMetaData#supportsSavepoints()
     */
    boolean isSupportSavePoints() throws JdbdException;

    /**
     * The session whether support {@link io.jdbd.statement.Statement#bindStmtVar(String, DataType, Object)} or not.
     *
     * @return true : support
     * @throws JdbdException throw when need session is open and session have closed
     */
    boolean isSupportStmtVar() throws JdbdException;

    /**
     * The session whether support multi-statement or not.
     *
     * @return true : support
     * @throws JdbdException throw when need session is open and session have closed
     */
    boolean isSupportMultiStatement() throws JdbdException;

    /**
     * The session whether support {@link io.jdbd.statement.OutParameter} or not.
     *
     * @return true : support
     * @throws JdbdException throw when need session is open and session have closed
     */
    boolean isSupportOutParameter() throws JdbdException;

    /**
     * The session whether support procedure or not.
     *
     * @return true : support
     * @throws JdbdException throw when need session is open and session have closed
     */
    boolean isSupportStoredProcedures() throws JdbdException;

    /**
     * The session whether support cursor or not.
     *
     * @return true : support {@link Cursor}
     * @throws JdbdException throw when need session is open and session have closed
     */
    boolean isSupportRefCursor() throws JdbdException;

    /**
     * The session whether support local transaction or not.
     *
     * @return true : support
     * @throws JdbdException throw when need session is open and session have closed
     */
    boolean iSupportLocalTransaction() throws JdbdException;

    /**
     * The session whether support {@link io.jdbd.statement.Statement#setImportPublisher(Function)} or not.
     *
     * @return true : support
     * @throws JdbdException throw when need session is open and session have closed
     */
    boolean isSupportImportPublisher() throws JdbdException;

    /**
     * The session whether support {@link io.jdbd.statement.Statement#setExportSubscriber(Function)} or not.
     *
     * @return true : support
     * @throws JdbdException throw when need session is open and session have closed
     */
    boolean isSupportExportSubscriber() throws JdbdException;


}
