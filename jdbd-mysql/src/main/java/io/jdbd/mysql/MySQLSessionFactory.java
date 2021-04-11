package io.jdbd.mysql;

import io.jdbd.JdbdSessionFactory;
import io.jdbd.TxDatabaseSession;
import io.jdbd.mysql.protocol.client.ClientCommandProtocol;
import io.jdbd.mysql.protocol.conf.MySQLUrl;
import reactor.core.publisher.Mono;

import java.util.Map;

public class MySQLSessionFactory implements JdbdSessionFactory {

    public static MySQLSessionFactory getInstance(String url, Map<String, String> properties) {
        return new MySQLSessionFactory(MySQLUrl.getInstance(url, properties));
    }

    private final MySQLUrl mySQLUrl;

    private MySQLSessionFactory(MySQLUrl mySQLUrl) {
        this.mySQLUrl = mySQLUrl;
    }

    @Override
    public Mono<TxDatabaseSession> getSession() {
        return Mono.empty();
    }

    @Override
    public Mono<Void> close() {
        return Mono.empty();
    }

    private Mono<TxDatabaseSession> createClientSession() {
        return Mono.empty();
    }

    private Mono<ClientCommandProtocol> handshake(ClientCommandProtocol clientProtocol) {
        return Mono.empty();
    }

    private Mono<ClientCommandProtocol> sslRequest(ClientCommandProtocol clientProtocol) {
        return Mono.just(clientProtocol);
    }

    private Mono<ClientCommandProtocol> authenticate(ClientCommandProtocol clientProtocol) {
        return Mono.just(clientProtocol);
    }

}
