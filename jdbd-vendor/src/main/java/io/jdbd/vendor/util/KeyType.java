package io.jdbd.vendor.util;

public enum KeyType {

    AES("AES"),
    ARCFOUR("ARCFOUR"),
    BLOWFISH("Blowfish"),
    DES("DES"),

    DE_SEDE("DESede"),
    HMAC_MD5("HmacMD5"),
    HMAC_SHA1("HmacSHA1"),
    HMAC_SHA224("HmacSHA224"),

    HMAC_SHA256("HmacSHA256"),
    HMAC_SHA384("HmacSHA384"),
    HMAC_SHA512("HmacSHA512"),
    RC2("RC2");


    private final String algorithm;


    KeyType(String algorithm) {
        this.algorithm = algorithm;
    }

    public final String getAlgorithm() {
        return algorithm;
    }

}
