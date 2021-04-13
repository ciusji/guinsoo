/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://h2database.com/html/license.html).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.index;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.gunsioo.engine.SessionLocal;
import org.gunsioo.message.DbException;
import org.gunsioo.result.Row;
import org.gunsioo.result.SearchRow;
import org.gunsioo.table.TableLink;
import org.gunsioo.value.ValueToObjectConverter2;

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
