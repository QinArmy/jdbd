package io.jdbd.vendor.result;

import io.jdbd.JdbdException;
import io.jdbd.meta.*;
import io.jdbd.result.FieldType;
import io.jdbd.result.ResultRowMeta;

public abstract class VendorResultRowMeta implements ResultRowMeta {


    public final int resultNo;


    protected VendorResultRowMeta(int resultNo) {
        this.resultNo = resultNo;
    }

    @Override
    public final int getResultNo() {
        return this.resultNo;
    }

    @Override
    public final DataType getDataType(String columnLabel) {
        return this.getDataType(this.getColumnIndex(columnLabel));
    }

    @Override
    public final JdbdType getJdbdType(String columnLabel) {
        return this.getJdbdType(this.getColumnIndex(columnLabel));
    }

    @Override
    public final FieldType getFieldType(String columnLabel) {
        return this.getFieldType(this.getColumnIndex(columnLabel));
    }

    @Override
    public final int getPrecision(String columnLabel) throws JdbdException {
        return this.getPrecision(this.getColumnIndex(columnLabel));
    }

    @Override
    public final int getScale(String columnLabel) throws JdbdException {
        return this.getScale(this.getColumnIndex(columnLabel));
    }

    @Override
    public final KeyMode getKeyMode(String columnLabel) throws JdbdException {
        return this.getKeyMode(this.getColumnIndex(columnLabel));
    }

    @Override
    public final NullMode getNullMode(String columnLabel) throws JdbdException {
        return this.getNullMode(this.getColumnIndex(columnLabel));
    }

    @Override
    public final BooleanMode getAutoIncrementMode(String columnLabel) throws JdbdException {
        return this.getAutoIncrementMode(this.getColumnIndex(columnLabel));
    }

    @Override
    public final Class<?> getFirstJavaType(String columnLabel) throws JdbdException {
        return this.getFirstJavaType(this.getColumnIndex(columnLabel));
    }

    @Override
    public final Class<?> getSecondJavaType(String columnLabel) throws JdbdException {
        return this.getSecondJavaType(this.getColumnIndex(columnLabel));
    }

    @Override
    public final String getCatalogName(String columnLabel) throws JdbdException {
        return this.getCatalogName(this.getColumnIndex(columnLabel));
    }

    @Override
    public final String getSchemaName(String columnLabel) throws JdbdException {
        return this.getSchemaName(this.getColumnIndex(columnLabel));
    }

    @Override
    public final String getTableName(String columnLabel) throws JdbdException {
        return this.getTableName(this.getColumnIndex(columnLabel));
    }

    @Override
    public final String getColumnName(String columnLabel) throws JdbdException {
        return this.getColumnName(this.getColumnIndex(columnLabel));
    }


}
