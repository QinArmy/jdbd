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
