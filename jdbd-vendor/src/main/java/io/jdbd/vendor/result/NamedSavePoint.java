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
import io.jdbd.session.SavePoint;
import io.jdbd.vendor.util.JdbdStrings;

/**
 * named {@link SavePoint}
 *
 * @see UnNamedSavePoint
 * @since 1.0
 */
public final class NamedSavePoint implements SavePoint {

    public static SavePoint fromName(String name) {
        return new NamedSavePoint(name);
    }

    private final String name;

    private NamedSavePoint(String name) {
        this.name = name;
    }

    @Override
    public boolean isNamed() {
        return true;
    }

    @Override
    public boolean isIdType() {
        return false;
    }

    @Override
    public int id() throws JdbdException {
        throw new JdbdException("this is named save point");
    }

    @Override
    public String name() throws JdbdException {
        return this.name;
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        final boolean match;
        if (obj == this) {
            match = true;
        } else if (obj instanceof NamedSavePoint) {
            match = ((NamedSavePoint) obj).name.equals(this.name);
        } else {
            match = false;
        }
        return match;
    }

    @Override
    public String toString() {
        return JdbdStrings.builder()
                .append(NamedSavePoint.class.getName())
                .append("[ name : ")
                .append(this.name)
                .append(" , hash : ")
                .append(System.identityHashCode(this))
                .append(" ]")
                .toString();
    }


}//NamedSavePoint
