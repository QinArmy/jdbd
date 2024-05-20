module jdbd.spi {
    requires jsr305;
    requires java.transaction.xa;
    requires org.reactivestreams;

    exports io.jdbd;
    exports io.jdbd.lang;
    exports io.jdbd.meta;
    exports io.jdbd.pool;

    exports io.jdbd.result;
    exports io.jdbd.session;
    exports io.jdbd.statement;
    exports io.jdbd.type;

    exports io.jdbd.util;

}
