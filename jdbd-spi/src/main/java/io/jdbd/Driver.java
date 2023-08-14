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
 * Application/pool developer can get the instance of {@link Driver} by {@link #findDriver(String)}
 * </p>
 *
 * @see DriverManager
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
     * <p>
     * Optional , driver perhaps support.
     * </p>
     *
     * @see io.jdbd.session.Option#AUTO_RECONNECT
     */
    String AUTO_RECONNECT = "autoReconnect";


    /**
     * @param url jdbc url
     * @return true: accept
     * @throws NullPointerException when url is null
     */
    boolean acceptsUrl(String url);


    /**
     * @param url jdbc url
     */
    DatabaseSessionFactory createSessionFactory(String url, Map<String, Object> properties) throws JdbdException;

    /**
     * <p>
     * This method is designed for poll session vendor developer,so application developer shouldn't invoke this method
     * and use {@link #createSessionFactory(String, Map)} method.
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
     *     <strong>NOTE</strong> : driver developer are not responsible for pooling.
     * </p>
     *
     * @param url jdbc url
     */
    DatabaseSessionFactory forPoolVendor(String url, Map<String, Object> properties) throws JdbdException;


    /**
     * @return database product name,For example :  MySQL , PostgreSQL.
     */
    String productName();

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
     * <li>{@link #productName()}</li>
     * <li>{@link #version()}</li>
     * <li>{@link System#identityHashCode(Object)}</li>
     * </ol>
     */
    @Override
    String toString();


    static Driver findDriver(String url) throws JdbdException {
        return DriverManager.findDriver(url);
    }


}
