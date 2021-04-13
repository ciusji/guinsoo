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
import org.gunsioo.schema.Constant;
import org.gunsioo.schema.Schema;

/**
 * This class represents the statement
 * DROP CONSTANT
 */
public class DropConstant extends SchemaOwnerCommand {

    private String constantName;
    private boolean ifExists;

    public DropConstant(SessionLocal session, Schema schema) {
        super(session, schema);
    }

    public void setIfExists(boolean b) {
        ifExists = b;
    }

    public void setConstantName(String constantName) {
        this.constantName = constantName;
    }

    @Override
    long update(Schema schema) {
        Database db = session.getDatabase();
        Constant constant = schema.findConstant(constantName);
        if (constant == null) {
            if (!ifExists) {
                throw DbException.get(ErrorCode.CONSTANT_NOT_FOUND_1, constantName);
            }
        } else {
            db.removeSchemaObject(session, constant);
        }
        return 0;
    }

    @Override
    public int getType() {
        return CommandInterface.DROP_CONSTANT;
    }

}
