/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.index;

import java.util.ArrayList;

import org.gunsioo.engine.SessionLocal;
import org.gunsioo.expression.condition.Comparison;
import org.gunsioo.message.DbException;
import org.gunsioo.result.ResultInterface;
import org.gunsioo.result.Row;
import org.gunsioo.result.SearchRow;
import org.gunsioo.result.SortOrder;
import org.gunsioo.table.Column;
import org.gunsioo.table.IndexColumn;
import org.gunsioo.table.Table;
import org.gunsioo.value.Value;
import org.gunsioo.value.ValueGeometry;
import org.gunsioo.value.ValueNull;

/**
 * The filter used to walk through an index. This class supports IN(..)
 * and IN(SELECT ...) optimizations.
 *
 * @author Thomas Mueller
 * @author Noel Grandin
 * @author Nicolas Fortin, Atelier SIG, IRSTV FR CNRS 24888
 */
public class IndexCursor implements Cursor {

    private SessionLocal session;
    private Index index;
    private Table table;
    private IndexColumn[] indexColumns;
    private boolean alwaysFalse;

    private SearchRow start, end, intersects;
    private Cursor cursor;
    private Column inColumn;
    private int inListIndex;
    private Value[] inList;
    private ResultInterface inResult;

    public IndexCursor() {
    }

    public void setIndex(Index index) {
        this.index = index;
        this.table = index.getTable();
        Column[] columns = table.getColumns();
        indexColumns = new IndexColumn[columns.length];
        IndexColumn[] idxCols = index.getIndexColumns();
        if (idxCols != null) {
            for (int i = 0, len = columns.length; i < len; i++) {
                int idx = index.getColumnIndex(columns[i]);
                if (idx >= 0) {
                    indexColumns[i] = idxCols[idx];
                }
            }
        }
    }

    /**
     * Prepare this index cursor to make a lookup in index.
     *
     * @param s Session.
     * @param indexConditions Index conditions.
     */
    public void prepare(SessionLocal s, ArrayList<IndexCondition> indexConditions) {
        session = s;
        alwaysFalse = false;
        start = end = null;
        inList = null;
        inColumn = null;
        inResult = null;
        intersects = null;
        for (IndexCondition condition : indexConditions) {
            if (condition.isAlwaysFalse()) {
                alwaysFalse = true;
                break;
            }
            // If index can perform only full table scan do not try to use it for regular
            // lookups, each such lookup will perform an own table scan.
            if (index.isFindUsingFullTableScan()) {
                continue;
            }
            Column column = condition.getColumn();
            if (condition.getCompareType() == Comparison.IN_LIST) {
                if (start == null && end == null) {
                    if (canUseIndexForIn(column)) {
                        this.inColumn = column;
                        inList = condition.getCurrentValueList(s);
                        inListIndex = 0;
                    }
                }
            } else if (condition.getCompareType() == Comparison.IN_QUERY) {
                if (start == null && end == null) {
                    if (canUseIndexForIn(column)) {
                        this.inColumn = column;
                        inResult = condition.getCurrentResult();
                    }
                }
            } else {
                Value v = condition.getCurrentValue(s);
                boolean isStart = condition.isStart();
                boolean isEnd = condition.isEnd();
                boolean isIntersects = condition.isSpatialIntersects();
                int columnId = column.getColumnId();
                if (columnId != SearchRow.ROWID_INDEX) {
                    IndexColumn idxCol = indexColumns[columnId];
                    if (idxCol != null && (idxCol.sortType & SortOrder.DESCENDING) != 0) {
                        // if the index column is sorted the other way, we swap
                        // end and start NULLS_FIRST / NULLS_LAST is not a
                        // problem, as nulls never match anyway
                        boolean temp = isStart;
                        isStart = isEnd;
                        isEnd = temp;
                    }
                }
                if (isStart) {
                    start = getSearchRow(start, columnId, v, true);
                }
                if (isEnd) {
                    end = getSearchRow(end, columnId, v, false);
                }
                if (isIntersects) {
                    intersects = getSpatialSearchRow(intersects, columnId, v);
                }
                // An X=? condition will produce less rows than
                // an X IN(..) condition, unless the X IN condition can use the index.
                if ((isStart || isEnd) && !canUseIndexFor(inColumn)) {
                    inColumn = null;
                    inList = null;
                    inResult = null;
                }
            }
        }
        if (inColumn != null) {
            start = table.getTemplateRow();
        }
    }

    /**
     * Re-evaluate the start and end values of the index search for rows.
     *
     * @param s the session
     * @param indexConditions the index conditions
     */
    public void find(SessionLocal s, ArrayList<IndexCondition> indexConditions) {
        prepare(s, indexConditions);
        if (inColumn != null) {
            return;
        }
        if (!alwaysFalse) {
            if (intersects != null && index instanceof SpatialIndex) {
                cursor = ((SpatialIndex) index).findByGeometry(session, start, end, intersects);
            } else if (index != null) {
                // ???
                cursor = index.find(session, start, end);
            }
        }
    }

    private boolean canUseIndexForIn(Column column) {
        if (inColumn != null) {
            // only one IN(..) condition can be used at the same time
            return false;
        }
        return canUseIndexFor(column);
    }

    private boolean canUseIndexFor(Column column) {
        // The first column of the index must match this column,
        // or it must be a VIEW index (where the column is null).
        // Multiple IN conditions with views are not supported, see
        // IndexCondition.getMask.
        IndexColumn[] cols = index.getIndexColumns();
        if (cols == null) {
            return true;
        }
        IndexColumn idxCol = cols[0];
        return idxCol == null || idxCol.column == column;
    }

    private SearchRow getSpatialSearchRow(SearchRow row, int columnId, Value v) {
        if (row == null) {
            row = table.getTemplateRow();
        } else if (row.getValue(columnId) != null) {
            // if an object needs to overlap with both a and b,
            // then it needs to overlap with the union of a and b
            // (not the intersection)
            ValueGeometry vg = row.getValue(columnId).convertToGeometry(null);
            v = v.convertToGeometry(null).getEnvelopeUnion(vg);
        }
        if (columnId == SearchRow.ROWID_INDEX) {
            row.setKey(v.getLong());
        } else {
            row.setValue(columnId, v);
        }
        return row;
    }

    private SearchRow getSearchRow(SearchRow row, int columnId, Value v, boolean max) {
        if (row == null) {
            row = table.getTemplateRow();
        } else {
            v = getMax(row.getValue(columnId), v, max);
        }
        if (columnId == SearchRow.ROWID_INDEX) {
            row.setKey(v.getLong());
        } else {
            row.setValue(columnId, v);
        }
        return row;
    }

    private Value getMax(Value a, Value b, boolean bigger) {
        if (a == null) {
            return b;
        } else if (b == null) {
            return a;
        }
        // IS NULL must be checked later
        if (a == ValueNull.INSTANCE) {
            return b;
        } else if (b == ValueNull.INSTANCE) {
            return a;
        }
        int comp = session.compare(a, b);
        if (comp == 0) {
            return a;
        }
        return (comp > 0) == bigger ? a : b;
    }

    /**
     * Check if the result is empty for sure.
     *
     * @return true if it is
     */
    public boolean isAlwaysFalse() {
        return alwaysFalse;
    }

    /**
     * Get start search row.
     *
     * @return search row
     */
    public SearchRow getStart() {
        return start;
    }

    /**
     * Get end search row.
     *
     * @return search row
     */
    public SearchRow getEnd() {
        return end;
    }

    @Override
    public Row get() {
        if (cursor == null) {
            return null;
        }
        return cursor.get();
    }

    @Override
    public SearchRow getSearchRow() {
        return cursor.getSearchRow();
    }

    @Override
    public boolean next() {
        while (true) {
            if (cursor == null) {
                nextCursor();
                if (cursor == null) {
                    return false;
                }
            }
            if (cursor.next()) {
                return true;
            }
            cursor = null;
        }
    }

    private void nextCursor() {
        if (inList != null) {
            while (inListIndex < inList.length) {
                Value v = inList[inListIndex++];
                if (v != ValueNull.INSTANCE) {
                    find(v);
                    break;
                }
            }
        } else if (inResult != null) {
            while (inResult.next()) {
                Value v = inResult.currentRow()[0];
                if (v != ValueNull.INSTANCE) {
                    find(v);
                    break;
                }
            }
        }
    }

    private void find(Value v) {
        v = inColumn.convert(session, v);
        int id = inColumn.getColumnId();
        start.setValue(id, v);
        cursor = index.find(session, start, start);
    }

    @Override
    public boolean previous() {
        throw DbException.getInternalError(toString());
    }

}