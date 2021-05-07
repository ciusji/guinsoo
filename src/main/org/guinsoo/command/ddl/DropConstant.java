/*
 * Copyright 2004-2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.command.ddl;

import org.guinsoo.api.ErrorCode;
import org.guinsoo.command.CommandInterface;
import org.guinsoo.engine.Database;
import org.guinsoo.engine.SessionLocal;
import org.guinsoo.message.DbException;
import org.guinsoo.schema.Constant;
import org.guinsoo.schema.Schema;

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
