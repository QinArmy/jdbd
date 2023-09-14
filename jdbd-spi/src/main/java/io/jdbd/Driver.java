package io.jdbd;

import io.jdbd.pool.PoolRmDatabaseSession;
import io.jdbd.session.DatabaseSessionFactory;

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
 * </p>
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
 * </p>
 * <p>
 * Application/pool developer can get the instance of {@link Driver} by {@link #findDriver(String)}
 * </p>
 *
 * @since 1.0
 */
public interface Driver {

    /**
     * <p>
     * Required , jdbd always support
     * </p>
     *
     * @see io.jdbd.session.Option#USER
     */
    String USER = "user";

    /**
     * <p>
     * Required , jdbd always support
     * </p>
     */
    String PASSWORD = "password";

    /**
     * <p>
     * Required , jdbd always support, if application developer don't put this property,then {@link DatabaseSessionFactory#name()} always return 'unnamed' .
     * </p>
     */
    String FACTORY_NAME = "factoryName";

    String PREPARE_THRESHOLD = "prepareThreshold";


    String CLIENT_INFO = "clientInfo";


    /**
     * @param url jdbc url
     * @return true: accept
     * @throws NullPointerException when url is null
     */
    boolean acceptsUrl(String url);


    /**
     * <p>
     * Create {@link DatabaseSessionFactory} for application developer.The factory don't create pool {@link io.jdbd.session.DatabaseSession}.
     * Because driver developers are not responsible for pooling.
     * </p>
     * <p>
     * Pool vendor developer should use {@link #forPoolVendor(String, Map)} create {@link DatabaseSessionFactory}.
     * </p>
     *
     * @param url format :jdbd:protocol:[subProtocol:]//[hostList]/[databaseName][;attributes][?properties] . For example:
     *            <ul>
     *              <li>jdbd:mysql://localhost:3306/army_test?sslMode=require</li>
     *              <li>jdbd:postgresql://localhost:5432/army_test?sslMode=require</li>
     *            </ul>
     */
    DatabaseSessionFactory forDeveloper(String url, Map<String, Object> properties) throws JdbdException;

    /**
     * <p>
     * This method is designed for poll session vendor developer,so application developer shouldn't invoke this method
     * and use {@link #forDeveloper(String, Map)} method.
     * </p>
     *
     * <p>  This method return {@link DatabaseSessionFactory} has below feature.
     *     <ul>
     *         <li>{@link DatabaseSessionFactory#localSession()} returning instance is {@link   io.jdbd.pool.PoolLocalDatabaseSession} instance</li>
     *         <li>{@link DatabaseSessionFactory#rmSession()} returning instance is {@link  PoolRmDatabaseSession} instance</li>
     *     </ul>
     * </p>
     * <p>
     *     This method is used by pool vendor,application developer shouldn't use this method.
     *     <strong>NOTE</strong> : driver developers are not responsible for pooling.
     * </p>
     *
     * @param url format : jdbd:protocol:[subProtocol:]//[hostList]/[databaseName][;attributes][?properties] . For example:
     *            <ul>
     *              <li>jdbd:mysql://localhost:3306/army_test?sslMode=require</li>
     *              <li>jdbd:postgresql://localhost:5432/army_test?sslMode=require</li>
     *            </ul>
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
