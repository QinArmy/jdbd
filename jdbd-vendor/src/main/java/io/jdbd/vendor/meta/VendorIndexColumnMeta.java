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

package io.jdbd.vendor.meta;

import io.jdbd.meta.BooleanMode;
import io.jdbd.meta.IndexColumnMeta;
import io.jdbd.meta.NullsSorting;
import io.jdbd.meta.Sorting;
import io.jdbd.session.Option;
import io.jdbd.vendor.VendorOptions;
import io.jdbd.vendor.util.JdbdOptionSpec;
import io.jdbd.vendor.util.JdbdStrings;

import java.util.Map;

public final class VendorIndexColumnMeta extends JdbdOptionSpec implements IndexColumnMeta {


    /**
     * <p>
     * optionFunc must support following option:
     *     <ul>
     *         <li>{@link VendorOptions#CARDINALITY},see {@link #cardinality()}</li>
     *         <li>{@link VendorOptions#SORTING},see {@link #sorting()}</li>
     *         <li>{@link VendorOptions#NULLS_SORTING},see {@link #nullsSorting()}</li>
     *         <li>{@link VendorTableColumnMeta#notNullMode()},see {@link #nullableMode()}</li>
     *         <li>{@link VendorOptions#VISIBLE},see {@link #visibleMode()}</li>
     *     </ul>
     * <br/>
     *
     * @param name column name
     */
    public static VendorIndexColumnMeta from(String name, Map<Option<?>, ?> optionMap) {
        return new VendorIndexColumnMeta(name, optionMap);
    }

    private final String name;

    private VendorIndexColumnMeta(String name, Map<Option<?>, ?> optionMap) {
        super(optionMap);
        this.name = name;
    }

    @Override
    public String columnName() {
        return this.name;
    }

    @Override
    public long cardinality() {
        return nonNullOf(VendorOptions.CARDINALITY);
    }

    @Override
    public Sorting sorting() {
        return nonNullOf(VendorOptions.SORTING);
    }

    @Override
    public NullsSorting nullsSorting() {
        return nonNullOf(VendorOptions.NULLS_SORTING);
    }

    @Override
    public BooleanMode nullableMode() {
        return nonNullOf(VendorOptions.NOT_NULL_MODE);
    }

    @Override
    public BooleanMode visibleMode() {
        return nonNullOf(VendorOptions.VISIBLE);
    }

    @Override
    public String toString() {
        return JdbdStrings.builder(220)
                .append(getClass().getName())
                .append("[ columnName : '")
                .append(this.name)
                .append("' , cardinality ")
                .append(cardinality())
                .append(" , sorting : ")
                .append(sorting())
                .append(" , nullsSorting : ")
                .append(nullsSorting())
                .append(" , nullableMode : ")
                .append(nullableMode())
                .append(" , visibleMode : ")
                .append(visibleMode())
                .append(" , hash : ")
                .append(System.identityHashCode(this))
                .append(" ]")
                .toString();
    }


}
