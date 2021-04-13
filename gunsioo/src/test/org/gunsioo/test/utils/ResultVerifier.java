/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.test.utils;

import java.lang.reflect.Method;

/**
 * This handler is called after a method returned.
 */
public interface ResultVerifier {

    /**
     * Verify the result or exception.
     *
     * @param returnValue the returned value or null
     * @param t the exception / error or null if the method returned normally
     * @param m the method or null if unknown
     * @param args the arguments or null if unknown
     * @return true if the method should be called again
     */
    boolean verify(Object returnValue, Throwable t, Method m, Object... args);

}
