package io.jdbd.session;

import io.jdbd.Driver;
import io.jdbd.DriverVersion;
import io.jdbd.lang.Nullable;
import org.reactivestreams.Publisher;

import java.util.Map;


/**
 * <p>
 * This interface representing the factory that create {@link DatabaseSession} by following methods:
 *     <ul>
 *         <li>{@link #localSession()}</li>
 *         <li>{@link #rmSession()}</li>
 *     </ul>
 * <br/>
 * <p>
 * The instance of this interface is created by :
 * <ul>
 *     <li>{@link Driver#forDeveloper(String, Map)}</li>
 *     <li>{@link Driver#forPoolVendor(String, Map)}</li>
 *     <li>pool vendor</li>
 * </ul>
 * <br/>
 *
 * @since 1.0
 */
public interface DatabaseSessionFactory extends OptionSpec, Closeable {

    /**
     * <p>
     * This method return factory name ,if you don't specified then return 'unnamed' .
     * <br/>
     *
     * @return factory name. see {@link io.jdbd.Driver#FACTORY_NAME}
     */
    String name();


    /**
     * <p>This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // factory is instance of DatabaseSessionFactory
     *             factory.localSession(null) ;
     *         </code>
     * </pre>
     *
     * @see #localSession(String)
     */
    Publisher<LocalDatabaseSession> localSession();

    /**
     * <p>
     * Get the instance of {@link LocalDatabaseSession}.
     * <br/>
     * <p>
     * <strong>NOTE</strong> : driver don't send message to database server before subscribing.
     * Driver developer must guarantee this feature.
     * <br/>
     *
     * @param name optional session name,if null ,then {@link DatabaseSession#name()} return 'unnamed' .
     * @return emit just one {@link LocalDatabaseSession} instance or {@link Throwable}. Like {@code reactor.core.publisher.Mono}.
     * <ul>
     * <li>If the instance of {@link DatabaseSessionFactory} is created pool vendor , then always emit non-{@link io.jdbd.pool.PoolLocalDatabaseSession} instance.</li>
     * <li>Else if the instance of {@link DatabaseSessionFactory} is created driver vendor ,then :
     *      <ul>
     *          <li>If the instance of {@link DatabaseSessionFactory} is created {@link Driver#forPoolVendor(String, Map)}, then always emit {@link io.jdbd.pool.PoolLocalDatabaseSession} instance.</li>
     *          <li>Else always emit non-{@link io.jdbd.pool.PoolLocalDatabaseSession} instance.</li>
     *      </ul>
     * </li>
     * <li>Else emit {@link LocalDatabaseSession} instance.</li>
     * </ul>
     * @throws io.jdbd.JdbdException emit(not throw) when
     *                               <ul>
     *                                   <li>this {@link DatabaseSessionFactory} have closed.</li>
     *                                   <li>network error</li>
     *                                   <li>server response error message,see {@link io.jdbd.result.ServerException}</li>
     *                               </ul>
     */
    Publisher<LocalDatabaseSession> localSession(@Nullable String name);


    /**
     * <p>This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // factory is instance of DatabaseSessionFactory
     *             factory.rmSession(null) ;
     *         </code>
     * </pre>
     *
     * @see #rmSession(String)
     */
    Publisher<RmDatabaseSession> rmSession();

    /**
     * <p>
     * Get the instance of {@link RmDatabaseSession}.
     * <br/>
     * <p>
     * <strong>NOTE</strong> : driver don't send message to database server before subscribing.
     * Driver developer must guarantee this feature.
     * <br/>
     *
     * @param name optional session name,if null ,then {@link DatabaseSession#name()} return 'unnamed' .
     * @return emit just one {@link RmDatabaseSession} instance or {@link Throwable}. Like {@code reactor.core.publisher.Mono}.
     * <ul>
     * <li>If the instance of {@link DatabaseSessionFactory} is created pool vendor , then always emit non-{@link io.jdbd.pool.PoolRmDatabaseSession} instance.</li>
     * <li>Else if the instance of {@link DatabaseSessionFactory} is created driver vendor ,then :
     *      <ul>
     *          <li>If the instance of {@link DatabaseSessionFactory} is created {@link Driver#forPoolVendor(String, Map)}, then always emit {@link io.jdbd.pool.PoolRmDatabaseSession} instance.</li>
     *          <li>Else always emit non-{@link io.jdbd.pool.PoolRmDatabaseSession} instance.</li>
     *      </ul>
     * </li>
     * <li>Else emit {@link RmDatabaseSession} instance.</li>
     * </ul>
     * @throws io.jdbd.JdbdException emit(not throw) when
     *                               <ul>
     *                                   <li>driver don't support this method</li>
     *                                   <li>this {@link DatabaseSessionFactory} have closed.</li>
     *                                   <li>network error</li>
     *                                   <li>server response error message,see {@link io.jdbd.result.ServerException}</li>
     *                               </ul>
     */
    Publisher<RmDatabaseSession> rmSession(@Nullable String name);


    /**
     * @return database product family,For example :  MySQL , PostgreSQL.
     */
    String productFamily();


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
     * @see Driver#version()
     */
    DriverVersion driverVersion();

    /**
     * override {@link Object#toString()}
     *
     * @return driver info, contain : <ol>
     * <li>{@link #name()}</li>
     * <li>{@link #factoryVendor()}</li>
     * <li>{@link #driverVersion()}</li>
     * <li>{@link #productFamily()}</li>
     * <li>{@link #driverVersion()}</li>
     * <li>{@link System#identityHashCode(Object)}</li>
     * </ol>
     */
    @Override
    String toString();


}
