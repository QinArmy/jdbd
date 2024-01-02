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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.util.BitSet;
import java.util.Random;

import static org.testng.Assert.assertEquals;

/**
 * This class is a test class of {@link JdbdStrings}
 */
public class JdbdStringsSuiteTests {

    private static final Logger LOG = LoggerFactory.getLogger(JdbdStringsSuiteTests.class);


    /**
     * @see JdbdStrings#bitSetToBitString(BitSet, boolean)
     */
    @Test
    public void bitSetToBitString() {
        BitSet bitSet;
        String bitString, original, reverseOriginal;

        final Random random = new Random();
        long next;
        for (int i = 0; i < 100; i++) {
            next = random.nextLong();
            original = Long.toBinaryString(next);
            reverseOriginal = new StringBuilder(original).reverse().toString();

            bitSet = BitSet.valueOf(new long[]{next});

            bitString = JdbdStrings.bitSetToBitString(bitSet, false);
            assertEquals(bitString, reverseOriginal, original);

            bitString = JdbdStrings.bitSetToBitString(bitSet, true);
            assertEquals(bitString, original, original);

        }


    }

    /**
     * @see JdbdStrings#bitStringToBitSet(String, boolean)
     */
    @Test
    public void bitStringToBitSet() {
        BitSet bitSet;
        String original, reverseOriginal;

        final Random random = new Random();
        long next;
        for (int i = 0; i < 100; i++) {
            next = random.nextLong();
            original = Long.toBinaryString(next);
            reverseOriginal = new StringBuilder(original).reverse().toString();

            bitSet = JdbdStrings.bitStringToBitSet(original, true);
            assertEquals(bitSet, BitSet.valueOf(new long[]{next}), original);

            bitSet = JdbdStrings.bitStringToBitSet(reverseOriginal, false);
            assertEquals(bitSet, BitSet.valueOf(new long[]{next}), original);

        }

    }

    /**
     * @see JdbdStrings#bitStringToBitSet(String, boolean)
     * @see JdbdStrings#bitSetToBitString(BitSet, boolean)
     */
    @Test(dependsOnMethods = {"bitSetToBitString", "bitStringToBitSet"})
    public void bitStringAndBitSetConvert() {
        BitSet bitSet;
        String bitString, original, reverseOriginal;

        final Random random = new Random();
        long next;
        for (int i = 0; i < 100; i++) {
            next = random.nextLong();
            original = Long.toBinaryString(next);
            reverseOriginal = new StringBuilder(original).reverse().toString();

            bitSet = JdbdStrings.bitStringToBitSet(original, true);

            bitString = JdbdStrings.bitSetToBitString(bitSet, true);
            assertEquals(bitString, original, original);

            bitString = JdbdStrings.bitSetToBitString(bitSet, false);
            assertEquals(bitString, reverseOriginal, original);

        }

    }


}
