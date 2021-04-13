/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://h2database.com/html/license.html).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.expression.function;

import org.gunsioo.api.ErrorCode;
import org.gunsioo.engine.SessionLocal;
import org.gunsioo.expression.Expression;
import org.gunsioo.expression.TypedValueExpression;
import org.gunsioo.message.DbException;
import org.gunsioo.util.StringUtils;
import org.gunsioo.value.TypeInfo;
import org.gunsioo.value.Value;
import org.gunsioo.value.ValueNull;
import org.gunsioo.value.ValueVarchar;

/**
 * An XML function.
 */
public final class XMLFunction extends FunctionN {

    /**
     * XMLATTR() (non-standard).
     */
    public static final int XMLATTR = 0;

    /**
     * XMLCDATA() (non-standard).
     */
    public static final int XMLCDATA = XMLATTR + 1;

    /**
     * XMLCOMMENT() (non-standard).
     */
    public static final int XMLCOMMENT = XMLCDATA + 1;

    /**
     * XMLNODE() (non-standard).
     */
    public static final int XMLNODE = XMLCOMMENT + 1;

    /**
     * XMLSTARTDOC() (non-standard).
     */
    public static final int XMLSTARTDOC = XMLNODE + 1;

    /**
     * XMLTEXT() (non-standard).
     */
    public static final int XMLTEXT = XMLSTARTDOC + 1;

    private static final String[] NAMES = { //
            "XMLATTR", "XMLCDATA", "XMLCOMMENT", "XMLNODE", "XMLSTARTDOC", "XMLTEXT" //
    };

    private final int function;

    public XMLFunction(int function) {
        super(new Expression[4]);
        this.function = function;
    }

    @Override
    public Value getValue(SessionLocal session) {
        switch (function) {
        case XMLNODE:
            return xmlNode(session);
        case XMLSTARTDOC:
            return ValueVarchar.get(StringUtils.xmlStartDoc(), session);
        default:
            return super.getValue(session);
        }
    }

    private Value xmlNode(SessionLocal session) {
        Value v1 = args[0].getValue(session);
        if (v1 == ValueNull.INSTANCE) {
            return ValueNull.INSTANCE;
        }
        int length = args.length;
        String attr = length >= 2 ? args[1].getValue(session).getString() : null;
        String content = length >= 3 ? args[2].getValue(session).getString() : null;
        boolean indent;
        if (length >= 4) {
            Value v4 = args[3].getValue(session);
            if (v4 == ValueNull.INSTANCE) {
                return ValueNull.INSTANCE;
            }
            indent = v4.getBoolean();
        } else {
            indent = true;
        }
        return ValueVarchar.get(StringUtils.xmlNode(v1.getString(), attr, content, indent), session);
    }

    @Override
    protected Value getValue(SessionLocal session, Value v1, Value v2, Value v3) {
        switch (function) {
        case XMLATTR:
            v1 = ValueVarchar.get(StringUtils.xmlAttr(v1.getString(), v2.getString()), session);
            break;
        case XMLCDATA:
            v1 = ValueVarchar.get(StringUtils.xmlCData(v1.getString()), session);
            break;
        case XMLCOMMENT:
            v1 = ValueVarchar.get(StringUtils.xmlComment(v1.getString()), session);
            break;
        case XMLTEXT:
            v1 = ValueVarchar.get(StringUtils.xmlText(v1.getString(), v2 != null && v2.getBoolean()), session);
            break;
        default:
            throw DbException.getInternalError("function=" + function);
        }
        return v1;
    }

    @Override
    public Expression optimize(SessionLocal session) {
        boolean allConst = optimizeArguments(session, true);
        int min, max;
        switch (function) {
        case XMLATTR:
            max = min = 2;
            break;
        case XMLNODE:
            min = 1;
            max = 4;
            break;
        case XMLCDATA:
        case XMLCOMMENT:
            max = min = 1;
            break;
        case XMLSTARTDOC:
            max = min = 0;
            break;
        case XMLTEXT:
            min = 1;
            max = 2;
            break;
        default:
            throw DbException.getInternalError("function=" + function);
        }
        int len = args.length;
        if (len < min || len > max) {
            throw DbException.get(ErrorCode.INVALID_PARAMETER_COUNT_2, getName(), min + ".." + max);
        }
        type = TypeInfo.TYPE_VARCHAR;
        if (allConst) {
            return TypedValueExpression.getTypedIfNull(getValue(session), type);
        }
        return this;
    }

    @Override
    public String getName() {
        return NAMES[function];
    }

}
