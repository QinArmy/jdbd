package io.jdbd.vendor.env;

import io.jdbd.lang.Nullable;

/**
 * <p>
 * This interface representing database server host from jdbd url.
 * <br/>
 *
 * @since 1.0
 */
public interface JdbdHost {

    String DEFAULT_HOST = "localhost";
    String HOST = "host";

    String PORT = "port";

    String DB_NAME = "dbname";

    String host();

    int port();


    interface HostInfo extends JdbdHost {

        String user();

        @Nullable
        String password();

        @Nullable
        String dbName();

        Environment properties();

        boolean isUnixDomainSocket();


    }


}
