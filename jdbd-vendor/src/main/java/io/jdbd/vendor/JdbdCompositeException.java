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

package io.jdbd.vendor;

import io.jdbd.JdbdException;
import io.jdbd.lang.Nullable;
import io.jdbd.vendor.util.JdbdCollections;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

public class JdbdCompositeException extends JdbdException {

    private final List<? extends Throwable> errorList;

    public JdbdCompositeException(List<? extends Throwable> errorList) {
        super(createErrorMessage(errorList));
        this.errorList = JdbdCollections.unmodifiableList(errorList);
    }

    /**
     * @return a unmodifiable list.
     */
    public List<? extends Throwable> getErrorList() {
        return this.errorList;
    }


    @Override
    public final void printStackTrace() {
        printStackTrace(System.err);
    }


    @Override
    public void printStackTrace(PrintWriter s) {
        super.printStackTrace(s);
    }

    @Nullable
    protected static Throwable getFirstError(List<? extends Throwable> errorList) {
        return errorList.isEmpty() ? null : errorList.get(0);
    }

    protected static String createErrorMessage(List<? extends Throwable> errorList) {
        if (errorList.isEmpty()) {
            throw new IllegalArgumentException("errorList is empty.");
        }
        String message;
        try (StringWriter stringWriter = new StringWriter(); PrintWriter writer = new PrintWriter(stringWriter)) {

            for (Throwable e : errorList) {
                e.printStackTrace(writer);
            }
            message = stringWriter.toString();
        } catch (IOException e) {
            message = String.format("print error stack trace failure,first error message:%s", errorList.get(0));
        }
        return message;
    }


}
