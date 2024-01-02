/*
 * Copyright 2023-2043 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
