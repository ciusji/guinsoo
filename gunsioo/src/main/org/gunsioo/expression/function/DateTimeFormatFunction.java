/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.expression.function;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

import org.gunsioo.api.ErrorCode;
import org.gunsioo.engine.SessionLocal;
import org.gunsioo.expression.Expression;
import org.gunsioo.expression.TypedValueExpression;
import org.gunsioo.message.DbException;
import org.gunsioo.util.DateTimeUtils;
import org.gunsioo.util.LegacyDateTimeUtils;
import org.gunsioo.value.TypeInfo;
import org.gunsioo.value.Value;
import org.gunsioo.value.ValueTimestampTimeZone;
import org.gunsioo.value.ValueVarchar;

/**
 * A date-time format function.
 */
public final class DateTimeFormatFunction extends FunctionN {

    /**
     * FORMATDATETIME() (non-standard).
     */
    public static final int FORMATDATETIME = 0;

    /**
     * PARSEDATETIME() (non-standard).
     */
    public static final int PARSEDATETIME = FORMATDATETIME + 1;

    private static final String[] NAMES = { //
            "FORMATDATETIME", "PARSEDATETIME" //
    };

    private final int function;

    public DateTimeFormatFunction(int function) {
        super(new Expression[4]);
        this.function = function;
    }

    @Override
    public Value getValue(SessionLocal session, Value v1, Value v2, Value v3) {
        String format = v2.getString(), locale, tz;
        if (v3 != null) {
            locale = v3.getString();
            tz = args.length > 3 ? args[3].getValue(session).getString() : null;
        } else {
            tz = locale = null;
        }
        switch (function) {
        case FORMATDATETIME: {
            if (v1 instanceof ValueTimestampTimeZone) {
                tz = DateTimeUtils
                        .timeZoneNameFromOffsetSeconds(((ValueTimestampTimeZone) v1).getTimeZoneOffsetSeconds());
            }
            v1 = ValueVarchar.get(
                    formatDateTime(LegacyDateTimeUtils.toTimestamp(session, null, v1), format, locale, tz), session);
            break;
        }
        case PARSEDATETIME: {
            v1 = LegacyDateTimeUtils.fromTimestamp(session, parseDateTime(v1.getString(), format, locale, tz) //
                    .getTime(), 0);
            break;
        }
        default:
            throw DbException.getInternalError("function=" + function);
        }
        return v1;
    }

    /**
     * Formats a date using a format string.
     *
     * @param date
     *            the date to format
     * @param format
     *            the format string
     * @param locale
     *            the locale
     * @param timeZone
     *            the timezone
     * @return the formatted date
     */
    public static String formatDateTime(java.util.Date date, String format, String locale, String timeZone) {
        SimpleDateFormat dateFormat = getDateFormat(format, locale, timeZone);
        synchronized (dateFormat) {
            return dateFormat.format(date);
        }
    }

    /**
     * Parses a date using a format string.
     *
     * @param date
     *            the date to parse
     * @param format
     *            the parsing format
     * @param locale
     *            the locale
     * @param timeZone
     *            the timeZone
     * @return the parsed date
     */
    public static java.util.Date parseDateTime(String date, String format, String locale, String timeZone) {
        SimpleDateFormat dateFormat = getDateFormat(format, locale, timeZone);
        try {
            synchronized (dateFormat) {
                return dateFormat.parse(date);
            }
        } catch (Exception e) {
            // ParseException
            throw DbException.get(ErrorCode.PARSE_ERROR_1, e, date);
        }
    }

    private static SimpleDateFormat getDateFormat(String format, String locale, String timeZone) {
        try {
            // currently, a new instance is create for each call
            // however, could cache the last few instances
            SimpleDateFormat df;
            if (locale == null) {
                df = new SimpleDateFormat(format);
            } else {
                Locale l = new Locale(locale);
                df = new SimpleDateFormat(format, l);
            }
            if (timeZone != null) {
                df.setTimeZone(TimeZone.getTimeZone(timeZone));
            }
            return df;
        } catch (Exception e) {
            throw DbException.get(ErrorCode.PARSE_ERROR_1, e, format + '/' + locale + '/' + timeZone);
        }
    }

    @Override
    public Expression optimize(SessionLocal session) {
        boolean allConst = optimizeArguments(session, true);
        switch (function) {
        case FORMATDATETIME:
            type = TypeInfo.TYPE_VARCHAR;
            break;
        case PARSEDATETIME:
            type = TypeInfo.TYPE_TIMESTAMP;
            break;
        default:
            throw DbException.getInternalError("function=" + function);
        }
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
