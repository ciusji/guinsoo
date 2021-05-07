/*
 * Copyright 2004-2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.expression.function;

import java.text.DateFormatSymbols;
import java.util.Locale;

import org.guinsoo.engine.SessionLocal;
import org.guinsoo.expression.Expression;
import org.guinsoo.expression.TypedValueExpression;
import org.guinsoo.message.DbException;
import org.guinsoo.util.DateTimeUtils;
import org.guinsoo.value.TypeInfo;
import org.guinsoo.value.Value;
import org.guinsoo.value.ValueNull;
import org.guinsoo.value.ValueVarchar;

/**
 * A DAYNAME() or MONTHNAME() function.
 */
public final class DayMonthNameFunction extends Function1 {

    /**
     * DAYNAME() (non-standard).
     */
    public static final int DAYNAME = 0;

    /**
     * MONTHNAME() (non-standard).
     */
    public static final int MONTHNAME = DAYNAME + 1;

    private static final String[] NAMES = { //
            "DAYNAME", "MONTHNAME" //
    };

    /**
     * English names of months and week days.
     */
    private static volatile String[][] MONTHS_AND_WEEKS;

    private final int function;

    public DayMonthNameFunction(Expression arg, int function) {
        super(arg);
        this.function = function;
    }

    @Override
    public Value getValue(SessionLocal session) {
        Value v = arg.getValue(session);
        if (v == ValueNull.INSTANCE) {
            return ValueNull.INSTANCE;
        }
        long dateValue = DateTimeUtils.dateAndTimeFromValue(v, session)[0];
        String result;
        switch (function) {
        case DAYNAME:
            result = getMonthsAndWeeks(1)[DateTimeUtils.getDayOfWeek(dateValue, 0)];
            break;
        case MONTHNAME:
            result = getMonthsAndWeeks(0)[DateTimeUtils.monthFromDateValue(dateValue) - 1];
            break;
        default:
            throw DbException.getInternalError("function=" + function);
        }
        return ValueVarchar.get(result, session);
    }

    /**
     * Return names of month or weeks.
     *
     * @param field
     *            0 for months, 1 for weekdays
     * @return names of month or weeks
     */
    private static String[] getMonthsAndWeeks(int field) {
        String[][] result = MONTHS_AND_WEEKS;
        if (result == null) {
            result = new String[2][];
            DateFormatSymbols dfs = DateFormatSymbols.getInstance(Locale.ENGLISH);
            result[0] = dfs.getMonths();
            result[1] = dfs.getWeekdays();
            MONTHS_AND_WEEKS = result;
        }
        return result[field];
    }

    @Override
    public Expression optimize(SessionLocal session) {
        arg = arg.optimize(session);
        type = TypeInfo.getTypeInfo(Value.VARCHAR, 20, 0, null);
        if (arg.isConstant()) {
            return TypedValueExpression.getTypedIfNull(getValue(session), type);
        }
        return this;
    }

    @Override
    public String getName() {
        return NAMES[function];
    }

}
