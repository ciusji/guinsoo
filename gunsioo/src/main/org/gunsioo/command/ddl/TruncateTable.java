/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://h2database.com/html/license.html).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.command.ddl;

import org.gunsioo.api.ErrorCode;
import org.gunsioo.command.CommandInterface;
import org.gunsioo.engine.Right;
import org.gunsioo.engine.SessionLocal;
import org.gunsioo.message.DbException;
import org.gunsioo.schema.Sequence;
import org.gunsioo.table.Column;
import org.gunsioo.table.Table;

/**
 * This class represents the statement
 * TRUNCATE TABLE
 */
public class TruncateTable extends DefineCommand {

    private Table table;

    private boolean restart;

    public TruncateTable(SessionLocal session) {
        super(session);
    }

    public void setTable(Table table) {
        this.table = table;
    }

    public void setRestart(boolean restart) {
        this.restart = restart;
    }

    @Override
    public long update() {
        if (!table.canTruncate()) {
            throw DbException.get(ErrorCode.CANNOT_TRUNCATE_1, table.getTraceSQL());
        }
        session.getUser().checkTableRight(table, Right.DELETE);
        table.lock(session, true, true);
        long result = table.truncate(session);
        if (restart) {
            for (Column column : table.getColumns()) {
                Sequence sequence = column.getSequence();
                if (sequence != null) {
                    sequence.modify(sequence.getStartValue(), null, null, null, null, null, null);
                    session.getDatabase().updateMeta(session, sequence);
                }
            }
        }
        return result;
    }

    @Override
    public int getType() {
        return CommandInterface.TRUNCATE_TABLE;
    }

}
