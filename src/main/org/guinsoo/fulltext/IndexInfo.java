/*
 * Copyright 2004-2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.fulltext;

/**
 * The settings of one full text search index.
 */
public class IndexInfo {

    /**
     * The index id.
     */
    protected int id;

    /**
     * The schema name.
     */
    protected String schema;

    /**
     * The table name.
     */
    protected String table;

    /**
     * The column indexes of the key columns.
     */
    protected int[] keys;

    /**
     * The column indexes of the index columns.
     */
    protected int[] indexColumns;

    /**
     * The column names.
     */
    protected String[] columns;
}
