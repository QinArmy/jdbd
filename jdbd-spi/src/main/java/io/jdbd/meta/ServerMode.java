package io.jdbd.meta;

public enum ServerMode {

    /**
     * database server is embedded in application
     */
    EMBEDDED,

    /**
     * database server is remote,client connect server by net protocol.
     */
    REMOTE,

    /**
     * mix {@link #REMOTE}  and {@link #REMOTE}
     */
    MIXED

}
