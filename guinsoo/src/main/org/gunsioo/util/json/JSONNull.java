/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.util.json;

/**
 * JSON null.
 */
public class JSONNull extends JSONValue {

    /**
     * {@code null} value.
     */
    public static final JSONNull NULL = new JSONNull();

    private JSONNull() {
    }

    @Override
    public void addTo(JSONTarget<?> target) {
        target.valueNull();
    }

}
