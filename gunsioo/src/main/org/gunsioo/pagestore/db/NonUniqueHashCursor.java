/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.pagestore.db;

import java.util.ArrayList;
import org.gunsioo.engine.SessionLocal;
import org.gunsioo.index.Cursor;
import org.gunsioo.result.Row;
import org.gunsioo.result.SearchRow;

/**
 * Cursor implementation for non-unique hash index
 *
 * @author Sergi Vladykin
 */
public class NonUniqueHashCursor implements Cursor {

    private final SessionLocal session;
    private final ArrayList<Long> positions;
    private final PageStoreTable tableData;

    private int index = -1;

    public NonUniqueHashCursor(SessionLocal session, PageStoreTable tableData,
            ArrayList<Long> positions) {
        this.session = session;
        this.tableData = tableData;
        this.positions = positions;
    }

    @Override
    public Row get() {
        if (index < 0 || index >= positions.size()) {
            return null;
        }
        return tableData.getRow(session, positions.get(index));
    }

    @Override
    public SearchRow getSearchRow() {
        return get();
    }

    @Override
    public boolean next() {
        return positions != null && ++index < positions.size();
    }

    @Override
    public boolean previous() {
        return positions != null && --index >= 0;
    }

}
