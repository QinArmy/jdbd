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

package io.jdbd.session;

import io.jdbd.lang.Nullable;
import io.jdbd.util.JdbdUtils;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

final class JdbdXid implements Xid {

    static JdbdXid from(final String gtrid, final @Nullable String bqual, final int formatId) {
        if (JdbdUtils.hasNoText(gtrid)) {
            throw new IllegalArgumentException("gtrid must have text");
        } else if (bqual != null && JdbdUtils.hasNoText(gtrid)) {
            throw new IllegalArgumentException("bqual must be null or  have text");
        }
        return new JdbdXid(gtrid, bqual, formatId);
    }

    private final String gtrid;

    private final String bqual;

    private final int formatId;

    private JdbdXid(String gtrid, @Nullable String bqual, int formatId) {
        this.gtrid = gtrid;
        this.bqual = bqual;
        this.formatId = formatId;
    }

    @Override
    public String getGtrid() {
        return this.gtrid;
    }

    @Override
    public String getBqual() {
        return this.bqual;
    }

    @Override
    public int getFormatId() {
        return this.formatId;
    }

    @Override
    public <T> T valueOf(Option<T> option) {
        // always null
        return null;
    }

    @Override
    public <T> T nonNullOf(Option<T> option) {
        throw new NullPointerException();
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.gtrid, this.bqual, this.formatId);
    }

    @Override
    public boolean equals(final Object obj) {
        final boolean match;
        if (obj == this) {
            match = true;
        } else if (obj instanceof Xid) {
            final Xid o = (Xid) obj;
            match = this.gtrid.equals(o.getGtrid())
                    && Objects.equals(o.getBqual(), this.bqual)
                    && o.getFormatId() == this.formatId;
        } else {
            match = false;
        }
        return match;
    }


    @Override
    public Set<Option<?>> optionSet() {
        return Collections.emptySet();
    }

    @Override
    public String toString() {
        return String.format("%s[ gtrid : %s , bqual : %s , formatId : %s , hash : %s ]",
                getClass().getName(),
                this.gtrid,
                this.bqual,
                this.formatId,
                System.identityHashCode(this)
        );
    }


}
