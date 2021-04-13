/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://h2database.com/html/license.html).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.command.ddl;

import org.gunsioo.api.ErrorCode;
import org.gunsioo.command.CommandInterface;
import org.gunsioo.engine.Database;
import org.gunsioo.engine.SessionLocal;
import org.gunsioo.message.DbException;
import org.gunsioo.schema.Schema;
import org.gunsioo.schema.Sequence;

/**
 * This class represents the statement CREATE SEQUENCE.
 */
public class CreateSequence extends SchemaOwnerCommand {

    private String sequenceName;

    private boolean ifNotExists;

    private SequenceOptions options;

    private boolean belongsToTable;

    public CreateSequence(SessionLocal session, Schema schema) {
        super(session, schema);
        transactional = true;
    }

    public void setSequenceName(String sequenceName) {
        this.sequenceName = sequenceName;
    }

    public void setIfNotExists(boolean ifNotExists) {
        this.ifNotExists = ifNotExists;
    }

    public void setOptions(SequenceOptions options) {
        this.options = options;
    }

    @Override
    long update(Schema schema) {
        Database db = session.getDatabase();
        if (schema.findSequence(sequenceName) != null) {
            if (ifNotExists) {
                return 0;
            }
            throw DbException.get(ErrorCode.SEQUENCE_ALREADY_EXISTS_1, sequenceName);
        }
        int id = getObjectId();
        Sequence sequence = new Sequence(session, schema, id, sequenceName, options, belongsToTable);
        db.addSchemaObject(session, sequence);
        return 0;
    }

    public void setBelongsToTable(boolean belongsToTable) {
        this.belongsToTable = belongsToTable;
    }

    @Override
    public int getType() {
        return CommandInterface.CREATE_SEQUENCE;
    }

}
