package io.jdbd.mysql.protocol.client;

import io.jdbd.JdbdSQLException;
import io.jdbd.mysql.Groups;
import io.jdbd.mysql.protocol.authentication.CachingSha2PasswordPlugin;
import io.jdbd.mysql.protocol.authentication.MySQLNativePasswordPlugin;
import io.jdbd.mysql.protocol.conf.PropertyKey;
import io.jdbd.mysql.session.MySQLSessionAdjutant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.*;
import reactor.netty.resources.LoopResources;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

@Test(groups = {Groups.AUTHENTICATE_PLUGIN}, dependsOnGroups = {Groups.MYSQL_URL, Groups.SQL_PARSER, Groups.UTILS})
public class AuthenticatePluginSuiteTests extends AbstractConnectionBasedSuiteTests {

    private static final Logger LOG = LoggerFactory.getLogger(AuthenticatePluginSuiteTests.class);


    @BeforeSuite
    public static void beforeSuite(ITestContext context) {
        LOG.info("LoopResources {}", LoopResources.hasNativeSupport());
    }

    @AfterSuite
    public static void afterSuite(ITestContext context) {

    }

    @BeforeClass
    public static void beforeClass() {
        LOG.info("\n{} group test start.\n", Groups.AUTHENTICATE_PLUGIN);
    }

    @AfterClass
    public static void afterClass() {
        LOG.info("\n{} group test end.\n", Groups.AUTHENTICATE_PLUGIN);
    }

    @Test(timeOut = TIME_OUT)
    public void cachingSha2PasswordPublicKeyAuthenticate() {
        final long startTime = System.currentTimeMillis();
        //TODO zoro add unit test
        LOG.info("cachingSha2PasswordPublicKeyAuthenticate test start.");
        final Path serverRSAPublicKeyPath;
        serverRSAPublicKeyPath = Paths.get(ClientTestUtils.getTestResourcesPath().toString()
                , "my-local/mysql-server/public_key.pem");

        if (Files.notExists(serverRSAPublicKeyPath)) {
            LOG.warn("{} not exists,ignore cachingSha2PasswordPublicKeyAuthenticate.", serverRSAPublicKeyPath);
            return;
        }

        final Map<String, String> propMap;
        propMap = new HashMap<>();

        propMap.put(PropertyKey.sslMode.getKey(), Enums.SslMode.DISABLED.name());
        // here use (CachingSha2PasswordPlugin and sslMode = DISABLED)
        propMap.put(PropertyKey.defaultAuthenticationPlugin.getKey(), CachingSha2PasswordPlugin.PLUGIN_NAME);
        propMap.put(PropertyKey.authenticationPlugins.getKey(), CachingSha2PasswordPlugin.class.getName());
        propMap.put(PropertyKey.serverRSAPublicKeyFile.getKey(), serverRSAPublicKeyPath.toString());

        // propMap.put(PropertyKey.allowPublicKeyRetrieval.getKey(), serverRSAPublicKeyPath.toString());

        MySQLSessionAdjutant sessionAdjutant = createSessionAdjutantForSingleHost(propMap);

        try {
            AuthenticateResult result = MySQLTaskExecutor.create(0, sessionAdjutant)
                    .flatMap(executor -> MySQLConnectionTask.authenticate(executor.getAdjutant()))
                    .block();

            assertNotNull(result, "result");
        } catch (Throwable e) {
            LOG.error("cachingSha2PasswordPublicKeyAuthenticate cost {}ms", System.currentTimeMillis() - startTime);
            throw e;
        }

        LOG.info("cachingSha2PasswordPublicKeyAuthenticate test end,cost {} ms", System.currentTimeMillis() - startTime);
    }


    @Test(dependsOnMethods = "cachingSha2PasswordPublicKeyAuthenticate", timeOut = TIME_OUT)
    public void defaultPlugin() throws Exception {
        LOG.info("defaultPlugin test start.");
        final Map<String, String> propMap;

        propMap = new HashMap<>();
        //propMap.put(PropertyKey.detectCustomCollations.getKey(), "true");
        //propMap.put(PropertyKey.sslMode.getKey(),  Enums.SslMode.PREFERRED.name());

        MySQLSessionAdjutant sessionAdjutant = createSessionAdjutantForSingleHost(propMap);

        AuthenticateResult result = MySQLTaskExecutor.create(0, sessionAdjutant)
                .flatMap(executor -> MySQLConnectionTask.authenticate(executor.getAdjutant()))
                .block();

        assertNotNull(result, "result");

        HandshakeV10Packet packet = result.handshakeV10Packet();
        assertNotNull(packet, "HandshakeV10Packet");

        assertTrue(result.negotiatedCapability() != 0, "negotiatedCapability");

        LOG.info("defaultPlugin test success. {}", packet);
    }


    @Test(dependsOnMethods = "defaultPlugin", timeOut = TIME_OUT)
    public void defaultPluginWithSslDisabled() {
        LOG.info("defaultPluginWithSslDisabled test start.");
        final Map<String, String> propMap;
        propMap = Collections.singletonMap(PropertyKey.sslMode.getKey()
                , Enums.SslMode.DISABLED.name());

        MySQLSessionAdjutant sessionAdjutant = createSessionAdjutantForSingleHost(propMap);

        AuthenticateResult result = MySQLTaskExecutor.create(0, sessionAdjutant)
                .flatMap(executor -> MySQLConnectionTask.authenticate(executor.getAdjutant()))
                .block();

        assertNotNull(result, "result");
        LOG.info("defaultPluginWithSslDisabled test success.handshakeV10Packet:\n {}", result.handshakeV10Packet());

    }

    @Test(dependsOnMethods = "defaultPlugin", expectedExceptions = JdbdSQLException.class)
    public void cachingSha2PasswordPluginEmptyPassword() {
        LOG.info("cachingSha2PasswordPluginEmptyPassword test start.");

        final Map<String, String> propMap;
        propMap = new HashMap<>();

        propMap.put(PropertyKey.sslMode.getKey(), Enums.SslMode.DISABLED.name());
        propMap.put(PropertyKey.defaultAuthenticationPlugin.getKey(), CachingSha2PasswordPlugin.PLUGIN_NAME);
        propMap.put(PropertyKey.authenticationPlugins.getKey(), CachingSha2PasswordPlugin.class.getName());
        propMap.put(PropertyKey.password.getKey(), "");

        MySQLSessionAdjutant sessionAdjutant = createSessionAdjutantForSingleHost(propMap);

        AuthenticateResult result = MySQLTaskExecutor.create(0, sessionAdjutant)
                .flatMap(executor -> MySQLConnectionTask.authenticate(executor.getAdjutant()))
                .block();

        assertNotNull(result, "result");

        Assert.fail("cachingSha2PasswordPluginEmptyPassword test failure.");
    }

    /**
     * This test need to config mysql serer.
     */
    @Test(enabled = false, dependsOnMethods = "defaultPlugin")
    public void mySQLNativePasswordPlugin() {
        LOG.info("mySQLNativePasswordPlugin test start.");

        final Map<String, String> propMap;
        propMap = new HashMap<>();

        propMap.put(PropertyKey.sslMode.getKey(), Enums.SslMode.DISABLED.name());
        propMap.put(PropertyKey.defaultAuthenticationPlugin.getKey(), MySQLNativePasswordPlugin.PLUGIN_NAME);
        propMap.put(PropertyKey.authenticationPlugins.getKey(), MySQLNativePasswordPlugin.class.getName());

        MySQLSessionAdjutant sessionAdjutant = createSessionAdjutantForSingleHost(propMap);

        AuthenticateResult result = MySQLTaskExecutor.create(0, sessionAdjutant)
                .flatMap(executor -> MySQLConnectionTask.authenticate(executor.getAdjutant()))
                .block();

        assertNotNull(result, "result");

        LOG.info("mySQLNativePasswordPlugin test end.");
    }


}
