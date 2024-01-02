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

import io.jdbd.lang.Nullable;
import io.jdbd.meta.SchemaMeta;
import io.jdbd.meta.TableMeta;
import io.jdbd.session.Option;
import io.jdbd.util.JdbdUtils;
import io.jdbd.vendor.util.JdbdStrings;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Set;
import java.util.function.Function;

public final class VendorTableMeta implements TableMeta {

    public static VendorTableMeta from(SchemaMeta schemaMetaData, String tableName, @Nullable String comment,
                                       Function<Option<?>, ?> optionFunc) {
        if (JdbdUtils.hasNoText(tableName) || optionFunc.apply(Option.TYPE_NAME) == null) {
            throw new IllegalArgumentException();
        }
        return new VendorTableMeta(schemaMetaData, tableName, comment, optionFunc);
    }

    private final SchemaMeta schemaMetaData;

    private final String tableName;

    private final String comment;

    private final Function<Option<?>, ?> optionFunc;

    private VendorTableMeta(SchemaMeta schemaMetaData, String tableName, @Nullable String comment,
                            Function<Option<?>, ?> optionFunc) {
        this.schemaMetaData = schemaMetaData;
        this.tableName = tableName;
        this.comment = comment;
        this.optionFunc = optionFunc;
    }

    @Override
    public SchemaMeta schemaMeta() {
        return this.schemaMetaData;
    }

    @Override
    public String tableName() {
        return this.tableName;
    }

    @Override
    public String comment() {
        return this.comment;
    }

    @Override
    public Set<String> privilegeSet() {
        final String privilegeString;
        privilegeString = valueOf(Option.PRIVILEGE);
        if (privilegeString == null) {
            return Collections.emptySet();
        }
        return JdbdStrings.spitAsSet(privilegeString, ",", true);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T valueOf(Option<T> option) {
        return (T) this.optionFunc.apply(option);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder(256);
        builder.append(getClass().getName())
                .append("[ catalog : '")
                .append(this.schemaMetaData.catalog())
                .append("' , schema : '")
                .append(this.schemaMetaData.schema())
                .append("' , tableName : '")
                .append(this.tableName)
                .append("' , comment : '")
                .append(this.comment)
                .append("' , tableType : '")
                .append(valueOf(Option.TYPE_NAME))
                .append('\'');

        Object optionValue;
        optionValue = this.optionFunc.apply(Option.CHARSET);
        if (optionValue instanceof Charset) {
            builder.append(" , charset : ")
                    .append(((Charset) optionValue).name());
        }

        optionValue = this.optionFunc.apply(Option.COLLATION);
        if (optionValue instanceof String) {
            builder.append(" , collation : '")
                    .append(optionValue)
                    .append('\'');
        }


        return builder.append(" , hash : ")
                .append(System.identityHashCode(this))
                .append(" ]")
                .toString();
    }


}
