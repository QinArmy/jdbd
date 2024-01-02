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

package io.jdbd.vendor.task;

import org.ietf.jgss.GSSContext;
import reactor.util.annotation.Nullable;

import javax.security.auth.login.LoginContext;

public final class GssWrapper {


    public static GssWrapper wrap(@Nullable LoginContext loginContext, GSSContext gssContext) {
        return new GssWrapper(loginContext, gssContext);
    }


    private final LoginContext loginContext;

    private final GSSContext gssContext;

    private GssWrapper(@Nullable LoginContext loginContext, GSSContext gssContext) {
        this.loginContext = loginContext;
        this.gssContext = gssContext;
    }

    @Nullable
    public final LoginContext getLoginContext() {
        return this.loginContext;
    }

    public final GSSContext getGssContext() {
        return this.gssContext;
    }
}
