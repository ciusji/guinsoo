/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.util.json;

import java.util.ArrayList;

/**
 * JSON array.
 */
public class JSONArray extends JSONValue {

    private final ArrayList<JSONValue> elements = new ArrayList<>();

    JSONArray() {
    }

    /**
     * Add a value to the array.
     *
     * @param value the value to add
     */
    void addElement(JSONValue value) {
        elements.add(value);
    }

    @Override
    public void addTo(JSONTarget<?> target) {
        target.startArray();
        for (JSONValue element : elements) {
            element.addTo(target);
        }
        target.endArray();
    }

    /**
     * Returns the value.
     *
     * @return the value
     */
    public JSONValue[] getArray() {
        return elements.toArray(new JSONValue[0]);
    }

}
