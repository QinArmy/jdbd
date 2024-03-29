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
import io.jdbd.meta.DatabaseMetaData;
import io.jdbd.meta.SchemaMeta;
import io.jdbd.session.DatabaseSession;
import io.jdbd.session.Option;
import io.jdbd.util.JdbdUtils;
import io.jdbd.vendor.util.JdbdOptionSpec;

import java.util.Map;

public final class VendorSchemaMeta extends JdbdOptionSpec implements SchemaMeta {

    public static VendorSchemaMeta from(DatabaseMetaData databaseMeta, String catalogName,
                                        String schemaName, Map<Option<?>, ?> optionMap) {
        return new VendorSchemaMeta(databaseMeta, catalogName, schemaName, null, optionMap);
    }

    public static VendorSchemaMeta fromCatalog(DatabaseMetaData databaseMeta, String catalogName,
                                               String schemaName, Map<Option<?>, ?> optionMap) {
        return new VendorSchemaMeta(databaseMeta, catalogName, schemaName, Boolean.FALSE, optionMap);
    }

    public static VendorSchemaMeta fromSchema(DatabaseMetaData databaseMeta, String catalogName,
                                              String schemaName, Map<Option<?>, ?> optionMap) {
        return new VendorSchemaMeta(databaseMeta, catalogName, schemaName, Boolean.TRUE, optionMap);
    }

    private final DatabaseMetaData databaseMeta;

    private final String catalogName;

    private final String schemaName;

    private final Boolean catalogPseudo;

    private VendorSchemaMeta(DatabaseMetaData databaseMeta, String catalogName,
                             String schemaName, @Nullable Boolean catalogPseudo, Map<Option<?>, ?> optionMap) {
        super(optionMap);
        if (JdbdUtils.hasNoText(catalogName) || JdbdUtils.hasNoText(schemaName)) {
            throw new IllegalArgumentException();
        }
        this.databaseMeta = databaseMeta;
        this.catalogName = catalogName;
        this.schemaName = schemaName;
        this.catalogPseudo = catalogPseudo;
    }

    @Override
    public DatabaseMetaData databaseMetadata() {
        return this.databaseMeta;
    }

    @Override
    public String catalog() {
        return this.catalogName;
    }

    @Override
    public String schema() {
        return this.schemaName;
    }

    @Override
    public boolean isPseudoCatalog() {
        return Boolean.TRUE.equals(this.catalogPseudo);
    }

    @Override
    public boolean isPseudoSchema() {
        return Boolean.FALSE.equals(this.catalogPseudo);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder(190);

        builder.append(getClass().getName())
                .append("[ catalog : '")
                .append(this.catalogName)
                .append("' , schema : '")
                .append(this.schemaName)
                .append("' , pseudoCatalog : ")
                .append(isPseudoCatalog())
                .append(" , pseudoSchema : ")
                .append(isPseudoSchema());

        final DatabaseSession session = this.databaseMeta.getSession();
        if (!session.isClosed()) {
            builder.append(" , sessionIdentifier : ")
                    .append(session.sessionIdentifier());
        }
        return builder.append(" , hash : ")
                .append(System.identityHashCode(this))
                .append(" ]")
                .toString();
    }


}
