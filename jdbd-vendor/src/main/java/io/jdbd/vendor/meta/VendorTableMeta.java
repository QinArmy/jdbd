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
