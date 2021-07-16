/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.command.ddl;

import org.guinsoo.api.ErrorCode;
import org.guinsoo.command.CommandInterface;
import org.guinsoo.engine.Database;
import org.guinsoo.engine.SessionLocal;
import org.guinsoo.message.DbException;
import org.guinsoo.schema.Schema;
import org.guinsoo.schema.Sequence;

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
