/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://h2database.com/html/license.html).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.util.json;

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
