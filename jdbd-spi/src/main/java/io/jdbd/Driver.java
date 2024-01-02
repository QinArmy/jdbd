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

package io.jdbd;

import io.jdbd.pool.PoolRmDatabaseSession;
import io.jdbd.session.DatabaseSessionFactory;

import java.nio.file.Path;
import java.util.Map;


/**
 * <p>
 * This interface representing database driver. This interface is implemented by database vendor.
 * The implementation of this interface must provide public static factory method, like following :
 * <pre><br/>
 *   public static Driver getInstance() {
 *       return INSTANCE;
 *   }
 *  </pre>
 * <br/>
 * <p>
 * For example, suppose the service provider
 * {@code io.jdbd.mysql.MySQLDriver} is packaged in a JAR file for the
 * class path. The JAR file will contain a provider-configuration file named:
 *
 * <blockquote>{@code META-INF/jdbd/io.jdbd.Driver}</blockquote>
 * <p>
 * that contains the line:
 *
 * <blockquote>{@code io.jdbd.mysql.MySQLDriver }</blockquote>
 * <br/>
 * <p>
 * Application/pool developer can get the instance of {@link Driver} by {@link #findDriver(String)}
 * <br/>
 *
 * @since 1.0
 */
public interface Driver {

    /**
     * <p>
     * Required , jdbd always support
     * <br/>
     *
     * @see io.jdbd.session.Option#USER
     */
    String USER = "user";

    /**
     * <p>
     * Required , jdbd always support
     * <br/>
     */
    String PASSWORD = "password";

    /**
     * <p>
     * Required , jdbd always support, if application developer don't put this property,then {@link DatabaseSessionFactory#name()} always return 'unnamed' .
     * <br/>
     */
    String FACTORY_NAME = "factoryName";

    String PREPARE_THRESHOLD = "prepareThreshold";


    String CLIENT_INFO = "clientInfo";


    /**
     * <p>driver whether accept url or not.
     *
     * @param url jdbc url
     * @return true: accept
     * @throws NullPointerException when url is null
     */
    boolean acceptsUrl(String url);


    /**
     * <p>
     * Create {@link DatabaseSessionFactory} for application developer.The factory don't create pool {@link io.jdbd.session.DatabaseSession}.
     * Because driver developers are not responsible for pooling.
     * <br/>
     * <p>
     * Pool vendor developer should use {@link #forPoolVendor(String, Map)} create {@link DatabaseSessionFactory}.
     * <br/>
     *
     * @param url        format :jdbd:protocol:[subProtocol:]//[hostList]/[databaseName][;attributes][?properties] . For example:
     *                   <ul>
     *                     <li>jdbd:mysql://localhost:3306/army_test?sslMode=require</li>
     *                     <li>jdbd:postgresql://localhost:5432/army_test?sslMode=require</li>
     *                   </ul>
     * @param properties properties map ,this map can override the attributes and properties in url. application developer can load properties file with {@link io.jdbd.util.JdbdUtils#loadProperties(Path)}.
     * @throws JdbdException throw url or properties error
     */
    DatabaseSessionFactory forDeveloper(String url, Map<String, Object> properties) throws JdbdException;

    /**
     * <p>
     * This method is designed for poll session vendor developer,so application developer shouldn't invoke this method
     * and use {@link #forDeveloper(String, Map)} method.
     * <br/>
     *
     * <p>  This method return {@link DatabaseSessionFactory} has below feature.
     *     <ul>
     *         <li>{@link DatabaseSessionFactory#localSession()} returning instance is {@link   io.jdbd.pool.PoolLocalDatabaseSession} instance</li>
     *         <li>{@link DatabaseSessionFactory#rmSession()} returning instance is {@link  PoolRmDatabaseSession} instance</li>
     *     </ul>
     * <br/>
     * <p>
     *     This method is used by pool vendor,application developer shouldn't use this method.
     *     <strong>NOTE</strong> : driver developers are not responsible for pooling.
     * <br/>
     *
     * @param url        format : jdbd:protocol:[subProtocol:]//[hostList]/[databaseName][;attributes][?properties] . For example:
     *                   <ul>
     *                     <li>jdbd:mysql://localhost:3306/army_test?sslMode=require</li>
     *                     <li>jdbd:postgresql://localhost:5432/army_test?sslMode=require</li>
     *                   </ul>
     * @param properties properties map ,this map can override the attributes and properties in url. application developer can load properties file with {@link io.jdbd.util.JdbdUtils#loadProperties(Path)}.
     * @throws JdbdException throw url or properties error
     */
    DatabaseSessionFactory forPoolVendor(String url, Map<String, Object> properties) throws JdbdException;


    /**
     * @return database product family,For example :  MySQL , PostgreSQL.
     */
    String productFamily();

    DriverVersion version();


    /**
     * @return driver vendor,The value returned typically is the package name for this vendor.
     */
    String vendor();


    /**
     * override {@link Object#toString()}
     *
     * @return driver info, contain : <ol>
     * <li>implementation class name</li>
     * <li>{@link #vendor()}</li>
     * <li>{@link #productFamily()}</li>
     * <li>{@link #version()}</li>
     * <li>{@link System#identityHashCode(Object)}</li>
     * </ol>
     */
    @Override
    String toString();


    /**
     * @param url format : jdbd:protocol:[subProtocol:]//[hostList]/[databaseName][;attributes][?properties] . For example:
     *            <ul>
     *              <li>jdbd:mysql://localhost:3306/army_test?sslMode=require</li>
     *              <li>jdbd:postgresql://localhost:5432/army_test?sslMode=require</li>
     *            </ul>
     * @throws JdbdException throw when not found match driver.
     */
    static Driver findDriver(String url) throws JdbdException {
        return DriverManager.findDriver(url);
    }


}
