/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.command.dml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.ResultSet;

import org.guinsoo.command.CommandInterface;
import org.guinsoo.command.Prepared;
import org.guinsoo.engine.Database;
import org.guinsoo.engine.SessionLocal;
import org.guinsoo.expression.Expression;
import org.guinsoo.expression.ExpressionColumn;
import org.guinsoo.message.DbException;
import org.guinsoo.result.LocalResult;
import org.guinsoo.result.ResultInterface;
import org.guinsoo.table.Column;
import org.guinsoo.tools.Csv;
import org.guinsoo.util.Utils;
import org.guinsoo.value.TypeInfo;
import org.guinsoo.value.ValueInteger;
import org.guinsoo.value.ValueVarchar;

/**
 * This class represents the statement CALL.
 */
public class Help extends Prepared {

    private final String[] conditions;

    private final Expression[] expressions;

    public Help(SessionLocal session, String[] conditions) {
        super(session);
        this.conditions = conditions;
        Database db = session.getDatabase();
        expressions = new Expression[] { //
                new ExpressionColumn(db, new Column("ID", TypeInfo.TYPE_INTEGER)), //
                new ExpressionColumn(db, new Column("SECTION", TypeInfo.TYPE_VARCHAR)), //
                new ExpressionColumn(db, new Column("TOPIC", TypeInfo.TYPE_VARCHAR)), //
                new ExpressionColumn(db, new Column("SYNTAX", TypeInfo.TYPE_VARCHAR)), //
                new ExpressionColumn(db, new Column("TEXT", TypeInfo.TYPE_VARCHAR)), //
        };
    }

    @Override
    public ResultInterface queryMeta() {
        LocalResult result = new LocalResult(session, expressions, 5, 5);
        result.done();
        return result;
    }

    @Override
    public ResultInterface query(long maxrows) {
        LocalResult result = new LocalResult(session, expressions, 5, 5);
        try {
            ResultSet rs = getTable();
            loop: for (int i = 0; rs.next(); i++) {
                String topic = rs.getString(2).trim();
                for (String condition : conditions) {
                    if (!topic.contains(condition)) {
                        continue loop;
                    }
                }
                result.addRow(
                        // ID
                        ValueInteger.get(i),
                        // SECTION
                        ValueVarchar.get(rs.getString(1).trim(), session),
                        // TOPIC
                        ValueVarchar.get(topic, session),
                        // SYNTAX
                        ValueVarchar.get(rs.getString(3).trim(), session),
                        // TEXT
                        ValueVarchar.get(rs.getString(4).trim(), session));
            }
        } catch (Exception e) {
            throw DbException.convert(e);
        }
        result.done();
        return result;
    }

    /**
     * Returns HELP table.
     *
     * @return HELP table
     * @throws IOException
     *             on I/O exception
     */
    public static ResultSet getTable() throws IOException {
        Reader reader = new InputStreamReader(new ByteArrayInputStream(Utils.getResource("/org/guinsoo/res/help.csv")));
        Csv csv = new Csv();
        csv.setLineCommentCharacter('#');
        return csv.read(reader, null);
    }

    @Override
    public boolean isQuery() {
        return true;
    }

    @Override
    public boolean isTransactional() {
        return true;
    }

    @Override
    public boolean isReadOnly() {
        return true;
    }

    @Override
    public int getType() {
        return CommandInterface.CALL;
    }

    @Override
    public boolean isCacheable() {
        return true;
    }

}
