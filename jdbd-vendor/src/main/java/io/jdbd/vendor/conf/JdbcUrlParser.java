package io.jdbd.vendor.conf;

import reactor.util.annotation.Nullable;

import java.util.List;
import java.util.Map;

public interface JdbcUrlParser {

    String getOriginalUrl();

    String getProtocol();

    @Nullable
    String getDbName();

    @Nullable
    String getSubProtocol();

    /**
     * @return a unmodifiable map
     */
    Map<String, String> getGlobalProperties();

    /**
     * @return a unmodifiable list
     */
    List<Map<String, String>> getHostInfo();

}
