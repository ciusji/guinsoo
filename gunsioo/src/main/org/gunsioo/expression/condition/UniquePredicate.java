/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://h2database.com/html/license.html).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.expression.condition;

import java.util.Arrays;

import org.gunsioo.command.query.Query;
import org.gunsioo.engine.SessionLocal;
import org.gunsioo.expression.Expression;
import org.gunsioo.expression.ValueExpression;
import org.gunsioo.result.LocalResult;
import org.gunsioo.result.ResultTarget;
import org.gunsioo.value.Value;
import org.gunsioo.value.ValueBoolean;
import org.gunsioo.value.ValueNull;

/**
 * Unique predicate as in UNIQUE(SELECT ...)
 */
public class UniquePredicate extends PredicateWithSubquery {

    private final class Target implements ResultTarget {

        private final int columnCount;

        private final LocalResult result;

        boolean hasDuplicates;

        Target(int columnCount, LocalResult result) {
            this.columnCount = columnCount;
            this.result = result;
        }

        @Override
        public void limitsWereApplied() {
            // Nothing to do
        }

        @Override
        public long getRowCount() {
            // Not required
            return 0L;
        }

        @Override
        public void addRow(Value... values) {
            if (hasDuplicates) {
                return;
            }
            for (int i = 0; i < columnCount; i++) {
                if (values[i] == ValueNull.INSTANCE) {
                    return;
                }
            }
            if (values.length != columnCount) {
                values = Arrays.copyOf(values, columnCount);
            }
            long expected = result.getRowCount() + 1;
            result.addRow(values);
            if (expected != result.getRowCount()) {
                hasDuplicates = true;
                result.close();
            }
        }
    }

    public UniquePredicate(Query query) {
        super(query);
    }

    @Override
    public Expression optimize(SessionLocal session) {
        super.optimize(session);
        if (query.isStandardDistinct()) {
            return ValueExpression.TRUE;
        }
        return this;
    }

    @Override
    public Value getValue(SessionLocal session) {
        query.setSession(session);
        int columnCount = query.getColumnCount();
        LocalResult result = new LocalResult(session,
                query.getExpressions().toArray(new Expression[0]), columnCount, columnCount);
        result.setDistinct();
        Target target = new Target(columnCount, result);
        query.query(Integer.MAX_VALUE, target);
        result.close();
        return ValueBoolean.get(!target.hasDuplicates);
    }

    @Override
    public StringBuilder getUnenclosedSQL(StringBuilder builder, int sqlFlags) {
        return super.getUnenclosedSQL(builder.append("UNIQUE"), sqlFlags);
    }

}
