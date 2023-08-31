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
