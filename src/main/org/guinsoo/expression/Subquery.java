/*
 * Copyright 2004-2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.expression;

import java.util.ArrayList;
import java.util.Arrays;

import org.guinsoo.command.query.Query;
import org.guinsoo.api.ErrorCode;
import org.guinsoo.engine.SessionLocal;
import org.guinsoo.message.DbException;
import org.guinsoo.result.ResultInterface;
import org.guinsoo.table.ColumnResolver;
import org.guinsoo.table.TableFilter;
import org.guinsoo.value.TypeInfo;
import org.guinsoo.value.Value;
import org.guinsoo.value.ValueNull;
import org.guinsoo.value.ValueRow;

/**
 * A query returning a single value.
 * Subqueries are used inside other statements.
 */
public final class Subquery extends Expression {

    private final Query query;
    private Expression expression;

    public Subquery(Query query) {
        this.query = query;
    }

    @Override
    public Value getValue(SessionLocal session) {
        query.setSession(session);
        try (ResultInterface result = query.query(2)) {
            Value v;
            if (!result.next()) {
                v = ValueNull.INSTANCE;
            } else {
                v = readRow(result);
                if (result.hasNext()) {
                    throw DbException.get(ErrorCode.SCALAR_SUBQUERY_CONTAINS_MORE_THAN_ONE_ROW);
                }
            }
            return v;
        }
    }

    /**
     * Evaluates and returns all rows of the subquery.
     *
     * @param session
     *            the session
     * @return values in all rows
     */
    public ArrayList<Value> getAllRows(SessionLocal session) {
        ArrayList<Value> list = new ArrayList<>();
        query.setSession(session);
        try (ResultInterface result = query.query(Integer.MAX_VALUE)) {
            while (result.next()) {
                list.add(readRow(result));
            }
        }
        return list;
    }

    private Value readRow(ResultInterface result) {
        Value[] values = result.currentRow();
        int visible = result.getVisibleColumnCount();
        return visible == 1 ? values[0]
                : ValueRow.get(getType(), visible == values.length ? values : Arrays.copyOf(values, visible));
    }

    @Override
    public TypeInfo getType() {
        return expression.getType();
    }

    @Override
    public void mapColumns(ColumnResolver resolver, int level, int state) {
        query.mapColumns(resolver, level + 1);
    }

    @Override
    public Expression optimize(SessionLocal session) {
        query.prepare();
        if (query.isConstantQuery()) {
            setType();
            return ValueExpression.get(getValue(session));
        }
        Expression e = query.getIfSingleRow();
        if (e != null) {
            return e.optimize(session);
        }
        setType();
        return this;
    }

    private void setType() {
        ArrayList<Expression> expressions = query.getExpressions();
        int columnCount = query.getColumnCount();
        if (columnCount == 1) {
            expression = expressions.get(0);
        } else {
            Expression[] list = new Expression[columnCount];
            for (int i = 0; i < columnCount; i++) {
                list[i] = expressions.get(i);
            }
            ExpressionList expressionList = new ExpressionList(list, false);
            expressionList.initializeType();
            expression = expressionList;
        }
    }

    @Override
    public void setEvaluatable(TableFilter tableFilter, boolean b) {
        query.setEvaluatable(tableFilter, b);
    }

    @Override
    public StringBuilder getUnenclosedSQL(StringBuilder builder, int sqlFlags) {
        return builder.append('(').append(query.getPlanSQL(sqlFlags)).append(')');
    }

    @Override
    public void updateAggregate(SessionLocal session, int stage) {
        query.updateAggregate(session, stage);
    }

    @Override
    public boolean isEverything(ExpressionVisitor visitor) {
        return query.isEverything(visitor);
    }

    public Query getQuery() {
        return query;
    }

    @Override
    public int getCost() {
        return query.getCostAsExpression();
    }

    @Override
    public boolean isConstant() {
        return query.isConstantQuery();
    }

}
