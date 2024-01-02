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

package io.jdbd.vendor.task;

public enum SslMode {

    /** Start with encrypted connection, fallback to non-encrypted (default). */
    PREFERRED,
    /** Ensure connection is encrypted. */
    REQUIRED,
    /** Ensure connection is encrypted, and client trusts server certificate. */
    VERIFY_CA,
    /**
     * Ensure connection is encrypted,and verify server certificate
     * ,and that the server certificate matches the host to which the connection is attempted
     */
    VERIFY_IDENTITY,
    /** Do not use encrypted connections. */
    DISABLED;


}
