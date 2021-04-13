/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://h2database.com/html/license.html).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.command.query;

import java.util.ArrayList;

import org.gunsioo.engine.Database;
import org.gunsioo.engine.SessionLocal;
import org.gunsioo.expression.Expression;
import org.gunsioo.expression.ExpressionColumn;
import org.gunsioo.table.Column;
import org.gunsioo.table.ColumnResolver;
import org.gunsioo.value.TypeInfo;
import org.gunsioo.value.Value;

/**
 * This class represents a column resolver for the column list of a SELECT
 * statement. It is used to resolve select column aliases in the HAVING clause.
 * Example:
 * <p>
 * SELECT X/3 AS A, COUNT(*) FROM SYSTEM_RANGE(1, 10) GROUP BY A HAVING A>2;
 * </p>
 *
 * @author Thomas Mueller
 */
public class SelectListColumnResolver implements ColumnResolver {

    private final Select select;
    private final Expression[] expressions;
    private final Column[] columns;

    SelectListColumnResolver(Select select) {
        this.select = select;
        int columnCount = select.getColumnCount();
        columns = new Column[columnCount];
        expressions = new Expression[columnCount];
        ArrayList<Expression> columnList = select.getExpressions();
        SessionLocal session = select.getSession();
        for (int i = 0; i < columnCount; i++) {
            Expression expr = columnList.get(i);
            columns[i] = new Column(expr.getAlias(session, i), TypeInfo.TYPE_NULL, null, i);
            expressions[i] = expr.getNonAliasExpression();
        }
    }

    @Override
    public Column[] getColumns() {
        return columns;
    }

    @Override
    public Column findColumn(String name) {
        Database db = select.getSession().getDatabase();
        for (Column column : columns) {
            if (db.equalsIdentifiers(column.getName(), name)) {
                return column;
            }
        }
        return null;
    }

    @Override
    public Select getSelect() {
        return select;
    }

    @Override
    public Value getValue(Column column) {
        return null;
    }

    @Override
    public Expression optimize(ExpressionColumn expressionColumn, Column column) {
        return expressions[column.getColumnId()];
    }

}
