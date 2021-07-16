/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.table;

import org.guinsoo.engine.SessionLocal;
import org.guinsoo.result.ResultInterface;
import org.guinsoo.schema.Schema;
import org.guinsoo.expression.function.table.TableFunction;

/**
 * A table backed by a system or user-defined function that returns a result
 * set.
 */
public class FunctionTable extends VirtualConstructedTable {

    private final TableFunction function;

    public FunctionTable(Schema schema, SessionLocal session, TableFunction function) {
        super(schema, 0, function.getName());
        this.function = function;
        function.optimize(session);
        ResultInterface result = function.getValueTemplate(session);
        int columnCount = result.getVisibleColumnCount();
        Column[] cols = new Column[columnCount];
        for (int i = 0; i < columnCount; i++) {
            cols[i] = new Column(result.getColumnName(i), result.getColumnType(i));
        }
        setColumns(cols);
    }

    @Override
    public boolean canGetRowCount(SessionLocal session) {
        return false;
    }

    @Override
    public long getRowCount(SessionLocal session) {
        return Long.MAX_VALUE;
    }

    @Override
    public long getRowCountApproximation(SessionLocal session) {
        return Long.MAX_VALUE;
    }

    @Override
    public ResultInterface getResult(SessionLocal session) {
        return function.getValue(session);
    }

    @Override
    public String getSQL(int sqlFlags) {
        return function.getSQL(sqlFlags);
    }

    @Override
    public StringBuilder getSQL(StringBuilder builder, int sqlFlags) {
        return builder.append(function.getSQL(sqlFlags));
    }

    @Override
    public boolean isDeterministic() {
        return function.isDeterministic();
    }

}
