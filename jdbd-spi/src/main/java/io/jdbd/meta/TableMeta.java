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

package io.jdbd.meta;


import io.jdbd.lang.Nullable;
import io.jdbd.session.Option;
import io.jdbd.session.OptionSpec;

import java.util.Set;

public interface TableMeta extends OptionSpec {

    String TABLE = "TABLE", VIEW = "VIEW", SYSTEM_TABLE = "SYSTEM TABLE", SYSTEM_VIEW = "SYSTEM VIEW";

    String GLOBAL_TEMPORARY = "GLOBAL TEMPORARY", LOCAL_TEMPORARY = "LOCAL TEMPORARY";

    String ALIAS = "ALIAS", SYNONYM = "SYNONYM";


    SchemaMeta schemaMeta();

    String tableName();

    /**
     * @return the comment of table
     */
    @Nullable
    String comment();

    /**
     * <p>
     * The privilege set you have for the column,if driver don't support this method,then always empty.
     * <br/>
     *
     * @return a unmodified set ,empty set or privilege set.
     */
    Set<String> privilegeSet();


    /**
     * <p>
     * This implementation of this method must support following :
     *     <ul>
     *         <li>{@link Option#TYPE_NAME}, Typical types are :
     *              <ul>
     *                  <li>{@link #TABLE}</li>
     *                  <li>{@link #VIEW}</li>
     *                  <li>{@link #SYSTEM_TABLE}</li>
     *                  <li>{@link #SYSTEM_VIEW}</li>
     *                  <li>{@link #GLOBAL_TEMPORARY}</li>
     *                  <li>{@link #LOCAL_TEMPORARY}</li>
     *                  <li>{@link #ALIAS}</li>
     *                  <li>{@link #SYNONYM}</li>
     *              </ul>
     *         </li>
     *     </ul>
     *<br/>
     * <p>
     *     The implementation of this method perhaps support some of following :
     *     <ul>
     *         <li>{@link Option#CHARSET}</li>
     *         <li>{@link Option#COLLATION}</li>
     *         <li>{@link Option#PRIVILEGE}</li>
     *     </ul>
     *<br/>
     */
    @Override
    <T> T valueOf(Option<T> option);

    /**
     * override {@link Object#toString()}
     *
     * @return table info, contain : <ol>
     * <li>implementation class name</li>
     * <li>{@link SchemaMeta#catalog()}</li>
     * <li>{@link SchemaMeta#schema()}</li>
     * <li>{@link #tableName()}</li>
     * <li>{@link #comment()}</li>
     * <li>table type</li>
     * <li>{@link System#identityHashCode(Object)}</li>
     * </ol>
     */
    @Override
    String toString();


}
