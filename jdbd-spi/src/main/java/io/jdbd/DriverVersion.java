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

/**
 * <p>
 * This interface representing driver version,see {@link Driver#version()}
 * <br/>
 *
 * @since 1.0
 */
public interface DriverVersion extends VersionSpec {

    /**
     * <p>  return driver name follow below:
     *     <ul>
     *         <li>If developer of implementation is database vendor,then should be database product name(eg:MySQL,DB2)</li>
     *         <li>Else ,then should be driver class name(eg:io.jdbd.mysql.Driver) </li>
     *     </ul>
     *<br/>
     */
    String getName();


}
