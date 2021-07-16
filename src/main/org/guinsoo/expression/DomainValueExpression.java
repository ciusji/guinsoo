/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.expression;

import org.guinsoo.api.ErrorCode;
import org.guinsoo.constraint.DomainColumnResolver;
import org.guinsoo.engine.SessionLocal;
import org.guinsoo.message.DbException;
import org.guinsoo.table.ColumnResolver;
import org.guinsoo.util.ParserUtil;
import org.guinsoo.value.TypeInfo;
import org.guinsoo.value.Value;

/**
 * An expression representing a value for domain constraint.
 */
public final class DomainValueExpression extends Operation0 {

    private DomainColumnResolver columnResolver;

    public DomainValueExpression() {
    }

    @Override
    public Value getValue(SessionLocal session) {
        return columnResolver.getValue(null);
    }

    @Override
    public TypeInfo getType() {
        return columnResolver.getValueType();
    }

    @Override
    public void mapColumns(ColumnResolver resolver, int level, int state) {
        if (resolver instanceof DomainColumnResolver) {
            columnResolver = (DomainColumnResolver) resolver;
        }
    }

    @Override
    public Expression optimize(SessionLocal session) {
        if (columnResolver == null) {
            throw DbException.get(ErrorCode.COLUMN_NOT_FOUND_1, "VALUE");
        }
        return this;
    }

    @Override
    public boolean isValueSet() {
        return true;
    }

    @Override
    public StringBuilder getUnenclosedSQL(StringBuilder builder, int sqlFlags) {
        if (columnResolver != null) {
            String name = columnResolver.getColumnName();
            if (name != null) {
                return ParserUtil.quoteIdentifier(builder, name, sqlFlags);
            }
        }
        return builder.append("VALUE");
    }

    @Override
    public boolean isEverything(ExpressionVisitor visitor) {
        switch (visitor.getType()) {
        case ExpressionVisitor.OPTIMIZABLE_AGGREGATE:
        case ExpressionVisitor.DETERMINISTIC:
        case ExpressionVisitor.READONLY:
        case ExpressionVisitor.INDEPENDENT:
        case ExpressionVisitor.EVALUATABLE:
        case ExpressionVisitor.SET_MAX_DATA_MODIFICATION_ID:
        case ExpressionVisitor.NOT_FROM_RESOLVER:
        case ExpressionVisitor.GET_DEPENDENCIES:
        case ExpressionVisitor.QUERY_COMPARABLE:
        case ExpressionVisitor.GET_COLUMNS1:
        case ExpressionVisitor.GET_COLUMNS2:
            return true;
        default:
            throw DbException.getInternalError("type=" + visitor.getType());
        }
    }

    @Override
    public int getCost() {
        return 1;
    }

}
