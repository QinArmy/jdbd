package io.jdbd;

import io.jdbd.lang.Nullable;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.ref.SoftReference;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @since 1.0
 */
abstract class DriverManager {

    private DriverManager() {
        throw new UnsupportedOperationException();
    }

    private static SoftReference<ConcurrentMap<Class<?>, Driver>> driverMapHolder;

    static Driver findDriver(final String jdbdUrl) throws JdbdException {

        SoftReference<ConcurrentMap<Class<?>, Driver>> reference = DriverManager.driverMapHolder;

        Driver driver = null;
        ConcurrentMap<Class<?>, Driver> driverMap;
        if (reference == null || (driverMap = reference.get()) == null) {
            driverMap = new ConcurrentHashMap<>();
            DriverManager.driverMapHolder = new SoftReference<>(driverMap);
        } else {
            for (Driver cacheDriver : driverMap.values()) {
                if (cacheDriver.acceptsUrl(jdbdUrl)) {
                    driver = cacheDriver;
                    break;
                }
            }
        }

        if (driver == null) {
            driver = loadDriverMap(jdbdUrl, driverMap);
        }

        if (driver == null) {
            throw new JdbdException(String.format("Not found driver for url %s", jdbdUrl));
        }
        return driver;

    }


    @Nullable
    private static Driver loadDriverMap(final String jdbdUrl, final ConcurrentMap<Class<?>, Driver> driverMap) {
        try {
            final Enumeration<URL> enumeration;
            enumeration = Thread.currentThread().getContextClassLoader()
                    .getResources("META-INF/jdbd/io.jdbd.Driver");

            Driver driver = null;
            while (enumeration.hasMoreElements()) {
                driver = loadDriverInstance(enumeration.nextElement(), jdbdUrl, driverMap);
                if (driver != null) {
                    break;
                }
            }
            return driver;
        } catch (Throwable e) {
            //no bug ,never here
            throw new JdbdException(e.getMessage(), e);
        }
    }


    @Nullable
    private static Driver loadDriverInstance(final URL url, final String jdbdUrl,
                                             final ConcurrentMap<Class<?>, Driver> driverMap) throws JdbdException {
        final Charset charset = StandardCharsets.UTF_8;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), charset))) {

            String line;
            Driver driver = null;
            Class<?> driverClass;
            while ((line = reader.readLine()) != null) {
                driverClass = loadClass(line);
                if (driverClass == null) {
                    continue;
                }
                driver = getDriverInstance(driverClass);
                if (driver == null) {
                    continue;
                }

                driverMap.putIfAbsent(driverClass, driver);

                if (driver.acceptsUrl(jdbdUrl)) {
                    break;
                }
                driver = null; // clear
            }
            return driver;
        } catch (Throwable e) {
            //  don't follow io.jdbd.Driver contract
            throw new JdbdException(e.getMessage(), e);
        }

    }

    @Nullable
    private static Driver getDriverInstance(final Class<?> driverClass) {
        Driver instance;
        try {
            final Method method;
            method = driverClass.getMethod("getInstance");
            final int modifier = method.getModifiers();
            if (Driver.class.isAssignableFrom(driverClass)
                    && Modifier.isPublic(modifier)
                    && Modifier.isStatic(modifier)
                    && method.getParameterCount() == 0
                    && Driver.class.isAssignableFrom(method.getReturnType())) {
                instance = (Driver) method.invoke(null);
            } else {
                instance = null;
            }
        } catch (Throwable e) {
            // don't follow io.jdbd.Driver contract,so ignore.
            instance = null;
        }
        return instance;
    }

    @Nullable
    private static Class<?> loadClass(final String className) {
        Class<?> driverClass;
        try {
            driverClass = Class.forName(className);
        } catch (ClassNotFoundException e) {
            driverClass = null;
        }
        return driverClass;
    }


}
