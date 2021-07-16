/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.util.json;

/**
 * JSON value.
 */
public abstract class JSONValue {

    JSONValue() {
    }

    /**
     * Appends this value to the specified target.
     *
     * @param target
     *            the target
     */
    public abstract void addTo(JSONTarget<?> target);

    @Override
    public final String toString() {
        JSONStringTarget target = new JSONStringTarget();
        addTo(target);
        return target.getResult();
    }

}
