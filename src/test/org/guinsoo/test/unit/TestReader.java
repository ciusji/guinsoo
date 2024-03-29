/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.test.unit;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;

import org.guinsoo.dev.util.ReaderInputStream;
import org.guinsoo.test.TestBase;
import org.guinsoo.util.IOUtils;

/**
 * Tests the stream to UTF-8 reader conversion.
 */
public class TestReader extends TestBase {

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
        String s = "\u00ef\u00f6\u00fc";
        StringReader r = new StringReader(s);
        InputStream in = new ReaderInputStream(r);
        byte[] buff = IOUtils.readBytesAndClose(in, 0);
        InputStream in2 = new ByteArrayInputStream(buff);
        Reader r2 = IOUtils.getBufferedReader(in2);
        String s2 = IOUtils.readStringAndClose(r2, Integer.MAX_VALUE);
        assertEquals(s, s2);
    }

}
