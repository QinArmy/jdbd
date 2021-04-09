package io.jdbd.mysql.protocol.client;

import io.jdbd.MultiResults;
import io.jdbd.ResultRow;
import io.jdbd.ResultStates;
import io.jdbd.mysql.Groups;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Test(groups = {Groups.MULTI_STMT}, dependsOnGroups = {Groups.COM_QUERY})
public class MultiStatementSuiteTests extends AbstractConnectionBasedSuiteTests {

    private static final Logger LOG = LoggerFactory.getLogger(MultiStatementSuiteTests.class);


    @Test(timeOut = TIME_OUT)
    public void multiStatement() {
        LOG.info("multiStatement test start");
        final MySQLTaskAdjutant adjutant = obtainMultiStmtTaskAdjutant();
        String sql;
        List<String> sqlList = new ArrayList<>();

        sql = "UPDATE mysql_types as t SET t.name = 'mysql' WHERE t.id = 20";
        sqlList.add(sql);

        sql = String.format("UPDATE mysql_types as t SET t.my_date = '%s' WHERE t.id = 21", LocalDate.now());
        sqlList.add(sql);

        sql = "SELECT t.id as id ,t.name as name,t.create_time as createTime FROM mysql_types as t ORDER BY t.id LIMIT 50";
        sqlList.add(sql);

        sql = String.format("UPDATE mysql_types as t SET t.my_time= '%s' WHERE t.id = 22", LocalTime.now());
        sqlList.add(sql);

        sql = "SELECT t.id as id ,t.name as name,t.create_time as createTime FROM mysql_types as t ORDER BY t.id LIMIT 50";
        sqlList.add(sql);

        sql = String.format("UPDATE mysql_types as t SET t.my_time= '%s' WHERE t.id = 23", LocalTime.now());
        sqlList.add(sql);

        final MultiResults multiResults1 = ComQueryTask.multiStmt(Collections.unmodifiableList(sqlList), adjutant);
        Mono.from(multiResults1.nextUpdate())
                .map(this::assertUpdateSuccess)

                .then(assertNextUpdateSuccess(multiResults1))
                .then(assertNextQuerySuccess(multiResults1))
                .then(assertNextUpdateSuccess(multiResults1))
                .then(assertNextQuerySuccess(multiResults1))

                .then(assertNextUpdateSuccess(multiResults1))
                .block();


        LOG.info("multiStatement test success");
        releaseMultiStmtConnection(adjutant);
    }


    /*################################## blow private method ##################################*/


    private ResultStates assertUpdateSuccess(ResultStates states) {
        Assert.assertEquals(states.getAffectedRows(), 1L, "update rows");
        return states;
    }

    private Mono<Void> assertNextUpdateSuccess(MultiResults results) {
        return Mono.defer(() -> Mono.from(results.nextUpdate()))
                .map(this::assertUpdateSuccess)
                .then();
    }

    private Mono<Void> assertNextQuerySuccess(MultiResults results) {
        return Flux.defer(() -> Flux.from(results.nextQuery()))
                .switchIfEmpty(emptyError())
                .map(this::assertResultRow)
                .then();
    }


    private ResultRow assertResultRow(ResultRow row) {
        LOG.trace("id:{},name:{},createTime:{}",
                row.getNonNull("id", Long.class),
                row.getNonNull("name", String.class),
                row.getNonNull("createTime", LocalDateTime.class)
        );
        return row;
    }

    private Mono<ResultRow> emptyError() {
        return Mono.defer(() -> Mono.error(new RuntimeException("Query result set is empty.")));
    }


}
