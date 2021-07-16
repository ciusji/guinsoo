/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.expression;

import static org.guinsoo.util.DateTimeUtils.NANOS_PER_DAY;

import org.guinsoo.engine.SessionLocal;
import org.guinsoo.message.DbException;
import org.guinsoo.util.DateTimeUtils;
import org.guinsoo.value.TypeInfo;
import org.guinsoo.value.Value;
import org.guinsoo.value.ValueDate;
import org.guinsoo.value.ValueNull;
import org.guinsoo.value.ValueTime;
import org.guinsoo.value.ValueTimeTimeZone;
import org.guinsoo.value.ValueTimestamp;
import org.guinsoo.value.ValueTimestampTimeZone;

/**
 * A compatibility mathematical operation with datetime values.
 */
public class CompatibilityDatePlusTimeOperation extends Operation2 {

    public CompatibilityDatePlusTimeOperation(Expression left, Expression right) {
        super(left, right);
        TypeInfo l = left.getType(), r = right.getType();
        int t;
        switch (l.getValueType()) {
        case Value.TIMESTAMP_TZ:
            if (r.getValueType() == Value.TIME_TZ) {
                throw DbException.getUnsupportedException("TIMESTAMP WITH TIME ZONE + TIME WITH TIME ZONE");
            }
            //$FALL-THROUGH$
        case Value.TIME:
            t = r.getValueType() == Value.DATE ? Value.TIMESTAMP : l.getValueType();
            break;
        case Value.TIME_TZ:
            if (r.getValueType() == Value.TIME_TZ) {
                throw DbException.getUnsupportedException("TIME WITH TIME ZONE + TIME WITH TIME ZONE");
            }
            t = r.getValueType() == Value.DATE ? Value.TIMESTAMP_TZ : l.getValueType();
            break;
        case Value.TIMESTAMP:
            t = r.getValueType() == Value.TIME_TZ ? Value.TIMESTAMP_TZ : Value.TIMESTAMP;
            break;
        default:
            throw DbException.getUnsupportedException(
                    Value.getTypeName(l.getValueType()) + " + " + Value.getTypeName(r.getValueType()));
        }
        type = TypeInfo.getTypeInfo(t, 0L, Math.max(l.getScale(), r.getScale()), null);
    }

    @Override
    public boolean needParentheses() {
        return true;
    }

    @Override
    public StringBuilder getUnenclosedSQL(StringBuilder builder, int sqlFlags) {
        left.getSQL(builder, sqlFlags, AUTO_PARENTHESES).append(" + ");
        return right.getSQL(builder, sqlFlags, AUTO_PARENTHESES);
    }

    @Override
    public Value getValue(SessionLocal session) {
        Value l = left.getValue(session);
        Value r = right.getValue(session);
        if (l == ValueNull.INSTANCE || r == ValueNull.INSTANCE) {
            return ValueNull.INSTANCE;
        }
        switch (l.getValueType()) {
        case Value.TIME:
            if (r.getValueType() == Value.DATE) {
                return ValueTimestamp.fromDateValueAndNanos(((ValueDate) r).getDateValue(), //
                        ((ValueTime) l).getNanos());
            }
            break;
        case Value.TIME_TZ:
            if (r.getValueType() == Value.DATE) {
                ValueTimeTimeZone t = (ValueTimeTimeZone) l;
                return ValueTimestampTimeZone.fromDateValueAndNanos(((ValueDate) r).getDateValue(), t.getNanos(),
                        t.getTimeZoneOffsetSeconds());
            }
            break;
        case Value.TIMESTAMP: {
            if (r.getValueType() == Value.TIME_TZ) {
                ValueTimestamp ts = (ValueTimestamp) l;
                l = ValueTimestampTimeZone.fromDateValueAndNanos(ts.getDateValue(), ts.getTimeNanos(),
                        ((ValueTimeTimeZone) r).getTimeZoneOffsetSeconds());
            }
            break;
        }
        }
        long[] a = DateTimeUtils.dateAndTimeFromValue(l, session);
        long dateValue = a[0], timeNanos = a[1]
                + (r instanceof ValueTime ? ((ValueTime) r).getNanos() : ((ValueTimeTimeZone) r).getNanos());
        if (timeNanos >= NANOS_PER_DAY) {
            timeNanos -= NANOS_PER_DAY;
            dateValue = DateTimeUtils.incrementDateValue(dateValue);
        }
        return DateTimeUtils.dateTimeToValue(l, dateValue, timeNanos);
    }

    @Override
    public Expression optimize(SessionLocal session) {
        left = left.optimize(session);
        right = right.optimize(session);
        if (left.isConstant() && right.isConstant()) {
            return ValueExpression.get(getValue(session));
        }
        return this;
    }

}
