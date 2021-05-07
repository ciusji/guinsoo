/*
 * Copyright 2004-2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.index;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.guinsoo.engine.SessionLocal;
import org.guinsoo.message.DbException;
import org.guinsoo.result.Row;
import org.guinsoo.result.SearchRow;
import org.guinsoo.table.TableLink;
import org.guinsoo.value.ValueToObjectConverter2;

/**
 * The cursor implementation for the linked index.
 */
public class LinkedCursor implements Cursor {

    private final TableLink tableLink;
    private final PreparedStatement prep;
    private final String sql;
    private final SessionLocal session;
    private final ResultSet rs;
    private Row current;

    LinkedCursor(TableLink tableLink, ResultSet rs, SessionLocal session,
            String sql, PreparedStatement prep) {
        this.session = session;
        this.tableLink = tableLink;
        this.rs = rs;
        this.sql = sql;
        this.prep = prep;
    }

    @Override
    public Row get() {
        return current;
    }

    @Override
    public SearchRow getSearchRow() {
        return current;
    }

    @Override
    public boolean next() {
        try {
            boolean result = rs.next();
            if (!result) {
                rs.close();
                tableLink.reusePreparedStatement(prep, sql);
                current = null;
                return false;
            }
        } catch (SQLException e) {
            throw DbException.convert(e);
        }
        current = tableLink.getTemplateRow();
        for (int i = 0; i < current.getColumnCount(); i++) {
            current.setValue(i, ValueToObjectConverter2.readValue(session, rs, i + 1,
                    tableLink.getColumn(i).getType().getValueType()));
        }
        return true;
    }

    @Override
    public boolean previous() {
        throw DbException.getInternalError(toString());
    }

}
