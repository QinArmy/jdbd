package io.jdbd.mysql.protocol.client;

public class Enums {

    public enum ClientPrepare {
        PREFERRED,
        UN_SUPPORT_STREAM,
        SERVER
    }

    public enum SslMode {
        PREFERRED, REQUIRED, VERIFY_CA, VERIFY_IDENTITY, DISABLED;
    }

    public enum ZeroDatetimeBehavior { // zeroDateTimeBehavior
        CONVERT_TO_NULL, EXCEPTION, ROUND;
    }
}