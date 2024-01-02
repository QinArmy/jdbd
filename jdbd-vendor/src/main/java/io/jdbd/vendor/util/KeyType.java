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
