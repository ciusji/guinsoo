/*
 * Copyright 2004-2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.util.json;

/**
 * JSON validation target.
 */
public abstract class JSONValidationTarget extends JSONTarget<JSONItemType> {

    /**
     * @return JSON item type of the top-level item, may not return
     *         {@link JSONItemType#VALUE}
     */
    @Override
    public abstract JSONItemType getResult();

}