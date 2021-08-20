package io.jdbd.postgre.protocol.client;

import io.jdbd.postgre.config.PostgreUrl;
import io.jdbd.postgre.session.SessionAdjutant;
import io.jdbd.result.ResultState;
import io.netty.channel.EventLoopGroup;
import reactor.core.publisher.Mono;
import reactor.netty.resources.LoopResources;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

abstract class AbstractTaskTests {


    static final Queue<ClientProtocol> PROTOCOL_QUEUE = new LinkedBlockingQueue<>();

    private final static EventLoopGroup EVENT_LOOP_GROUP = LoopResources.create("jdbd-postgre", 20, true)
            .onClient(true);

    static final SessionAdjutant DEFAULT_SESSION_ADJUTANT = createDefaultSessionAdjutant();


    static ResultState assertUpdateOne(ResultState state) {
        assertEquals(state.getAffectedRows(), 1L, "affectedRows");
        return state;
    }


    static Mono<ClientProtocol> obtainProtocol() {
        final ClientProtocol protocol = PROTOCOL_QUEUE.poll();
        final Mono<ClientProtocol> mono;
        if (protocol == null) {
            mono = ClientProtocolFactory.single(DEFAULT_SESSION_ADJUTANT, 0);
        } else {
            mono = protocol.reset();
        }
        return mono;
    }

    static ClientProtocol obtainProtocolWithSync() {
        ClientProtocol protocol;
        protocol = obtainProtocol()
                .block();
        assertNotNull(protocol, "protocol");
        return protocol;
    }

    static TaskAdjutant mapToTaskAdjutant(ClientProtocol protocol) {
        return ((ClientProtocolImpl) protocol).adjutant;
    }

    static <T> Mono<T> releaseConnection(ClientProtocol protocol) {
        return protocol.reset()
                .doAfterTerminate(() -> PROTOCOL_QUEUE.offer(protocol))
                .then(Mono.empty());
    }


    static SessionAdjutant createDefaultSessionAdjutant() {
        Map<String, String> propMap = new HashMap<>();
        return new SessionAdjutantImpl(ClientTestUtils.createUrl(propMap));
    }


    private static final class SessionAdjutantImpl implements SessionAdjutant {

        private final PostgreUrl postgreUrl;

        private SessionAdjutantImpl(PostgreUrl postgreUrl) {
            this.postgreUrl = postgreUrl;
        }

        @Override
        public PostgreUrl obtainUrl() {
            return this.postgreUrl;
        }

        @Override
        public EventLoopGroup obtainEventLoopGroup() {
            return EVENT_LOOP_GROUP;
        }

    }


}
