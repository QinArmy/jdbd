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

package io.jdbd;

import io.jdbd.session.OptionSpec;

/**
 * <p>
 * This interface is base interface of following
 *     <ul>
 *         <li>{@link DriverVersion}</li>
 *         <li>{@link io.jdbd.session.ServerVersion}</li>
 *     </ul>
 * <br/>
 *
 * @since 1.0
 */
public interface VersionSpec extends OptionSpec {

    /**
     * Retrieves the driver's major version number. Initially this should be 1.
     *
     * @return this driver's major version number
     */
    int getMajor();

    /**
     * Gets the driver's minor version number. Initially this should be 0.
     *
     * @return this driver's minor version number
     */
    int getMinor();


    String getVersion();


    int getSubMinor();

    boolean meetsMinimum(int major, int minor, int subMinor);

}
