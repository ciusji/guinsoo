/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.command.ddl;

import java.util.ArrayList;
import org.guinsoo.api.ErrorCode;
import org.guinsoo.command.CommandInterface;
import org.guinsoo.engine.Database;
import org.guinsoo.engine.SessionLocal;
import org.guinsoo.message.DbException;
import org.guinsoo.schema.Schema;
import org.guinsoo.schema.SchemaObject;

/**
 * This class represents the statement
 * ALTER SCHEMA RENAME
 */
public class AlterSchemaRename extends DefineCommand {

    private Schema oldSchema;
    private String newSchemaName;

    public AlterSchemaRename(SessionLocal session) {
        super(session);
    }

    public void setOldSchema(Schema schema) {
        oldSchema = schema;
    }

    public void setNewName(String name) {
        newSchemaName = name;
    }

    @Override
    public long update() {
        session.getUser().checkSchemaAdmin();
        Database db = session.getDatabase();
        if (!oldSchema.canDrop()) {
            throw DbException.get(ErrorCode.SCHEMA_CAN_NOT_BE_DROPPED_1, oldSchema.getName());
        }
        if (db.findSchema(newSchemaName) != null || newSchemaName.equals(oldSchema.getName())) {
            throw DbException.get(ErrorCode.SCHEMA_ALREADY_EXISTS_1, newSchemaName);
        }
        db.renameDatabaseObject(session, oldSchema, newSchemaName);
        ArrayList<SchemaObject> all = new ArrayList<>();
        for (Schema schema : db.getAllSchemas()) {
            schema.getAll(all);
            for (SchemaObject schemaObject : all) {
                db.updateMeta(session, schemaObject);
            }
            all.clear();
        }
        return 0;
    }

    @Override
    public int getType() {
        return CommandInterface.ALTER_SCHEMA_RENAME;
    }

}
