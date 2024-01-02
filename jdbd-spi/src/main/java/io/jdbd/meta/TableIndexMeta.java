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
import io.jdbd.session.OptionSpec;

import java.util.List;
import java.util.function.Function;

/**
 * <p>
 * This interface representing table index meta.
 * <br/>
 *
 * @see DatabaseMetaData#indexesOfTable(TableMeta, Function)
 * @since 1.0
 */
public interface TableIndexMeta extends OptionSpec {

    String BTREE = "BTREE", HASH = "HASH", FULLTEXT = "FULLTEXT", RTREE = "RTREE";

    TableMeta tableMeta();


    String indexName();

    /**
     * @return index type,typical types are :
     * <ul>
     *     <li>{@link #BTREE}</li>
     *     <li>{@link #HASH}</li>
     *     <li>{@link #FULLTEXT}</li>
     *     <li>{@link #RTREE}</li>
     * </ul>
     */
    String indexType();

    KeyType keyType();

    /**
     * @return true : index is unique
     */
    boolean isUnique();

    /**
     * <p>
     * Index column list,the order math the column sequence number in the index.
     *<br/>
     *
     * @return a unmodified list
     */
    List<IndexColumnMeta> indexColumnList();

    /**
     * @return index comment
     */
    @Nullable
    String comment();

    /**
     * override {@link Object#toString()}
     *
     * @return index info, contain : <ol>
     * <li>implementation class name</li>
     * <li>{@link SchemaMeta#catalog()}</li>
     * <li>{@link SchemaMeta#schema()}</li>
     * <li>{@link TableMeta#tableName()}</li>
     * <li>{@link #indexName()}</li>
     * <li>{@link #indexType()}</li>
     * <li>{@link #keyType()}</li>
     * <li>{@link #isUnique()}</li>
     * <li>{@link #indexColumnList()} </li>
     * <li>{@link #comment()}</li>
     * <li>{@link System#identityHashCode(Object)}</li>
     * </ol>
     */
    @Override
    String toString();


}
