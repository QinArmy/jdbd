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

package io.jdbd.vendor.util;

import io.jdbd.lang.Nullable;
import io.jdbd.session.XaException;
import io.jdbd.session.Xid;

public abstract class XaUtils {

    protected XaUtils() {
        throw new UnsupportedOperationException();
    }


    @Nullable
    public static XaException checkXid(final @Nullable Xid xid) {
        final XaException error;
        final String bqual;
        if (xid == null) {
            error = JdbdExceptions.xidIsNull();
        } else if (!JdbdStrings.hasText(xid.getGtrid())) {
            error = JdbdExceptions.xaGtridNoText();
        } else if ((bqual = xid.getBqual()) != null && !JdbdStrings.hasText(bqual)) {
            error = JdbdExceptions.xaBqualNonNullAndNoText();
        } else {
            error = null;
        }
        return error;
    }


}
