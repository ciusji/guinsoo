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
import org.guinsoo.schema.Schema;
import org.guinsoo.schema.Sequence;
import org.guinsoo.table.Column;

/**
 * This class represents the statement ALTER SEQUENCE.
 */
public class AlterSequence extends SchemaOwnerCommand {

    private boolean ifExists;

    private Column column;

    private Boolean always;

    private String sequenceName;

    private Sequence sequence;

    private SequenceOptions options;

    public AlterSequence(SessionLocal session, Schema schema) {
        super(session, schema);
        transactional = true;
    }

    public void setIfExists(boolean b) {
        ifExists = b;
    }

    public void setSequenceName(String sequenceName) {
        this.sequenceName = sequenceName;
    }

    public void setOptions(SequenceOptions options) {
        this.options = options;
    }

    @Override
    public boolean isTransactional() {
        return true;
    }

    /**
     * Set the column
     *
     * @param column the column
     * @param always whether value should be always generated, or null if "set
     *            generated is not specified
     */
    public void setColumn(Column column, Boolean always) {
        this.column = column;
        this.always = always;
        sequence = column.getSequence();
        if (sequence == null && !ifExists) {
            throw DbException.get(ErrorCode.SEQUENCE_NOT_FOUND_1, column.getTraceSQL());
        }
    }

    @Override
    long update(Schema schema) {
        if (sequence == null) {
            sequence = schema.findSequence(sequenceName);
            if (sequence == null) {
                if (!ifExists) {
                    throw DbException.get(ErrorCode.SEQUENCE_NOT_FOUND_1, sequenceName);
                }
                return 0;
            }
        }
        if (column != null) {
            session.getUser().checkTableRight(column.getTable(), Right.SCHEMA_OWNER);
        }
        options.setDataType(sequence.getDataType());
        Long startValue = options.getStartValue(session);
        sequence.modify(
                options.getRestartValue(session, startValue != null ? startValue : sequence.getStartValue()),
                startValue,
                options.getMinValue(sequence, session), options.getMaxValue(sequence, session),
                options.getIncrement(session), options.getCycle(), options.getCacheSize(session));
        sequence.flush(session);
        if (column != null && always != null) {
            column.setSequence(sequence, always);
            session.getDatabase().updateMeta(session, column.getTable());
        }
        return 0;
    }

    @Override
    public int getType() {
        return CommandInterface.ALTER_SEQUENCE;
    }

}
