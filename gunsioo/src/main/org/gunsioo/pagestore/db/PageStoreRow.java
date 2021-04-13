/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://h2database.com/html/license.html).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.pagestore.db;

import org.gunsioo.engine.Constants;
import org.gunsioo.message.DbException;
import org.gunsioo.result.Row;
import org.gunsioo.result.SearchRow;
import org.gunsioo.value.Value;
import org.gunsioo.value.ValueBigint;

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
