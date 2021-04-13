/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://h2database.com/html/license.html).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.expression.condition;

import org.gunsioo.command.query.Query;
import org.gunsioo.engine.SessionLocal;
import org.gunsioo.result.ResultInterface;
import org.gunsioo.value.Value;
import org.gunsioo.value.ValueBoolean;

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
