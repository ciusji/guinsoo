/*
 * Copyright 2004-2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.command.ddl;

import org.guinsoo.api.ErrorCode;
import org.guinsoo.command.CommandInterface;
import org.guinsoo.engine.Right;
import org.guinsoo.engine.SessionLocal;
import org.guinsoo.message.DbException;
import org.guinsoo.schema.Sequence;
import org.guinsoo.table.Column;
import org.guinsoo.table.Table;

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
