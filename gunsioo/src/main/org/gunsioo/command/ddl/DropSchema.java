/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://h2database.com/html/license.html).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.command.ddl;

import java.util.ArrayList;
import org.gunsioo.api.ErrorCode;
import org.gunsioo.command.CommandInterface;
import org.gunsioo.constraint.ConstraintActionType;
import org.gunsioo.engine.Database;
import org.gunsioo.engine.SessionLocal;
import org.gunsioo.message.DbException;
import org.gunsioo.schema.Schema;
import org.gunsioo.schema.SchemaObject;

/**
 * This class represents the statement
 * DROP SCHEMA
 */
public class DropSchema extends DefineCommand {

    private String schemaName;
    private boolean ifExists;
    private ConstraintActionType dropAction;

    public DropSchema(SessionLocal session) {
        super(session);
        dropAction = session.getDatabase().getSettings().dropRestrict ?
                ConstraintActionType.RESTRICT : ConstraintActionType.CASCADE;
    }

    public void setSchemaName(String name) {
        this.schemaName = name;
    }

    @Override
    public long update() {
        Database db = session.getDatabase();
        Schema schema = db.findSchema(schemaName);
        if (schema == null) {
            if (!ifExists) {
                throw DbException.get(ErrorCode.SCHEMA_NOT_FOUND_1, schemaName);
            }
        } else {
            session.getUser().checkSchemaOwner(schema);
            if (!schema.canDrop()) {
                throw DbException.get(ErrorCode.SCHEMA_CAN_NOT_BE_DROPPED_1, schemaName);
            }
            if (dropAction == ConstraintActionType.RESTRICT && !schema.isEmpty()) {
                ArrayList<SchemaObject> all = schema.getAll(null);
                int size = all.size();
                if (size > 0) {
                    StringBuilder builder = new StringBuilder();
                    for (int i = 0; i < size; i++) {
                        if (i > 0) {
                            builder.append(", ");
                        }
                        builder.append(all.get(i).getName());
                    }
                    throw DbException.get(ErrorCode.CANNOT_DROP_2, schemaName, builder.toString());
                }
            }
            db.removeDatabaseObject(session, schema);
        }
        return 0;
    }

    public void setIfExists(boolean ifExists) {
        this.ifExists = ifExists;
    }

    public void setDropAction(ConstraintActionType dropAction) {
        this.dropAction = dropAction;
    }

    @Override
    public int getType() {
        return CommandInterface.DROP_SCHEMA;
    }

}
