/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.expression;

import org.guinsoo.engine.SessionLocal;
import org.guinsoo.table.ColumnResolver;
import org.guinsoo.table.TableFilter;
import org.guinsoo.util.ParserUtil;
import org.guinsoo.value.TypeInfo;
import org.guinsoo.value.Value;

/**
 * A column alias as in SELECT 'Hello' AS NAME ...
 */
public final class Alias extends Expression {

    private final String alias;
    private Expression expr;
    private final boolean aliasColumnName;

    public Alias(Expression expression, String alias, boolean aliasColumnName) {
        this.expr = expression;
        this.alias = alias;
        this.aliasColumnName = aliasColumnName;
    }

    @Override
    public Expression getNonAliasExpression() {
        return expr;
    }

    @Override
    public Value getValue(SessionLocal session) {
        return expr.getValue(session);
    }

    @Override
    public TypeInfo getType() {
        return expr.getType();
    }

    @Override
    public void mapColumns(ColumnResolver resolver, int level, int state) {
        expr.mapColumns(resolver, level, state);
    }

    @Override
    public Expression optimize(SessionLocal session) {
        expr = expr.optimize(session);
        return this;
    }

    @Override
    public void setEvaluatable(TableFilter tableFilter, boolean b) {
        expr.setEvaluatable(tableFilter, b);
    }

    @Override
    public boolean isIdentity() {
        return expr.isIdentity();
    }

    @Override
    public StringBuilder getUnenclosedSQL(StringBuilder builder, int sqlFlags) {
        expr.getUnenclosedSQL(builder, sqlFlags).append(" AS ");
        return ParserUtil.quoteIdentifier(builder, alias, sqlFlags);
    }

    @Override
    public void updateAggregate(SessionLocal session, int stage) {
        expr.updateAggregate(session, stage);
    }

    @Override
    public String getAlias(SessionLocal session, int columnIndex) {
        return alias;
    }

    @Override
    public String getColumnNameForView(SessionLocal session, int columnIndex) {
        return alias;
    }

    @Override
    public int getNullable() {
        return expr.getNullable();
    }

    @Override
    public boolean isEverything(ExpressionVisitor visitor) {
        return expr.isEverything(visitor);
    }

    @Override
    public int getCost() {
        return expr.getCost();
    }

    @Override
    public String getTableName() {
        if (aliasColumnName) {
            return null;
        }
        return expr.getTableName();
    }

    @Override
    public String getColumnName(SessionLocal session, int columnIndex) {
        if (!(expr instanceof ExpressionColumn) || aliasColumnName) {
            return alias;
        }
        return expr.getColumnName(session, columnIndex);
    }

}
