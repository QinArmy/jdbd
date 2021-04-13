package io.jdbd.vendor.conf;


import io.jdbd.UrlException;
import io.jdbd.vendor.util.JdbdCollections;
import io.jdbd.vendor.util.JdbdStringUtils;
import reactor.util.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class AbstractHostInfo<K extends IPropertyKey> implements HostInfo<K> {


    protected final String originalUrl;
    protected final String host;
    protected final int port;
    protected final String user;

    protected final String password;
    protected final boolean isPasswordLess;
    protected final Properties<K> properties;

    protected final String dbName;


    protected AbstractHostInfo(JdbcUrlParser parser, int index) {
        this.originalUrl = Objects.requireNonNull(parser.getOriginalUrl(), "getOriginalUrl");
        final Map<String, String> globalProperties = parser.getGlobalProperties();
        final Map<String, String> hostProperties = parser.getHostInfo().get(index);

        if (!JdbdStringUtils.hasText(this.originalUrl)
                || JdbdCollections.isEmpty(hostProperties)
                || JdbdCollections.isEmpty(globalProperties)) {
            throw new IllegalArgumentException("please check arguments.");
        }
        int capacity = (int) ((globalProperties.size() + hostProperties.size()) / 0.75F);
        final Map<String, String> map = new HashMap<>(capacity);
        //firstly
        map.putAll(globalProperties);
        // secondly
        map.putAll(hostProperties);


        String host = map.remove(HOST);

        this.host = JdbdStringUtils.hasText(host) ? host : DEFAULT_HOST;
        final String portText = map.remove(PORT);
        try {
            this.port = Integer.parseInt(portText);
        } catch (NumberFormatException e) {
            throw new UrlException(e, this.originalUrl, "post[%s] format error", portText);
        }
        this.user = map.remove(USER);
        this.password = map.remove(PASSWORD);

        if (!JdbdStringUtils.hasText(this.user)) {
            throw new UrlException(this.originalUrl, "%s property must be not empty", USER);
        }
        this.isPasswordLess = !JdbdStringUtils.hasText(this.password);
        this.dbName = map.remove(DB_NAME);

        this.properties = createProperties(map);
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("(")
                .append("host = '").append(this.host)
                .append(", port = ").append(this.port)
                .append(", isPasswordLess = ").append(this.isPasswordLess)
                .append(", propertiesSize = ")
                .append(this.properties.size())
                .append(')')
                .toString();
    }

    @Override
    public String getHostPortPair() {
        return this.host + HOST_PORT_SEPARATOR + this.port;
    }


    @Override
    public String getOriginalUrl() {
        return this.originalUrl;
    }

    @Override
    public String getHost() {
        return this.host;
    }

    @Override
    public int getPort() {
        return this.port;
    }

    @Override
    public String getUser() {
        return this.user;
    }

    @Nullable
    public String getPassword() {
        return this.password;
    }

    @Override
    public boolean isPasswordLess() {
        return this.isPasswordLess;
    }

    @Override
    public Properties<K> getProperties() {
        return this.properties;
    }

    @Nullable
    public String getDbName() {
        return this.dbName;
    }


    protected Properties<K> createProperties(Map<String, String> map) {
        return ImmutableMapProperties.getInstance(map);
    }


}