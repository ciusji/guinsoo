/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.index;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.guinsoo.command.Parser;
import org.guinsoo.command.query.AllColumnsForPlan;
import org.guinsoo.command.query.Query;
import org.guinsoo.command.query.SelectUnion;
import org.guinsoo.engine.Constants;
import org.guinsoo.engine.SessionLocal;
import org.guinsoo.expression.Parameter;
import org.guinsoo.message.DbException;
import org.guinsoo.table.Column;
import org.guinsoo.table.IndexColumn;
import org.guinsoo.table.TableFilter;
import org.guinsoo.table.TableView;
import org.guinsoo.value.Value;
import org.guinsoo.api.ErrorCode;
import org.guinsoo.expression.condition.Comparison;
import org.guinsoo.result.LocalResult;
import org.guinsoo.result.ResultInterface;
import org.guinsoo.result.Row;
import org.guinsoo.result.SearchRow;
import org.guinsoo.result.SortOrder;
import org.guinsoo.util.IntArray;

/**
 * This object represents a virtual index for a query.
 * Actually it only represents a prepared SELECT statement.
 */
public class ViewIndex extends Index implements SpatialIndex {

    private static final long MAX_AGE_NANOS =
            TimeUnit.MILLISECONDS.toNanos(Constants.VIEW_COST_CACHE_MAX_AGE);

    private final TableView view;
    private final String querySQL;
    private final ArrayList<Parameter> originalParameters;
    private boolean recursive;
    private final int[] indexMasks;
    private Query query;
    private final SessionLocal createSession;

    /**
     * The time in nanoseconds when this index (and its cost) was calculated.
     */
    private final long evaluatedAt;

    /**
     * Constructor for the original index in {@link TableView}.
     *
     * @param view the table view
     * @param querySQL the query SQL
     * @param originalParameters the original parameters
     * @param recursive if the view is recursive
     */
    public ViewIndex(TableView view, String querySQL,
            ArrayList<Parameter> originalParameters, boolean recursive) {
        super(view, 0, null, null, IndexType.createNonUnique(false));
        this.view = view;
        this.querySQL = querySQL;
        this.originalParameters = originalParameters;
        this.recursive = recursive;
        columns = new Column[0];
        this.createSession = null;
        this.indexMasks = null;
        // this is a main index of TableView, it does not need eviction time
        // stamp
        evaluatedAt = Long.MIN_VALUE;
    }

    /**
     * Constructor for plan item generation. Over this index the query will be
     * executed.
     *
     * @param view the table view
     * @param index the view index
     * @param session the session
     * @param masks the masks
     * @param filters table filters
     * @param filter current filter
     * @param sortOrder sort order
     */
    public ViewIndex(TableView view, ViewIndex index, SessionLocal session,
                     int[] masks, TableFilter[] filters, int filter, SortOrder sortOrder) {
        super(view, 0, null, null, IndexType.createNonUnique(false));
        this.view = view;
        this.querySQL = index.querySQL;
        this.originalParameters = index.originalParameters;
        this.recursive = index.recursive;
        this.indexMasks = masks;
        this.createSession = session;
        columns = new Column[0];
        if (!recursive) {
            query = getQuery(session, masks, filters, filter, sortOrder);
        }
        if (recursive || view.getTopQuery() != null) {
            evaluatedAt = Long.MAX_VALUE;
        } else {
            long time = System.nanoTime();
            if (time == Long.MAX_VALUE) {
                time++;
            }
            evaluatedAt = time;
        }
    }

    public SessionLocal getSession() {
        return createSession;
    }

    public boolean isExpired() {
        assert evaluatedAt != Long.MIN_VALUE : "must not be called for main index of TableView";
        return !recursive && view.getTopQuery() == null &&
                System.nanoTime() - evaluatedAt > MAX_AGE_NANOS;
    }

    @Override
    public String getPlanSQL() {
        return query == null ? null : query.getPlanSQL(TRACE_SQL_FLAGS | ADD_PLAN_INFORMATION);
    }

    @Override
    public void close(SessionLocal session) {
        // nothing to do
    }

    @Override
    public void add(SessionLocal session, Row row) {
        throw DbException.getUnsupportedException("VIEW");
    }

    @Override
    public void remove(SessionLocal session, Row row) {
        throw DbException.getUnsupportedException("VIEW");
    }

    @Override
    public double getCost(SessionLocal session, int[] masks,
            TableFilter[] filters, int filter, SortOrder sortOrder,
            AllColumnsForPlan allColumnsSet) {
        return recursive ? 1000 : query.getCost();
    }

    @Override
    public Cursor find(SessionLocal session, SearchRow first, SearchRow last) {
        return find(session, first, last, null);
    }

    @Override
    public Cursor findByGeometry(SessionLocal session, SearchRow first, SearchRow last, SearchRow intersection) {
        return find(session, first, last, intersection);
    }

    private Cursor findRecursive(SearchRow first, SearchRow last) {
        assert recursive;
        ResultInterface recursiveResult = view.getRecursiveResult();
        if (recursiveResult != null) {
            recursiveResult.reset();
            return new ViewCursor(this, recursiveResult, first, last);
        }
        if (query == null) {
            Parser parser = new Parser(createSession);
            parser.setRightsChecked(true);
            parser.setSuppliedParameterList(originalParameters);
            query = (Query) parser.prepare(querySQL);
            query.setNeverLazy(true);
        }
        if (!query.isUnion()) {
            throw DbException.get(ErrorCode.SYNTAX_ERROR_2,
                    "recursive queries without UNION");
        }
        SelectUnion union = (SelectUnion) query;
        Query left = union.getLeft();
        left.setNeverLazy(true);
        // to ensure the last result is not closed
        left.disableCache();
        ResultInterface resultInterface = left.query(0);
        LocalResult localResult = union.getEmptyResult();
        // ensure it is not written to disk,
        // because it is not closed normally
        localResult.setMaxMemoryRows(Integer.MAX_VALUE);
        while (resultInterface.next()) {
            Value[] cr = resultInterface.currentRow();
            localResult.addRow(cr);
        }
        Query right = union.getRight();
        right.setNeverLazy(true);
        resultInterface.reset();
        view.setRecursiveResult(resultInterface);
        // to ensure the last result is not closed
        right.disableCache();
        while (true) {
            resultInterface = right.query(0);
            if (!resultInterface.hasNext()) {
                break;
            }
            while (resultInterface.next()) {
                Value[] cr = resultInterface.currentRow();
                localResult.addRow(cr);
            }
            resultInterface.reset();
            view.setRecursiveResult(resultInterface);
        }
        view.setRecursiveResult(null);
        localResult.done();
        return new ViewCursor(this, localResult, first, last);
    }

    /**
     * Set the query parameters.
     *
     * @param session the session
     * @param first the lower bound
     * @param last the upper bound
     * @param intersection the intersection
     */
    public void setupQueryParameters(SessionLocal session, SearchRow first, SearchRow last,
            SearchRow intersection) {
        ArrayList<Parameter> paramList = query.getParameters();
        if (originalParameters != null) {
            for (Parameter orig : originalParameters) {
                int idx = orig.getIndex();
                Value value = orig.getValue(session);
                setParameter(paramList, idx, value);
            }
        }
        int len;
        if (first != null) {
            len = first.getColumnCount();
        } else if (last != null) {
            len = last.getColumnCount();
        } else if (intersection != null) {
            len = intersection.getColumnCount();
        } else {
            len = 0;
        }
        int idx = view.getParameterOffset(originalParameters);
        for (int i = 0; i < len; i++) {
            int mask = indexMasks[i];
            if ((mask & IndexCondition.EQUALITY) != 0) {
                setParameter(paramList, idx++, first.getValue(i));
            }
            if ((mask & IndexCondition.START) != 0) {
                setParameter(paramList, idx++, first.getValue(i));
            }
            if ((mask & IndexCondition.END) != 0) {
                setParameter(paramList, idx++, last.getValue(i));
            }
            if ((mask & IndexCondition.SPATIAL_INTERSECTS) != 0) {
                setParameter(paramList, idx++, intersection.getValue(i));
            }
        }
    }

    private Cursor find(SessionLocal session, SearchRow first, SearchRow last,
            SearchRow intersection) {
        if (recursive) {
            return findRecursive(first, last);
        }
        setupQueryParameters(session, first, last, intersection);
        ResultInterface result = query.query(0);
        return new ViewCursor(this, result, first, last);
    }

    private static void setParameter(ArrayList<Parameter> paramList, int x,
            Value v) {
        if (x >= paramList.size()) {
            // the parameter may be optimized away as in
            // select * from (select null as x) where x=1;
            return;
        }
        Parameter param = paramList.get(x);
        param.setValue(v);
    }

    public Query getQuery() {
        return query;
    }

    private Query getQuery(SessionLocal session, int[] masks,
            TableFilter[] filters, int filter, SortOrder sortOrder) {
        Query q = (Query) session.prepare(querySQL, true, true);
        if (masks == null) {
            return q;
        }
        if (!q.allowGlobalConditions()) {
            return q;
        }
        int firstIndexParam = view.getParameterOffset(originalParameters);
        // the column index of each parameter
        // (for example: paramColumnIndex {0, 0} mean
        // param[0] is column 0, and param[1] is also column 0)
        IntArray paramColumnIndex = new IntArray();
        int indexColumnCount = 0;
        for (int i = 0; i < masks.length; i++) {
            int mask = masks[i];
            if (mask == 0) {
                continue;
            }
            indexColumnCount++;
            // the number of parameters depends on the mask;
            // for range queries it is 2: >= x AND <= y
            // but bitMask could also be 7 (=, and <=, and >=)
            int bitCount = Integer.bitCount(mask);
            for (int j = 0; j < bitCount; j++) {
                paramColumnIndex.add(i);
            }
        }
        int len = paramColumnIndex.size();
        ArrayList<Column> columnList = new ArrayList<>(len);
        for (int i = 0; i < len;) {
            int idx = paramColumnIndex.get(i);
            columnList.add(table.getColumn(idx));
            int mask = masks[idx];
            if ((mask & IndexCondition.EQUALITY) != 0) {
                Parameter param = new Parameter(firstIndexParam + i);
                q.addGlobalCondition(param, idx, Comparison.EQUAL_NULL_SAFE);
                i++;
            }
            if ((mask & IndexCondition.START) != 0) {
                Parameter param = new Parameter(firstIndexParam + i);
                q.addGlobalCondition(param, idx, Comparison.BIGGER_EQUAL);
                i++;
            }
            if ((mask & IndexCondition.END) != 0) {
                Parameter param = new Parameter(firstIndexParam + i);
                q.addGlobalCondition(param, idx, Comparison.SMALLER_EQUAL);
                i++;
            }
            if ((mask & IndexCondition.SPATIAL_INTERSECTS) != 0) {
                Parameter param = new Parameter(firstIndexParam + i);
                q.addGlobalCondition(param, idx, Comparison.SPATIAL_INTERSECTS);
                i++;
            }
        }
        columns = columnList.toArray(new Column[0]);

        // reconstruct the index columns from the masks
        this.indexColumns = new IndexColumn[indexColumnCount];
        this.columnIds = new int[indexColumnCount];
        for (int type = 0, indexColumnId = 0; type < 2; type++) {
            for (int i = 0; i < masks.length; i++) {
                int mask = masks[i];
                if (mask == 0) {
                    continue;
                }
                if (type == 0) {
                    if ((mask & IndexCondition.EQUALITY) == 0) {
                        // the first columns need to be equality conditions
                        continue;
                    }
                } else {
                    if ((mask & IndexCondition.EQUALITY) != 0) {
                        // after that only range conditions
                        continue;
                    }
                }
                Column column = table.getColumn(i);
                indexColumns[indexColumnId] = new IndexColumn(column);
                columnIds[indexColumnId] = column.getColumnId();
                indexColumnId++;
            }
        }

        String sql = q.getPlanSQL(DEFAULT_SQL_FLAGS);
        q = (Query) session.prepare(sql, true, true);
        return q;
    }

    @Override
    public void remove(SessionLocal session) {
        throw DbException.getUnsupportedException("VIEW");
    }

    @Override
    public void truncate(SessionLocal session) {
        throw DbException.getUnsupportedException("VIEW");
    }

    @Override
    public void checkRename() {
        throw DbException.getUnsupportedException("VIEW");
    }

    @Override
    public boolean needRebuild() {
        return false;
    }

    public void setRecursive(boolean value) {
        this.recursive = value;
    }

    @Override
    public long getRowCount(SessionLocal session) {
        return 0;
    }

    @Override
    public long getRowCountApproximation(SessionLocal session) {
        return 0;
    }

    public boolean isRecursive() {
        return recursive;
    }
}
