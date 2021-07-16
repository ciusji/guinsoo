/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.test;

import org.junit.jupiter.api.Test;

/**
 * This class is a bridge between JUnit and the custom test framework
 * used by H2.
 */
public class TestAllJunit {

    /**
     * Run all the fast tests.
     */
    @Test
    public void testTravis() throws Exception {
        TestAll.main("travis");
    }
}
