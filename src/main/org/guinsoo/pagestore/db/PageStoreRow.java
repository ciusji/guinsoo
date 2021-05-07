/*
 * Copyright 2004-2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.pagestore.db;

import org.guinsoo.engine.Constants;
import org.guinsoo.message.DbException;
import org.guinsoo.result.Row;
import org.guinsoo.result.SearchRow;
import org.guinsoo.value.Value;
import org.guinsoo.value.ValueBigint;

/**
 * Page Store implementation of a row.
 */
public final class PageStoreRow {

    /**
     * An empty array of Row objects.
     */
    static final Row[] EMPTY_ARRAY = new Row[0];

    /**
     * An empty array of SearchRow objects.
     */
    static final SearchRow[] EMPTY_SEARCH_ARRAY = new SearchRow[0];

    /**
     * The implementation of a removed row in an in-memory table.
     */
    static final class RemovedRow extends Row {

        RemovedRow(long key) {
            setKey(key);
        }

        @Override
        public Value getValue(int i) {
            if (i == ROWID_INDEX) {
                return ValueBigint.get(key);
            }
            throw DbException.getInternalError();
        }

        @Override
        public void setValue(int i, Value v) {
            if (i == ROWID_INDEX) {
                key = v.getLong();
            } else {
                throw DbException.getInternalError();
            }
        }

        @Override
        public int getColumnCount() {
            return 0;
        }

        @Override
        public String toString() {
            return "( /* key:" + key + " */ )";
        }

        @Override
        public int getMemory() {
            return Constants.MEMORY_ROW;
        }

        @Override
        public Value[] getValueList() {
            return null;
        }

        @Override
        public void copyFrom(SearchRow source) {
            setKey(source.getKey());
        }
    }

    private PageStoreRow() {
    }

}
