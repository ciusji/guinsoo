/*
 * Copyright 2004-2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.expression.function;

import org.guinsoo.command.Parser;
import org.guinsoo.engine.Database;
import org.guinsoo.engine.SessionLocal;
import org.guinsoo.expression.Expression;
import org.guinsoo.expression.ExpressionColumn;
import org.guinsoo.expression.ExpressionVisitor;
import org.guinsoo.message.DbException;
import org.guinsoo.schema.Schema;
import org.guinsoo.schema.Sequence;
import org.guinsoo.util.StringUtils;
import org.guinsoo.value.TypeInfo;
import org.guinsoo.value.Value;

/**
 * NEXTVAL() and CURRVAL() compatibility functions.
 */
public final class CompatibilitySequenceValueFunction extends Function1_2 {

    private final boolean current;

    public CompatibilitySequenceValueFunction(Expression left, Expression right, boolean current) {
        super(left, right);
        this.current = current;
    }

    @Override
    public Value getValue(SessionLocal session, Value v1, Value v2) {
        String schemaName, sequenceName;
        if (v2 == null) {
            Parser p = new Parser(session);
            String sql = v1.getString();
            Expression expr = p.parseExpression(sql);
            if (expr instanceof ExpressionColumn) {
                ExpressionColumn seq = (ExpressionColumn) expr;
                schemaName = seq.getOriginalTableAliasName();
                if (schemaName == null) {
                    schemaName = session.getCurrentSchemaName();
                    sequenceName = sql;
                } else {
                    sequenceName = seq.getColumnName(session, -1);
                }
            } else {
                throw DbException.getSyntaxError(sql, 1);
            }
        } else {
            schemaName = v1.getString();
            sequenceName = v2.getString();
        }
        Database database = session.getDatabase();
        Schema s = database.findSchema(schemaName);
        if (s == null) {
            schemaName = StringUtils.toUpperEnglish(schemaName);
            s = database.getSchema(schemaName);
        }
        Sequence seq = s.findSequence(sequenceName);
        if (seq == null) {
            sequenceName = StringUtils.toUpperEnglish(sequenceName);
            seq = s.getSequence(sequenceName);
        }
        return (current ? session.getCurrentValueFor(seq) : session.getNextValueFor(seq, null)).convertTo(type);
    }

    @Override
    public Expression optimize(SessionLocal session) {
        left = left.optimize(session);
        if (right != null) {
            right = right.optimize(session);
        }
        type = session.getMode().decimalSequences ? TypeInfo.TYPE_NUMERIC_BIGINT : TypeInfo.TYPE_BIGINT;
        return this;
    }

    @Override
    public boolean isEverything(ExpressionVisitor visitor) {
        switch (visitor.getType()) {
        case ExpressionVisitor.INDEPENDENT:
        case ExpressionVisitor.DETERMINISTIC:
        case ExpressionVisitor.QUERY_COMPARABLE:
            return false;
        case ExpressionVisitor.READONLY:
            if (!current) {
                return false;
            }
            //$FALL-THROUGH$
        case ExpressionVisitor.OPTIMIZABLE_AGGREGATE:
        case ExpressionVisitor.EVALUATABLE:
        case ExpressionVisitor.SET_MAX_DATA_MODIFICATION_ID:
        case ExpressionVisitor.NOT_FROM_RESOLVER:
        case ExpressionVisitor.GET_DEPENDENCIES:
        case ExpressionVisitor.GET_COLUMNS1:
        case ExpressionVisitor.GET_COLUMNS2:
            return super.isEverything(visitor);
        default:
            throw DbException.getInternalError("type=" + visitor.getType());
        }
    }

    @Override
    public String getName() {
        return current ? "CURRVAL" : "NEXTVAL";
    }

}
