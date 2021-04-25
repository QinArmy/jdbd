package io.jdbd.mysql.protocol.client;

import io.jdbd.mysql.Groups;
import io.jdbd.mysql.stmt.BindableStmt;
import io.jdbd.result.ResultRow;
import io.jdbd.result.ResultState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Supplier;

/**
 * @see ComQueryCommandWriter
 */
@Test(groups = {Groups.COM_QUERY_WRITER}, dependsOnGroups = {Groups.SESSION_INITIALIZER, Groups.UTILS})
public class ComQueryCommandWriterSuiteTests extends AbstractStmtTaskSuiteTests {

    private static final Logger LOG = LoggerFactory.getLogger(ComQueryCommandWriterSuiteTests.class);

    public ComQueryCommandWriterSuiteTests() {
        super(SubType.COM_QUERY);
    }

    @Override
    Mono<ResultState> executeUpdate(BindableStmt stmt, MySQLTaskAdjutant adjutant) {
        throw new UnsupportedOperationException();
    }

    @Override
    Flux<ResultRow> executeQuery(BindableStmt stmt, MySQLTaskAdjutant adjutant) {
        throw new UnsupportedOperationException();
    }

    @Override
    Logger obtainLogger() {
        return LOG;
    }

    /**
     * @see ComQueryCommandWriter#createStaticSingleCommand(String, Supplier, MySQLTaskAdjutant)
     */
    @Test(timeOut = TIME_OUT)
    public void createStaticSingleCommand() {
        LOG.info("createStaticSingleCommand test start");
        //final MySQLTaskAdjutant adjutant = obtainTaskAdjutant();
        //TODO zoro add test code
        LOG.info("createStaticSingleCommand test success");
        // releaseConnection(adjutant);
    }


}
