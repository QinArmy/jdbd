/*
 * Copyright 2023-2043 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.jdbd.session;

import io.jdbd.Driver;
import io.jdbd.JdbdException;
import io.jdbd.meta.DataType;
import io.jdbd.meta.DatabaseMetaData;
import io.jdbd.meta.ServerMode;
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
     *
     * @return driver vendor,The value returned typically is the package name for this vendor.
     * @see Driver#vendor()
     */
    String driverVendor();


    /**
     * The session whether support save points or not.
     *
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

    /**
     * Get server mode
     *
     * @throws JdbdException throw when need session is open and session have closed
     */
    ServerMode serverMode() throws JdbdException;


    /**
     * <p>Some database product support other database product syntax.
     * <p>For example : OceanBase support Oracle and MySQL syntax.
     * so {@link DatabaseMetaData#productName()} return OceanBase and this method return Oracle or MySQL .
     *
     * @return <ul>
     * <li>the database product that support itself syntax return same value with {@link DatabaseMetaData#productFamily()}</li>
     * <li>else return the database product family that database support , for example : OceanBase return Oracle or MySQL</li>
     * </ul>
     */
    String supportProductFamily();

}
