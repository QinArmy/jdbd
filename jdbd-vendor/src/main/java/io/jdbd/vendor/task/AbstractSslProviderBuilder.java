package io.jdbd.vendor.task;

@Deprecated
public abstract class AbstractSslProviderBuilder {
//
//
//    protected final HostInfo hostInfo;
//
//    protected final Properties properties;
//
//    private ByteBufAllocator allocator;
//
//    protected AbstractSslProviderBuilder(HostInfo hostInfo) {
//        this.hostInfo = hostInfo;
//        this.properties = hostInfo.getProperties();
//    }
//
//    public final AbstractSslProviderBuilder allocator(ByteBufAllocator allocator) {
//        this.allocator = allocator;
//        return this;
//    }
//
//    @Nullable
//    protected abstract StoreProps getStorePropsForKey();
//
//    @Nullable
//    protected abstract StoreProps getStorePropsForTrust();
//
//    protected abstract boolean isFallbackToSystemKeyStore();
//
//    protected abstract SslMode getSslMode();
//
//
//    protected KeyManagerFactory mapKeyManagerFactory(KeyManagerFactory factory) {
//        return factory;
//    }
//
//    /**
//     * <p>
//     * when {@link SslMode} is {@link SslMode#VERIFY_CA} or  {@link SslMode#VERIFY_IDENTITY}
//     * , invoking this method.
//     * </p>
//     * <p>
//     * You can choose to use {@link TrustManagerFactoryWrapper} wrapper factory.
//     * </p>
//     *
//     * @param factory {@link TrustManagerFactory} be initialized with {@link KeyStore}.
//     * @see TrustManagerFactoryWrapper
//     */
//    protected TrustManagerFactory mapTrustManagerFactory(TrustManagerFactory factory) {
//        return factory;
//    }
//
//
//    protected TrustManager createNoVerifyTrustManager() {
//        return NoValidateTrustManager.INSTANCE;
//    }
//
//
//    private SslContext createSslContext() throws SSLException, JdbdConnectionException {
//        SslContextBuilder builder = SslContextBuilder.forClient();
//        // 1. config TrustManager
//        configTrustManager(builder);
//        //2. config KeyManager
//        KeyManagerFactory keyManagerFactory = tryObtainKeyManagerFactory();
//        if (keyManagerFactory != null) {
//            builder.keyManager(keyManagerFactory);
//        }
//        return builder.build();
//    }
//
//    private void configTrustManager(SslContextBuilder contextBuilder) throws JdbdConnectionException {
//        final SslMode sslMode = getSslMode();
//
//        final Pair<KeyStore, char[]> storePair = tryLoadKeyStoreAndPassword(true);
//        final String algorithm = "PKIX";
//        try {
//            if (storePair != null || sslMode == SslMode.VERIFY_CA || sslMode == SslMode.VERIFY_IDENTITY) {
//                TrustManagerFactory factory = TrustManagerFactory.getInstance(algorithm);
//                switch (sslMode) {
//                    case VERIFY_CA:
//                    case VERIFY_IDENTITY:
//                        factory = mapTrustManagerFactory(factory);
//                        break;
//                }
//                if (storePair == null) {
//                    factory.init((KeyStore) null);//initializes the TrustManagerFactory with the default truststore.
//                } else {
//                    factory.init(storePair.getFirst());
//                }
//                contextBuilder.trustManager(factory);
//            } else {
//                contextBuilder.trustManager(createNoVerifyTrustManager());
//            }
//
//        } catch (NoSuchAlgorithmException e) {
//            String message = String.format("%s algorithm[%s] not found.", TrustManagerFactory.class.getName()
//                    , algorithm);
//            throw new JdbdConnectionException(message, e);
//        } catch (KeyStoreException e) {
//            String message = String.format("Cannot init %s due to %s", TrustManagerFactory.class.getName()
//                    , e.getMessage());
//            throw new JdbdConnectionException(message, e);
//        }
//    }
//
//
//    @Nullable
//    private KeyManagerFactory tryObtainKeyManagerFactory() throws JdbdConnectionException {
//        try {
//            Pair<KeyStore, char[]> storePair = tryLoadKeyStoreAndPassword(true);
//            if (storePair == null) {
//                return null;
//            }
//            KeyManagerFactory kmf = KeyManagerFactory.getInstance("PKIX");
//            kmf.init(storePair.getFirst(), storePair.getSecond());
//            return mapKeyManagerFactory(kmf);
//        } catch (NoSuchAlgorithmException e) {
//            String message = String.format("%s algorithm[%s] not found.", KeyManagerFactory.class.getName()
//                    , KeyManagerFactory.getDefaultAlgorithm());
//            throw new JdbdConnectionException(message, e);
//        } catch (KeyStoreException | UnrecoverableKeyException e) {
//            String message = String.format("Cannot init %s due to %s", KeyManagerFactory.class.getName(), e.getMessage());
//            throw new JdbdConnectionException(message, e);
//        }
//    }
//
//
//    @Nullable
//    private Pair<KeyStore, char[]> tryLoadKeyStoreAndPassword(boolean key) throws JdbdConnectionException {
//        final String systemStoreType, systemStoreUrl, systemStorePassword;
//
//        final StoreProps storeProps;
//        if (key) {
//            storeProps = getStorePropsForKey();
//            systemStoreType = System.getProperty("javax.net.ssl.keyStoreType");
//            systemStoreUrl = System.getProperty("javax.net.ssl.keyStore");
//            systemStorePassword = System.getProperty("javax.net.ssl.keyStorePassword");
//        } else {
//            storeProps = getStorePropsForTrust();
//            systemStoreType = System.getProperty("javax.net.ssl.trustStoreType");
//            systemStoreUrl = System.getProperty("javax.net.ssl.trustStore");
//            systemStorePassword = System.getProperty("javax.net.ssl.trustStorePassword");
//        }
//
//        if (storeProps == null && systemStoreUrl == null) {
//            return null;
//        }
//
//        final String storeType, storeUrl, storePassword;
//        if (storeProps != null) {
//            storeType = storeProps.storeType;
//            storeUrl = Objects.requireNonNull(storeProps.storeUrl, "storeProps.storeUrl");
//            storePassword = storeProps.storePassword;
//        } else if (isFallbackToSystemKeyStore()) {
//            storeType = systemStoreType;
//            storeUrl = systemStoreUrl;
//            storePassword = systemStorePassword;
//        } else {
//            return null;
//        }
//
//        URL url;
//        try {
//            url = new URL(storeUrl);
//        } catch (MalformedURLException e) {
//            try {
//                url = new URL("file:" + storeUrl);
//            } catch (MalformedURLException ex) {
//                throw new JdbdConnectionException(String.format("%s[%s] isn't url.", storeUrl, storeUrl), e);
//            }
//        }
//        final char[] pwd = (storePassword == null) ? new char[0] : storePassword.toCharArray();
//
//        try (InputStream in = url.openStream()) {
//
//            final KeyStore keyStore;
//            if (JdbdStrings.hasText(storeType)) {
//                keyStore = KeyStore.getInstance(storeType);
//            } else {
//                keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
//            }
//            keyStore.load(in, pwd);
//            return new Pair<>(keyStore, pwd);
//        } catch (MalformedURLException e) {
//            throw new JdbdConnectionException(String.format("%s[%s] isn't url.", storeUrl, storeUrl), e);
//        } catch (KeyStoreException e) {
//            throw new JdbdConnectionException(String.format("%s[%s] is KeyStore type than is supported by provider."
//                    , storeUrl, storeType), e);
//        } catch (NoSuchAlgorithmException | IOException | CertificateException e) {
//            String message = String.format("Cannot load KeyStore by url[%s] and type[%s]", storeUrl, storeType);
//            throw new JdbdConnectionException(message, e);
//        }
//
//    }
//
//
//    protected static final class StoreProps {
//
//        @Nullable
//        private final String storeType;
//
//        private final String storeUrl;
//
//        @Nullable
//        private final String storePassword;
//
//        public StoreProps(@Nullable String storeType, String storeUrl, @Nullable String storePassword) {
//            this.storeType = storeType;
//            this.storeUrl = storeUrl;
//            this.storePassword = storePassword;
//        }
//
//
//    }
//
//    protected static final class TrustManagerFactoryWrapper extends TrustManagerFactory {
//
//        public static TrustManagerFactoryWrapper verify(TrustManagerFactory factory
//                , UnaryOperator<X509TrustManager> operator) {
//            TrustManagerFactorySpiWrapper spi = new TrustManagerFactorySpiWrapper(factory, operator);
//            return new TrustManagerFactoryWrapper(factory, spi);
//        }
//
//
//        private TrustManagerFactoryWrapper(TrustManagerFactory factory, TrustManagerFactorySpiWrapper spi) {
//            super(spi, factory.getProvider(), factory.getAlgorithm());
//        }
//
//
//    }
//
//    protected static final class KeyManagerFactoryWrapper extends KeyManagerFactory {
//
//        public static KeyManagerFactoryWrapper verify(KeyManagerFactory factory
//                , UnaryOperator<X509KeyManager> operator) {
//            KeyManagerFactorySpiWrapper spi = new KeyManagerFactorySpiWrapper(factory, operator);
//            return new KeyManagerFactoryWrapper(factory, spi);
//        }
//
//        private KeyManagerFactoryWrapper(KeyManagerFactory factory, KeyManagerFactorySpi spi) {
//            super(spi, factory.getProvider(), factory.getAlgorithm());
//        }
//
//    }
//
//
//    protected static final class NoValidateTrustManager implements X509TrustManager {
//
//        public static final NoValidateTrustManager INSTANCE = new NoValidateTrustManager();
//
//        private NoValidateTrustManager() {
//        }
//
//        @Override
//        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) {
//            //no-op
//        }
//
//        @Override
//        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) {
//            //no-op
//        }
//
//        @Override
//        public X509Certificate[] getAcceptedIssuers() {
//            return new X509Certificate[0];
//        }
//    }
//
//    protected static final class SimpleValidateTrustManager implements X509TrustManager {
//
//        public static final SimpleValidateTrustManager INSTANCE = new SimpleValidateTrustManager();
//
//        private SimpleValidateTrustManager() {
//        }
//
//        @Override
//        public void checkClientTrusted(X509Certificate[] chain, String authType)
//                throws CertificateException {
//            for (X509Certificate certificate : chain) {
//                certificate.checkValidity();
//            }
//        }
//
//        @Override
//        public void checkServerTrusted(X509Certificate[] chain, String authType)
//                throws CertificateException {
//            for (X509Certificate certificate : chain) {
//                certificate.checkValidity();
//            }
//        }
//
//        @Override
//        public X509Certificate[] getAcceptedIssuers() {
//            return new X509Certificate[0];
//        }
//    }
//
//    private static final class TrustManagerFactorySpiWrapper extends TrustManagerFactorySpi {
//
//        private final TrustManagerFactory factory;
//
//        private final UnaryOperator<X509TrustManager> function;
//
//
//        private TrustManagerFactorySpiWrapper(TrustManagerFactory factory, UnaryOperator<X509TrustManager> function) {
//            this.factory = factory;
//            this.function = function;
//        }
//
//        @Override
//        protected final void engineInit(KeyStore keyStore) throws KeyStoreException {
//            this.factory.init(keyStore);
//        }
//
//        @Override
//        protected final void engineInit(ManagerFactoryParameters managerFactoryParameters)
//                throws InvalidAlgorithmParameterException {
//            this.factory.init(managerFactoryParameters);
//        }
//
//        @Override
//        protected final TrustManager[] engineGetTrustManagers() {
//            TrustManager[] managerArray = this.factory.getTrustManagers();
//            TrustManager[] wrapperArray = new TrustManager[managerArray.length];
//            TrustManager tm;
//            final UnaryOperator<X509TrustManager> function = this.function;
//            for (int i = 0; i < managerArray.length; i++) {
//                tm = managerArray[i];
//                if (tm instanceof X509TrustManager) {
//                    wrapperArray[i] = function.apply((X509TrustManager) tm);
//                } else {
//                    wrapperArray[i] = tm;
//                }
//            }
//            return wrapperArray;
//        }
//
//
//    }// class TrustManagerFactoryWrapperSpi
//
//    private static final class KeyManagerFactorySpiWrapper extends KeyManagerFactorySpi {
//
//        private final KeyManagerFactory factory;
//
//        private final UnaryOperator<X509KeyManager> operator;
//
//        private KeyManagerFactorySpiWrapper(KeyManagerFactory factory, UnaryOperator<X509KeyManager> operator) {
//            this.factory = factory;
//            this.operator = operator;
//        }
//
//        @Override
//        protected final void engineInit(KeyStore keyStore, char[] chars)
//                throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
//            this.factory.init(keyStore, chars);
//        }
//
//        @Override
//        protected final void engineInit(ManagerFactoryParameters managerFactoryParameters)
//                throws InvalidAlgorithmParameterException {
//            this.factory.init(managerFactoryParameters);
//        }
//
//        @Override
//        protected final KeyManager[] engineGetKeyManagers() {
//            KeyManager[] managerArray = this.factory.getKeyManagers();
//            KeyManager[] wrapperArray = new KeyManager[managerArray.length];
//            KeyManager km;
//            final UnaryOperator<X509KeyManager> operator = this.operator;
//            for (int i = 0; i < managerArray.length; i++) {
//                km = managerArray[i];
//                if (km instanceof X509KeyManager) {
//                    wrapperArray[i] = operator.apply((X509KeyManager) km);
//                } else {
//                    wrapperArray[i] = km;
//                }
//            }
//            return wrapperArray;
//        }
//
//    }// class KeyManagerFactorySpiWrapper


}
