package io.jdbd.vendor.meta;

import io.jdbd.meta.IndexColumnMeta;
import io.jdbd.meta.KeyType;
import io.jdbd.meta.TableIndexMeta;
import io.jdbd.meta.TableMeta;
import io.jdbd.session.Option;
import io.jdbd.vendor.VendorOptions;
import io.jdbd.vendor.util.JdbdCollections;

import java.util.List;
import java.util.function.Function;

public final class VendorTableIndexMeta implements TableIndexMeta {

    /**
     * <p>
     * optionFunc must support following option:
     *     <ul>
     *         <li>{@link VendorOptions#INDEX_TYPE},see {@link #indexType()}</li>
     *         <li>{@link VendorOptions#KEY_TYPE},see {@link #keyType()}</li>
     *         <li>{@link Option#UNIQUE},see {@link #isUnique()}</li>
     *         <li>{@link VendorOptions#COMMENT},see {@link #comment()}</li>
     *     </ul>
     * </p>
     *
     * @param name index name
     */
    public static VendorTableIndexMeta from(TableMeta tableMeta, String name, List<IndexColumnMeta> columnList,
                                            Function<Option<?>, ?> optionFunc) {
        return new VendorTableIndexMeta(tableMeta, name, columnList, optionFunc);
    }


    private final TableMeta tableMeta;

    private final String name;

    private final List<IndexColumnMeta> columnList;
    private final Function<Option<?>, ?> optionFunc;

    private VendorTableIndexMeta(TableMeta tableMeta, String name, List<IndexColumnMeta> columnList,
                                 Function<Option<?>, ?> optionFunc) {
        this.tableMeta = tableMeta;
        this.name = name;
        this.columnList = JdbdCollections.unmodifiableList(columnList);
        this.optionFunc = optionFunc;
    }

    @Override
    public TableMeta tableMeta() {
        return this.tableMeta;
    }

    @Override
    public String indexName() {
        return this.name;
    }

    @Override
    public String indexType() {
        return nonNullOf(VendorOptions.INDEX_TYPE);
    }

    @Override
    public KeyType keyType() {
        return nonNullOf(VendorOptions.KEY_TYPE);
    }

    @Override
    public boolean isUnique() {
        return nonNullOf(Option.UNIQUE);
    }

    @Override
    public List<IndexColumnMeta> indexColumnList() {
        return this.columnList;
    }

    @Override
    public String comment() {
        return valueOf(VendorOptions.COMMENT);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T valueOf(Option<T> option) {
        final Object value;
        if (option == Option.NAME) {
            value = this.name;
        } else {
            value = this.optionFunc.apply(option);
        }
        return (T) value;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder(690);
        builder.append(getClass().getName())
                .append("[ catalog : '")
                .append(this.tableMeta.schemaMeta().catalog())
                .append("' , schema : '")
                .append(this.tableMeta.schemaMeta().schema())
                .append("' , tableName : '")
                .append(this.tableMeta.tableName())
                .append("' , indexName : '")
                .append(this.name)
                .append("' , indexType : '")
                .append(this.indexType())
                .append("' , keyType : ")
                .append(this.keyType())
                .append(" , isUnique : ")
                .append(this.isUnique())
                .append(" , indexColumnList : ")
                .append(this.columnList)
                .append(" , comment : ");

        final String commentText;
        commentText = comment();
        if (commentText != null) {
            builder.append('\'');
        }
        builder.append(commentText);
        if (commentText != null) {
            builder.append('\'');
        }

        return builder.append(" , hash : ")
                .append(System.identityHashCode(this))
                .append(" ]")
                .toString();
    }


}
