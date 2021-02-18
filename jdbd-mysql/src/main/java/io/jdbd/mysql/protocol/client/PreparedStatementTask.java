package io.jdbd.mysql.protocol.client;

import io.jdbd.ResultRow;
import io.jdbd.ResultStates;
import io.jdbd.mysql.JdbdMySQLException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Consumer;

public interface PreparedStatementTask extends MySQLTask {

    /**
     * @throws JdbdMySQLException when task(prepared statement) not prepared yet.
     */
    MySQLColumnMeta[] obtainParameterMeta() throws JdbdMySQLException;

    int obtainParameterCount() throws IllegalStateException;

    MySQLColumnMeta obtainParameterMeta(int parameterIndex) throws IllegalStateException;

    int obtainPreparedWarningCount() throws IllegalStateException;

    MySQLColumnMeta[] obtainColumnMeta();

    Flux<ResultRow> executeQuery(List<BindValue> parameterGroup, Consumer<ResultStates> statesConsumer);

    Mono<ResultStates> executeUpdate(List<BindValue> parameterGroup);

    Flux<ResultStates> executeBatchUpdate(List<List<BindValue>> parameterGroupList);


}
