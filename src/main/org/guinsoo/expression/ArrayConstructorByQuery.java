/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.expression;

import java.util.ArrayList;

import org.guinsoo.command.query.Query;
import org.guinsoo.api.ErrorCode;
import org.guinsoo.engine.SessionLocal;
import org.guinsoo.message.DbException;
import org.guinsoo.result.ResultInterface;
import org.guinsoo.table.ColumnResolver;
import org.guinsoo.table.TableFilter;
import org.guinsoo.util.StringUtils;
import org.guinsoo.value.TypeInfo;
import org.guinsoo.value.Value;
import org.guinsoo.value.ValueArray;

/**
 * Array value constructor by query.
 */
public final class ArrayConstructorByQuery extends Expression {

    /**
     * The subquery.
     */
    private final Query query;

    private TypeInfo componentType, type;

    /**
     * Creates new instance of array value constructor by query.
     *
     * @param query
     *            the query
     */
    public ArrayConstructorByQuery(Query query) {
        this.query = query;
    }

    @Override
    public StringBuilder getUnenclosedSQL(StringBuilder builder, int sqlFlags) {
        return StringUtils.indent(builder.append("ARRAY ("), query.getPlanSQL(sqlFlags), 4, false).append(')');
    }

    @Override
    public Value getValue(SessionLocal session) {
        query.setSession(session);
        ArrayList<Value> values = new ArrayList<>();
        try (ResultInterface result = query.query(0)) {
            while (result.next()) {
                values.add(result.currentRow()[0]);
            }
        }
        return ValueArray.get(componentType, values.toArray(new Value[0]), session);
    }

    @Override
    public TypeInfo getType() {
        return type;
    }

    @Override
    public void mapColumns(ColumnResolver resolver, int level, int state) {
        query.mapColumns(resolver, level + 1);
    }

    @Override
    public Expression optimize(SessionLocal session) {
        query.prepare();
        if (query.getColumnCount() != 1) {
            throw DbException.get(ErrorCode.SUBQUERY_IS_NOT_SINGLE_COLUMN);
        }
        componentType = query.getExpressions().get(0).getType();
        type = TypeInfo.getTypeInfo(Value.ARRAY, -1L, -1, componentType);
        return this;
    }

    @Override
    public void setEvaluatable(TableFilter tableFilter, boolean value) {
        query.setEvaluatable(tableFilter, value);
    }

    @Override
    public void updateAggregate(SessionLocal session, int stage) {
        query.updateAggregate(session, stage);
    }

    @Override
    public boolean isEverything(ExpressionVisitor visitor) {
        return query.isEverything(visitor);
    }

    @Override
    public int getCost() {
        return query.getCostAsExpression();
    }

}
