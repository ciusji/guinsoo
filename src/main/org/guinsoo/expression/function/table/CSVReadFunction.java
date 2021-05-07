/*
 * Copyright 2004-2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.expression.function.table;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.guinsoo.api.ErrorCode;
import org.guinsoo.engine.SessionLocal;
import org.guinsoo.expression.Expression;
import org.guinsoo.message.DbException;
import org.guinsoo.result.ResultInterface;
import org.guinsoo.schema.FunctionAlias;
import org.guinsoo.tools.Csv;
import org.guinsoo.util.StringUtils;
import org.guinsoo.expression.function.CSVWriteFunction;

/**
 * A CSVREAD function.
 *
 */
public final class CSVReadFunction extends TableFunction {

    public CSVReadFunction() {
        super(new Expression[4]);
    }

    @Override
    public ResultInterface getValue(SessionLocal session) {
        session.getUser().checkAdmin();
        String fileName = getValue(session, 0);
        String columnList = getValue(session, 1);
        Csv csv = new Csv();
        String options = getValue(session, 2);
        String charset = null;
        if (options != null && options.indexOf('=') >= 0) {
            charset = csv.setOptions(options);
        } else {
            charset = options;
            String fieldSeparatorRead = getValue(session, 3);
            String fieldDelimiter = getValue(session, 4);
            String escapeCharacter = getValue(session, 5);
            String nullString = getValue(session, 6);
            CSVWriteFunction.setCsvDelimiterEscape(csv, fieldSeparatorRead, fieldDelimiter, escapeCharacter);
            csv.setNullString(nullString);
        }
        char fieldSeparator = csv.getFieldSeparatorRead();
        String[] columns = StringUtils.arraySplit(columnList, fieldSeparator, true);
        try {
            return FunctionAlias.JavaMethod.resultSetToResult(session, csv.read(fileName, columns, charset), Integer.MAX_VALUE);
        } catch (SQLException e) {
            throw DbException.convert(e);
        }
    }

    private String getValue(SessionLocal session, int index) {
        return getValue(session, args, index);
    }

    @Override
    public void optimize(SessionLocal session) {
        super.optimize(session);
        int len = args.length;
        if (len < 1 || len > 7) {
            throw DbException.get(ErrorCode.INVALID_PARAMETER_COUNT_2, getName(), "1..7");
        }
    }

    @Override
    public ResultInterface getValueTemplate(SessionLocal session) {
        session.getUser().checkAdmin();
        String fileName = getValue(session, args, 0);
        if (fileName == null) {
            throw DbException.get(ErrorCode.PARAMETER_NOT_SET_1, "fileName");
        }
        String columnList = getValue(session, args, 1);
        Csv csv = new Csv();
        String options = getValue(session, args, 2);
        String charset = null;
        if (options != null && options.indexOf('=') >= 0) {
            charset = csv.setOptions(options);
        } else {
            charset = options;
            String fieldSeparatorRead = getValue(session, args, 3);
            String fieldDelimiter = getValue(session, args, 4);
            String escapeCharacter = getValue(session, args, 5);
            CSVWriteFunction.setCsvDelimiterEscape(csv, fieldSeparatorRead, fieldDelimiter, escapeCharacter);
        }
        char fieldSeparator = csv.getFieldSeparatorRead();
        String[] columns = StringUtils.arraySplit(columnList, fieldSeparator, true);
        ResultInterface result;
        try (ResultSet rs = csv.read(fileName, columns, charset)) {
            result = FunctionAlias.JavaMethod.resultSetToResult(session, rs, 0);
        } catch (SQLException e) {
            throw DbException.convert(e);
        } finally {
            csv.close();
        }
        return result;
    }

    private static String getValue(SessionLocal session, Expression[] args, int index) {
        return index < args.length ? args[index].getValue(session).getString() : null;
    }

    @Override
    public String getName() {
        return "READ_CSV";
    }

    @Override
    public boolean isDeterministic() {
        return false;
    }

}
