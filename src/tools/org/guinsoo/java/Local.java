/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.java;

/**
 * This annotation marks fields that are not shared and therefore don't need to
 * be garbage collected separately.
 */
public @interface Local {
    // empty
}
