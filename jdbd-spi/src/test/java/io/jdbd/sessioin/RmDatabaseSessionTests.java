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

package io.jdbd.sessioin;

import io.jdbd.session.RmDatabaseSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.transaction.xa.XAResource;

public class RmDatabaseSessionTests {

    private static final Logger LOG = LoggerFactory.getLogger(RmDatabaseSessionTests.class);

    @Test
    public void xaFlags() {
        Assert.assertEquals(RmDatabaseSession.TM_NO_FLAGS, XAResource.TMNOFLAGS);
        Assert.assertEquals(RmDatabaseSession.TM_JOIN, XAResource.TMJOIN);
        Assert.assertEquals(RmDatabaseSession.TM_END_RSCAN, XAResource.TMENDRSCAN);
        Assert.assertEquals(RmDatabaseSession.TM_START_RSCAN, XAResource.TMSTARTRSCAN);

        Assert.assertEquals(RmDatabaseSession.TM_SUSPEND, XAResource.TMSUSPEND);
        Assert.assertEquals(RmDatabaseSession.TM_SUCCESS, XAResource.TMSUCCESS);
        Assert.assertEquals(RmDatabaseSession.TM_RESUME, XAResource.TMRESUME);
        Assert.assertEquals(RmDatabaseSession.TM_FAIL, XAResource.TMFAIL);

        Assert.assertEquals(RmDatabaseSession.TM_ONE_PHASE, XAResource.TMONEPHASE);
        Assert.assertEquals(RmDatabaseSession.XA_RDONLY, XAResource.XA_RDONLY);
        Assert.assertEquals(RmDatabaseSession.XA_OK, XAResource.XA_OK);


    }

}
