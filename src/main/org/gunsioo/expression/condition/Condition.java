/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.expression.condition;

import org.gunsioo.engine.SessionLocal;
import org.gunsioo.expression.Expression;
import org.gunsioo.expression.function.CastSpecification;
import org.gunsioo.value.TypeInfo;
import org.gunsioo.value.Value;

/**
 * Represents a condition returning a boolean value, or NULL.
 */
abstract class Condition extends Expression {

    /**
     * Add a cast around the expression (if necessary) so that the type is boolean.
     *
     * @param session the session
     * @param expression the expression
     * @return the new expression
     */
    static Expression castToBoolean(SessionLocal session, Expression expression) {
        if (expression.getType().getValueType() == Value.BOOLEAN) {
            return expression;
        }
        return new CastSpecification(expression, TypeInfo.TYPE_BOOLEAN);
    }

    @Override
    public TypeInfo getType() {
        return TypeInfo.TYPE_BOOLEAN;
    }

}
