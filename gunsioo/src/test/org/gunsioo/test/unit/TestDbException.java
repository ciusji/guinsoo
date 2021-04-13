/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://h2database.com/html/license.html).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.test.unit;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.SQLException;

import org.gunsioo.api.ErrorCode;
import org.gunsioo.jdbc.JdbcException;
import org.gunsioo.jdbc.JdbcSQLException;
import org.gunsioo.message.DbException;
import org.gunsioo.test.TestBase;

/**
 * Tests DbException class.
 */
public class TestDbException extends TestBase {

    /**
     * Run just this test.
     *
     * @param a
     *            ignored
     */
    public static void main(String... a) throws Exception {
        TestBase.createCaller().init().testFromMain();
    }

    @Override
    public void test() throws Exception {
        testGetJdbcSQLException();
    }

    private void testGetJdbcSQLException() throws Exception {
        for (Field field : ErrorCode.class.getDeclaredFields()) {
            if (field.getModifiers() == (Modifier.PUBLIC | Modifier.STATIC | Modifier.FINAL)) {
                int errorCode = field.getInt(null);
                SQLException exception = DbException.getJdbcSQLException(errorCode);
                if (exception instanceof JdbcSQLException) {
                    fail("Custom exception expected for " + ErrorCode.class.getName() + '.' + field.getName() + " ("
                            + errorCode + ')');
                }
                if (!(exception instanceof JdbcException)) {
                    fail("Custom exception for " + ErrorCode.class.getName() + '.' + field.getName() + " (" + errorCode
                            + ") should implement JdbcException");
                }
            }
        }
    }

}
