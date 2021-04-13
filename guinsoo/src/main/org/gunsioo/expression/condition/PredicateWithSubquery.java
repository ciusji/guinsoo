/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.expression.condition;

import org.gunsioo.command.query.Query;
import org.gunsioo.engine.SessionLocal;
import org.gunsioo.expression.Expression;
import org.gunsioo.expression.ExpressionVisitor;
import org.gunsioo.table.ColumnResolver;
import org.gunsioo.table.TableFilter;
import org.gunsioo.util.StringUtils;

/**
 * Base class for predicates with a subquery.
 */
abstract class PredicateWithSubquery extends Condition {

    /**
     * The subquery.
     */
    final Query query;

    PredicateWithSubquery(Query query) {
        this.query = query;
    }

    @Override
    public void mapColumns(ColumnResolver resolver, int level, int state) {
        query.mapColumns(resolver, level + 1);
    }

    @Override
    public Expression optimize(SessionLocal session) {
        query.prepare();
        return this;
    }

    @Override
    public void setEvaluatable(TableFilter tableFilter, boolean value) {
        query.setEvaluatable(tableFilter, value);
    }

    @Override
    public StringBuilder getUnenclosedSQL(StringBuilder builder, int sqlFlags) {
        return StringUtils.indent(builder.append('('), query.getPlanSQL(sqlFlags), 4, false).append(')');
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
