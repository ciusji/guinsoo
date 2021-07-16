/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.expression.condition;

import org.guinsoo.command.query.Query;
import org.guinsoo.engine.SessionLocal;
import org.guinsoo.result.ResultInterface;
import org.guinsoo.value.Value;
import org.guinsoo.value.ValueBoolean;

/**
 * Exists predicate as in EXISTS(SELECT ...)
 */
public class ExistsPredicate extends PredicateWithSubquery {

    public ExistsPredicate(Query query) {
        super(query);
    }

    @Override
    public Value getValue(SessionLocal session) {
        query.setSession(session);
        ResultInterface result = query.query(1);
        session.addTemporaryResult(result);
        return ValueBoolean.get(result.hasNext());
    }

    @Override
    public StringBuilder getUnenclosedSQL(StringBuilder builder, int sqlFlags) {
        return super.getUnenclosedSQL(builder.append("EXISTS"), sqlFlags);
    }

}
