/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://h2database.com/html/license.html).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.expression.function;

import java.sql.Connection;
import java.sql.SQLException;

import org.gunsioo.api.ErrorCode;
import org.gunsioo.engine.SessionLocal;
import org.gunsioo.expression.Expression;
import org.gunsioo.expression.ExpressionVisitor;
import org.gunsioo.message.DbException;
import org.gunsioo.tools.Csv;
import org.gunsioo.value.TypeInfo;
import org.gunsioo.value.Value;
import org.gunsioo.value.ValueInteger;

/**
 * A CSVWRITE function.
 */
public final class CSVWriteFunction extends FunctionN {

    public CSVWriteFunction() {
        super(new Expression[4]);
    }

    @Override
    public Value getValue(SessionLocal session) {
        session.getUser().checkAdmin();
        Connection conn = session.createConnection(false);
        Csv csv = new Csv();
        String options = getValue(session, 2);
        String charset = null;
        if (options != null && options.indexOf('=') >= 0) {
            charset = csv.setOptions(options);
        } else {
            charset = options;
            String fieldSeparatorWrite = getValue(session, 3);
            String fieldDelimiter = getValue(session, 4);
            String escapeCharacter = getValue(session, 5);
            String nullString = getValue(session, 6);
            String lineSeparator = getValue(session, 7);
            setCsvDelimiterEscape(csv, fieldSeparatorWrite, fieldDelimiter, escapeCharacter);
            csv.setNullString(nullString);
            if (lineSeparator != null) {
                csv.setLineSeparator(lineSeparator);
            }
        }
        try {
            return ValueInteger.get(csv.write(conn, args[0].getValue(session).getString(),
                    args[1].getValue(session).getString(), charset));
        } catch (SQLException e) {
            throw DbException.convert(e);
        }
    }

    private String getValue(SessionLocal session, int index) {
        return index < args.length ? args[index].getValue(session).getString() : null;
    }

    /**
     * Sets delimiter options.
     *
     * @param csv
     *            the CSV utility instance
     * @param fieldSeparator
     *            the field separator
     * @param fieldDelimiter
     *            the field delimiter
     * @param escapeCharacter
     *            the escape character
     */
    public static void setCsvDelimiterEscape(Csv csv, String fieldSeparator, String fieldDelimiter,
            String escapeCharacter) {
        if (fieldSeparator != null) {
            csv.setFieldSeparatorWrite(fieldSeparator);
            if (!fieldSeparator.isEmpty()) {
                char fs = fieldSeparator.charAt(0);
                csv.setFieldSeparatorRead(fs);
            }
        }
        if (fieldDelimiter != null) {
            char fd = fieldDelimiter.isEmpty() ? 0 : fieldDelimiter.charAt(0);
            csv.setFieldDelimiter(fd);
        }
        if (escapeCharacter != null) {
            char ec = escapeCharacter.isEmpty() ? 0 : escapeCharacter.charAt(0);
            csv.setEscapeCharacter(ec);
        }
    }

    @Override
    public Expression optimize(SessionLocal session) {
        optimizeArguments(session, false);
        int len = args.length;
        if (len < 2 || len > 8) {
            throw DbException.get(ErrorCode.INVALID_PARAMETER_COUNT_2, getName(), "2..8");
        }
        type = TypeInfo.TYPE_INTEGER;
        return this;
    }

    @Override
    public String getName() {
        return "CSVWRITE";
    }

    @Override
    public boolean isEverything(ExpressionVisitor visitor) {
        return isEverythingNonDeterministic(visitor);
    }

}
