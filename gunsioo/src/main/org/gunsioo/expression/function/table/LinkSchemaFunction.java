/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://h2database.com/html/license.html).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.expression.function.table;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.gunsioo.api.ErrorCode;
import org.gunsioo.engine.SessionLocal;
import org.gunsioo.expression.Expression;
import org.gunsioo.message.DbException;
import org.gunsioo.result.ResultInterface;
import org.gunsioo.result.SimpleResult;
import org.gunsioo.util.JdbcUtils;
import org.gunsioo.util.StringUtils;
import org.gunsioo.value.TypeInfo;
import org.gunsioo.value.ValueVarchar;

/**
 * A LINK_SCHEMA function.
 */
public final class LinkSchemaFunction extends TableFunction {

    public LinkSchemaFunction() {
        super(new Expression[6]);
    }

    @Override
    public ResultInterface getValue(SessionLocal session) {
        session.getUser().checkAdmin();
        String targetSchema = getValue(session, 0);
        String driver = getValue(session, 1);
        String url = getValue(session, 2);
        String user = getValue(session, 3);
        String password = getValue(session, 4);
        String sourceSchema = getValue(session, 5);
        if (targetSchema == null || driver == null || url == null || user == null || password == null
                || sourceSchema == null) {
            return getValueTemplate(session);
        }
        Connection conn = session.createConnection(false);
        Connection c2 = null;
        Statement stat = null;
        ResultSet rs = null;
        SimpleResult result = new SimpleResult();
        result.addColumn("TABLE_NAME", TypeInfo.TYPE_VARCHAR);
        try {
            c2 = JdbcUtils.getConnection(driver, url, user, password);
            stat = conn.createStatement();
            stat.execute(StringUtils.quoteIdentifier(new StringBuilder("CREATE SCHEMA IF NOT EXISTS "), targetSchema)
                    .toString());
            // Workaround for PostgreSQL to avoid index names
            if (url.startsWith("jdbc:postgresql:")) {
                rs = c2.getMetaData().getTables(null, sourceSchema, null,
                        new String[] { "TABLE", "LINKED TABLE", "VIEW", "EXTERNAL" });
            } else {
                rs = c2.getMetaData().getTables(null, sourceSchema, null, null);
            }
            while (rs.next()) {
                String table = rs.getString("TABLE_NAME");
                StringBuilder buff = new StringBuilder();
                buff.append("DROP TABLE IF EXISTS ");
                StringUtils.quoteIdentifier(buff, targetSchema).append('.');
                StringUtils.quoteIdentifier(buff, table);
                stat.execute(buff.toString());
                buff.setLength(0);
                buff.append("CREATE LINKED TABLE ");
                StringUtils.quoteIdentifier(buff, targetSchema).append('.');
                StringUtils.quoteIdentifier(buff, table).append('(');
                StringUtils.quoteStringSQL(buff, driver).append(", ");
                StringUtils.quoteStringSQL(buff, url).append(", ");
                StringUtils.quoteStringSQL(buff, user).append(", ");
                StringUtils.quoteStringSQL(buff, password).append(", ");
                StringUtils.quoteStringSQL(buff, sourceSchema).append(", ");
                StringUtils.quoteStringSQL(buff, table).append(')');
                stat.execute(buff.toString());
                result.addRow(ValueVarchar.get(table, session));
            }
        } catch (SQLException e) {
            result.close();
            throw DbException.convert(e);
        } finally {
            JdbcUtils.closeSilently(rs);
            JdbcUtils.closeSilently(c2);
            JdbcUtils.closeSilently(stat);
        }
        return result;
    }

    private String getValue(SessionLocal session, int index) {
        return args[index].getValue(session).getString();
    }

    @Override
    public void optimize(SessionLocal session) {
        super.optimize(session);
        int len = args.length;
        if (len != 6) {
            throw DbException.get(ErrorCode.INVALID_PARAMETER_COUNT_2, getName(), "6");
        }
    }

    @Override
    public ResultInterface getValueTemplate(SessionLocal session) {
        SimpleResult result = new SimpleResult();
        result.addColumn("TABLE_NAME", TypeInfo.TYPE_VARCHAR);
        return result;
    }

    @Override
    public String getName() {
        return "LINK_SCHEMA";
    }

    @Override
    public boolean isDeterministic() {
        return false;
    }

}
