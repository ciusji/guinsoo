/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.expression.function;

import org.gunsioo.engine.SessionLocal;
import org.gunsioo.expression.ExpressionVisitor;
import org.gunsioo.expression.Operation0;
import org.gunsioo.message.DbException;
import org.gunsioo.util.HasSQL;
import org.gunsioo.util.ParserUtil;
import org.gunsioo.value.TypeInfo;
import org.gunsioo.value.Value;
import org.gunsioo.value.ValueNull;
import org.gunsioo.value.ValueVarchar;

/**
 * Simple general value specifications.
 */
public final class CurrentGeneralValueSpecification extends Operation0 implements NamedExpression {

    /**
     * The "CURRENT_CATALOG" general value specification.
     */
    public static final int CURRENT_CATALOG = 0;

    /**
     * The "CURRENT_PATH" general value specification.
     */
    public static final int CURRENT_PATH = CURRENT_CATALOG + 1;

    /**
     * The function "CURRENT_ROLE" general value specification.
     */
    public static final int CURRENT_ROLE = CURRENT_PATH + 1;

    /**
     * The function "CURRENT_SCHEMA" general value specification.
     */
    public static final int CURRENT_SCHEMA = CURRENT_ROLE + 1;

    /**
     * The function "CURRENT_USER" general value specification.
     */
    public static final int CURRENT_USER = CURRENT_SCHEMA + 1;

    /**
     * The function "SESSION_USER" general value specification.
     */
    public static final int SESSION_USER = CURRENT_USER + 1;

    /**
     * The function "SYSTEM_USER" general value specification.
     */
    public static final int SYSTEM_USER = SESSION_USER + 1;

    private static final String[] NAMES = { "CURRENT_CATALOG", "CURRENT_PATH", "CURRENT_ROLE", "CURRENT_SCHEMA",
            "CURRENT_USER", "SESSION_USER", "SYSTEM_USER" };

    private final int specification;

    public CurrentGeneralValueSpecification(int specification) {
        this.specification = specification;
    }

    @Override
    public Value getValue(SessionLocal session) {
        String s;
        switch (specification) {
        case CURRENT_CATALOG:
            s = session.getDatabase().getShortName();
            break;
        case CURRENT_PATH: {
            String[] searchPath = session.getSchemaSearchPath();
            if (searchPath != null) {
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < searchPath.length; i++) {
                    if (i > 0) {
                        builder.append(',');
                    }
                    ParserUtil.quoteIdentifier(builder, searchPath[i], HasSQL.DEFAULT_SQL_FLAGS);
                }
                s = builder.toString();
            } else {
                s = "";
            }
            break;
        }
        case CURRENT_ROLE:
            s = session.getDatabase().sysIdentifier(session.getDatabase().getPublicRole().getName());
            break;
        case CURRENT_SCHEMA:
            s = session.getCurrentSchemaName();
            break;
        case CURRENT_USER:
        case SESSION_USER:
        case SYSTEM_USER:
            s = session.getDatabase().sysIdentifier(session.getUser().getName());
            break;
        default:
            throw DbException.getInternalError("specification=" + specification);
        }
        return s != null ? ValueVarchar.get(s, session) : ValueNull.INSTANCE;
    }

    @Override
    public StringBuilder getUnenclosedSQL(StringBuilder builder, int sqlFlags) {
        return builder.append(getName());
    }

    @Override
    public boolean isEverything(ExpressionVisitor visitor) {
        switch (visitor.getType()) {
        case ExpressionVisitor.DETERMINISTIC:
            return false;
        }
        return true;
    }

    @Override
    public TypeInfo getType() {
        return TypeInfo.TYPE_VARCHAR;
    }

    @Override
    public int getCost() {
        return 1;
    }

    @Override
    public String getName() {
        return NAMES[specification];
    }

}
