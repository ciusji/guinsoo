/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.build.indexer;

/**
 * Represents a weight of a token in a page.
 */
public class Weight {

    /**
     * The weight of a word in a title.
     */
    static final int TITLE = 10000;

    /**
     * The weight of a word in the header.
     */
    static final int HEADER = 100;

    /**
     * The weight of a word in a paragraph.
     */
    static final int PARAGRAPH = 1;

    /**
     * The page referenced.
     */
    Page page;

    /**
     * The weight value.
     */
    int value;

    @Override
    public String toString() {
        return "" + value;
    }

}
