/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://h2database.com/html/license.html).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.util.json;

import java.math.BigDecimal;

/**
 * JSON number.
 */
public class JSONNumber extends JSONValue {

    private final BigDecimal value;

    JSONNumber(BigDecimal value) {
        this.value = value;
    }

    @Override
    public void addTo(JSONTarget<?> target) {
        target.valueNumber(value);
    }

    /**
     * Returns the value.
     *
     * @return the value
     */
    public BigDecimal getBigDecimal() {
        return value;
    }

}
