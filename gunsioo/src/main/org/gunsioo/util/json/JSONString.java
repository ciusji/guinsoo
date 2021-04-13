/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://h2database.com/html/license.html).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.util.json;

/**
 * JSON string.
 */
public class JSONString extends JSONValue {

    private final String value;

    JSONString(String value) {
        this.value = value;
    }

    @Override
    public void addTo(JSONTarget<?> target) {
        target.valueString(value);
    }

    /**
     * Returns the value.
     *
     * @return the value
     */
    public String getString() {
        return value;
    }

}
