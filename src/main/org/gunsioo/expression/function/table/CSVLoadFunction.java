/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.expression.function.table;

import org.gunsioo.api.ErrorCode;
import org.gunsioo.engine.SessionLocal;
import org.gunsioo.expression.Expression;
import org.gunsioo.message.DbException;
import org.gunsioo.result.LocalResult;
import org.gunsioo.result.ResultInterface;
import org.gunsioo.schema.FunctionAlias.JavaMethod;
import org.gunsioo.tools.Csv;
import org.gunsioo.util.StringUtils;
import org.gunsioo.value.Value;
import org.gunsioo.value.ValueVarchar;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * A CSVLOAD function.
 *
 * <p>
 * TODO:
 * 1. csv load options
 * 2. csv load mode
 */
public final class CSVLoadFunction extends TableFunction {

    public CSVLoadFunction() {
        super(new Expression[4]);
    }

    @Override
    public ResultInterface getValue(SessionLocal session) {
        session.getUser().checkAdmin();
        String fileName = getValue(session, 0);
        String columnList = getValue(session, 1);
        Csv csv = new Csv();
        char fieldSeparator = csv.getFieldSeparatorRead();
        String[] columns = StringUtils.arraySplit(columnList, fieldSeparator, true);

        try {
            ResultSet rs = csv.read(fileName, columns, null);
            ResultSetMetaData meta = rs.getMetaData();
            int columnCount = meta.getColumnCount();
            LocalResult result = new LocalResult(session);
            while (rs.next()) {
                Value[] list = new Value[columnCount];
                for (int j = 0; j < columnCount; j++) {
                    list[j] = ValueVarchar.get(rs.getString(j + 1));
                }
                result.addRow(list);
            }
            return result;
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
        if (len != 1) {
            throw DbException.get(ErrorCode.INVALID_PARAMETER_COUNT_2, getName(), "only 1");
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
        char fieldSeparator = csv.getFieldSeparatorRead();
        String[] columns = StringUtils.arraySplit(columnList, fieldSeparator, true);
        ResultInterface result;
        try (ResultSet rs = csv.read(fileName, columns, null)) {
            result = JavaMethod.resultSetToResult(session, rs, 0);
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
        return "LOAD_CSV";
    }

    @Override
    public boolean isDeterministic() {
        return false;
    }

}
