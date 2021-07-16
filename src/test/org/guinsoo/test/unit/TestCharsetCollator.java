/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.test.unit;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.text.Collator;
import org.guinsoo.test.TestBase;
import org.guinsoo.value.CharsetCollator;
import org.guinsoo.value.CompareMode;

/**
 * Unittest for org.guinsoo.value.CharsetCollator
 */
public class TestCharsetCollator extends TestBase {
    private CharsetCollator cp500Collator = new CharsetCollator(Charset.forName("cp500"));
    private CharsetCollator utf8Collator = new CharsetCollator(StandardCharsets.UTF_8);

    /**
     * Run just this test.
     *
     * @param a ignored
     */
    public static void main(String... a) throws Exception {
        TestBase.createCaller().init().testFromMain();
    }


    @Override
    public void test() throws Exception {
        testBasicComparison();
        testNumberToCharacterComparison();
        testLengthComparison();
        testCreationFromCompareMode();
        testCreationFromCompareModeWithInvalidCharset();
        testCaseInsensitive();
    }

    private void testCreationFromCompareModeWithInvalidCharset() {
        assertThrows(UnsupportedCharsetException.class, () -> CompareMode.getCollator("CHARSET_INVALID"));
    }

    private void testCreationFromCompareMode() {
        Collator utf8Col = CompareMode.getCollator("CHARSET_UTF-8");
        assertTrue(utf8Col instanceof CharsetCollator);
        assertEquals(((CharsetCollator) utf8Col).getCharset(), StandardCharsets.UTF_8);
    }

    private void testBasicComparison() {
        assertTrue(cp500Collator.compare("A", "B") < 0);
        assertTrue(cp500Collator.compare("AA", "AB") < 0);
    }

    private void testLengthComparison() {
        assertTrue(utf8Collator.compare("AA", "A") > 0);
    }

    private void testNumberToCharacterComparison() {
        assertTrue(cp500Collator.compare("A", "1") < 0);
        assertTrue(utf8Collator.compare("A", "1") > 0);
    }

    private void testCaseInsensitive() {
        CharsetCollator c = new CharsetCollator(StandardCharsets.UTF_8);
        c.setStrength(Collator.SECONDARY);
        assertEquals(0, c.compare("a", "A"));
    }

}
