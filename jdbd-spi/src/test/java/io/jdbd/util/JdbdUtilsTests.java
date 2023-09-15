package io.jdbd.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.charset.StandardCharsets;

/**
 * This class is the test class of {@link JdbdUtils}
 */
public class JdbdUtilsTests {

    private static final Logger LOG = LoggerFactory.getLogger(JdbdUtilsTests.class);

    /**
     * This method is test of {@link JdbdUtils#hexEscapesText(boolean, byte[])}
     */
    @Test
    public void hexEscapesText() {
        final String text, hexString;
        text = "中国QinArmy's jdbd";

        hexString = JdbdUtils.hexEscapesText(true, text.getBytes(StandardCharsets.UTF_8));
        Assert.assertEquals(JdbdUtils.decodeHexAsString(hexString), text);

    }

}
