/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.expression.function;

import org.guinsoo.api.ErrorCode;
import org.guinsoo.engine.SessionLocal;
import org.guinsoo.expression.Expression;
import org.guinsoo.expression.ExpressionVisitor;
import org.guinsoo.expression.ValueExpression;
import org.guinsoo.message.DbException;
import org.guinsoo.schema.FunctionAlias;
import org.guinsoo.table.ColumnResolver;
import org.guinsoo.table.TableFilter;
import org.guinsoo.value.TypeInfo;
import org.guinsoo.value.Value;

/**
 * This class wraps a user-defined function.
 */
public final class JavaFunction extends Expression implements NamedExpression {

    private final FunctionAlias functionAlias;
    private final FunctionAlias.JavaMethod javaMethod;
    private final Expression[] args;

    public JavaFunction(FunctionAlias functionAlias, Expression[] args) {
        this.functionAlias = functionAlias;
        this.javaMethod = functionAlias.findJavaMethod(args);
        if (javaMethod.getDataType() == null) {
            throw DbException.get(ErrorCode.FUNCTION_NOT_FOUND_1, getName());
        }
        this.args = args;
    }

    @Override
    public Value getValue(SessionLocal session) {
        return javaMethod.getValue(session, args, false);
    }

    @Override
    public TypeInfo getType() {
        return javaMethod.getDataType();
    }

    @Override
    public void mapColumns(ColumnResolver resolver, int level, int state) {
        for (Expression e : args) {
            e.mapColumns(resolver, level, state);
        }
    }

    @Override
    public Expression optimize(SessionLocal session) {
        boolean allConst = functionAlias.isDeterministic();
        for (int i = 0, len = args.length; i < len; i++) {
            Expression e = args[i].optimize(session);
            args[i] = e;
            allConst &= e.isConstant();
        }
        if (allConst) {
            return ValueExpression.get(getValue(session));
        }
        return this;
    }

    @Override
    public void setEvaluatable(TableFilter tableFilter, boolean b) {
        for (Expression e : args) {
            if (e != null) {
                e.setEvaluatable(tableFilter, b);
            }
        }
    }

    @Override
    public StringBuilder getUnenclosedSQL(StringBuilder builder, int sqlFlags) {
        return writeExpressions(functionAlias.getSQL(builder, sqlFlags).append('('), args, sqlFlags).append(')');
    }

    @Override
    public void updateAggregate(SessionLocal session, int stage) {
        for (Expression e : args) {
            if (e != null) {
                e.updateAggregate(session, stage);
            }
        }
    }

    @Override
    public String getName() {
        return functionAlias.getName();
    }

    @Override
    public boolean isEverything(ExpressionVisitor visitor) {
        switch (visitor.getType()) {
        case ExpressionVisitor.DETERMINISTIC:
            if (!functionAlias.isDeterministic()) {
                return false;
            }
            // only if all parameters are deterministic as well
            break;
        case ExpressionVisitor.GET_DEPENDENCIES:
            visitor.addDependency(functionAlias);
            break;
        default:
        }
        for (Expression e : args) {
            if (e != null && !e.isEverything(visitor)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int getCost() {
        int cost = javaMethod.hasConnectionParam() ? 25 : 5;
        for (Expression e : args) {
            cost += e.getCost();
        }
        return cost;
    }

    @Override
    public int getSubexpressionCount() {
        return args.length;
    }

    @Override
    public Expression getSubexpression(int index) {
        return args[index];
    }

}
