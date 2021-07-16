/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.expression.function;

import org.guinsoo.expression.Expression;
import org.guinsoo.expression.Operation1;

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
