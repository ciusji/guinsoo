/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.test.db;

import java.io.Serializable;

/**
 * A utility class for TestLob.
 */
class TestLobObject implements Serializable {

    private static final long serialVersionUID = 1L;
    String data;

    TestLobObject(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "TestLobObject: " + data;
    }
}
