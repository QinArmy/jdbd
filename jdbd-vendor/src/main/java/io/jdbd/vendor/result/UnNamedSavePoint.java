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
 * Non-named {@link SavePoint}
 *
 * @see NamedSavePoint
 * @since 1.0
 */
public final class UnNamedSavePoint implements SavePoint {

    public static SavePoint fromId(int id) {
        return new UnNamedSavePoint(id);
    }

    private final int id;

    private UnNamedSavePoint(int id) {
        this.id = id;
    }

    @Override
    public boolean isNamed() {
        return false;
    }

    @Override
    public boolean isIdType() {
        return true;
    }

    @Override
    public int id() throws JdbdException {
        return this.id;
    }

    @Override
    public String name() throws JdbdException {
        throw new JdbdException("this is un-named save point");
    }

    @Override
    public int hashCode() {
        return this.id;
    }

    @Override
    public boolean equals(final Object obj) {
        final boolean match;
        if (obj == this) {
            match = true;
        } else if (obj instanceof UnNamedSavePoint) {
            match = ((UnNamedSavePoint) obj).id == this.id;
        } else {
            match = false;
        }
        return match;
    }

    @Override
    public String toString() {
        return JdbdStrings.builder()
                .append(UnNamedSavePoint.class.getName())
                .append("[ id : ")
                .append(this.id)
                .append(" , hash : ")
                .append(System.identityHashCode(this))
                .append(" ]")
                .toString();
    }


}//UnNamedSavePoint
