/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.table;

import java.util.ArrayList;

import org.guinsoo.command.query.TableValueConstructor;
import org.guinsoo.engine.SessionLocal;
import org.guinsoo.expression.Expression;
import org.guinsoo.result.ResultInterface;
import org.guinsoo.result.SimpleResult;
import org.guinsoo.schema.Schema;

/**
 * A table for table value constructor.
 */
public class TableValueConstructorTable extends VirtualConstructedTable {

    private final ArrayList<ArrayList<Expression>> rows;

    public TableValueConstructorTable(Schema schema, SessionLocal session, Column[] columns,
                                      ArrayList<ArrayList<Expression>> rows) {
        super(schema, 0, "VALUES");
        setColumns(columns);
        this.rows = rows;
    }

    @Override
    public boolean canGetRowCount(SessionLocal session) {
        return true;
    }

    @Override
    public long getRowCount(SessionLocal session) {
        return rows.size();
    }

    @Override
    public long getRowCountApproximation(SessionLocal session) {
        return rows.size();
    }

    @Override
    public ResultInterface getResult(SessionLocal session) {
        SimpleResult simple = new SimpleResult();
        int columnCount = columns.length;
        for (int i = 0; i < columnCount; i++) {
            Column column = columns[i];
            simple.addColumn(column.getName(), column.getType());
        }
        TableValueConstructor.getVisibleResult(session, simple, columns, rows);
        return simple;
    }

    @Override
    public StringBuilder getSQL(StringBuilder builder, int sqlFlags) {
        builder.append('(');
        TableValueConstructor.getValuesSQL(builder, sqlFlags, rows);
        return builder.append(')');
    }

    @Override
    public boolean isDeterministic() {
        return true;
    }

}
