/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://h2database.com/html/license.html).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.expression.function;

import org.gunsioo.expression.Expression;
import org.gunsioo.expression.Operation1;

/**
 * Function with one argument.
 */
public abstract class Function1 extends Operation1 implements NamedExpression {

    protected Function1(Expression arg) {
        super(arg);
    }

    @Override
    public StringBuilder getUnenclosedSQL(StringBuilder builder, int sqlFlags) {
        return arg.getUnenclosedSQL(builder.append(getName()).append('('), sqlFlags).append(')');
    }

}
