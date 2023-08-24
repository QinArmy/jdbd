package io.jdbd.vendor.util;

public enum KeyPairType {

    /**
     * Generates keypairs for the RSA algorithm (Signature/Cipher).
     */
    RSA("RSA"),

    /**
     * Generates keypairs for the Diffie-Hellman KeyAgreement algorithm.
     * Note: key.getAlgorithm() will return "DH" instead of "DiffieHellman".
     */
    DH("DiffieHellman"),

    /**
     * Generates keypairs for the Digital Signature Algorithm.
     */
    DSA("DSA"),

//    /**
//     * Generates keypairs for the RSASSA-PSS signature algorithm.
//     */
    // RSASSA_PSS("RSASSA-PSS"),

    /**
     * Generates keypairs for the Elliptic Curve algorithm.
     */
    EC("EC");

    private final String algorithm;


    KeyPairType(String algorithm) {
        this.algorithm = algorithm;
    }

    public final String getAlgorithm() {
        return this.algorithm;
    }
}
