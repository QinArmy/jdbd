module jdbd.vendor {

    requires jsr305;
    requires org.slf4j;
    requires io.netty.buffer;
    requires io.netty.handler;

    requires io.netty.transport;
    requires io.netty.transport.unix.common;
    requires io.netty.codec;

    requires java.security.jgss;
    requires org.reactivestreams;
    requires reactor.core;
    requires reactor.netty.core;

    requires jdbd.spi;

    exports io.jdbd.vendor;
    exports io.jdbd.vendor.env;
    exports io.jdbd.vendor.meta;
    exports io.jdbd.vendor.protocol;

    exports io.jdbd.vendor.result;
    exports io.jdbd.vendor.stmt;
    exports io.jdbd.vendor.syntax;
    exports io.jdbd.vendor.task;

    exports io.jdbd.vendor.util;

}
