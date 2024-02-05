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

package io.jdbd.vendor.result;

import io.jdbd.JdbdException;
import io.jdbd.meta.BooleanMode;
import io.jdbd.meta.DataType;
import io.jdbd.meta.JdbdType;
import io.jdbd.meta.KeyType;
import io.jdbd.result.FieldType;
import io.jdbd.result.ResultRowMeta;
import io.jdbd.session.Option;
import io.jdbd.vendor.util.JdbdCollections;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract class VendorResultRowMeta implements ResultRowMeta {


    public final int resultNo;


    // don't need volatile
    private List<String> labelList;

    protected VendorResultRowMeta(int resultNo) {
        this.resultNo = resultNo;
    }

    @Override
    public final int resultNo() {
        return this.resultNo;
    }

    @Override
    public final <T> T getNonNullOf(int indexBasedZero, Option<T> option) throws JdbdException, NullPointerException {
        final T value;
        value = getOf(indexBasedZero, option);
        if (value == null) {
            String m = String.format("the value of %s is null", option);
            throw new NullPointerException(m);
        }
        return value;
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
    public final KeyType getKeyMode(String columnLabel) throws JdbdException {
        return this.getKeyMode(this.getColumnIndex(columnLabel));
    }

    @Override
    public final BooleanMode getNullableMode(String columnLabel) throws JdbdException {
        return this.getNullableMode(this.getColumnIndex(columnLabel));
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

    @Override
    public final <T> T getOf(String columnLabel, Option<T> option) throws JdbdException {
        return getOf(this.getColumnIndex(columnLabel), option);
    }

    @Override
    public final <T> T getNonNullOf(String columnLabel, Option<T> option) throws JdbdException, NullPointerException {
        return getNonNullOf(this.getColumnIndex(columnLabel), option);
    }

    @Override
    public final List<String> getColumnLabelList() {
        List<String> labelList = this.labelList;
        if (labelList != null) {
            return labelList;
        }
        final ColumnMeta[] columnMetaArray = getColumnMetaArray();
        if (columnMetaArray.length == 1) {
            labelList = Collections.singletonList(columnMetaArray[0].getColumnLabel());
        } else {
            labelList = JdbdCollections.arrayList(columnMetaArray.length);
            for (ColumnMeta meta : columnMetaArray) {
                labelList.add(meta.getColumnLabel());
            }
            labelList = Collections.unmodifiableList(labelList);
        }
        this.labelList = labelList;
        return labelList;
    }


    protected abstract ColumnMeta[] getColumnMetaArray();

    /**
     * @return a unmodifiable map
     */
    protected static Map<String, Integer> createLabelToIndexMap(final ColumnMeta[] columnMetaArray) {
        final Map<String, Integer> map;
        if (columnMetaArray.length == 1) {
            map = Collections.singletonMap(columnMetaArray[0].getColumnLabel(), 0);
        } else {
            Map<String, Integer> tempMap = JdbdCollections.hashMap((int) (columnMetaArray.length / 0.75f));
            for (int i = 0; i < columnMetaArray.length; i++) {
                tempMap.put(columnMetaArray[i].getColumnLabel(), i); // override , if duplication
            }
            map = Collections.unmodifiableMap(tempMap);
        }
        return map;
    }


    protected static JdbdException createNotFoundIndexException(final String columnLabel) {
        String m = String.format("Not found column index for column label[%s]", columnLabel);
        return new JdbdException(m);
    }


}
