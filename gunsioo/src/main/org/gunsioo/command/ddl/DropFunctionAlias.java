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
import org.gunsioo.schema.FunctionAlias;
import org.gunsioo.schema.Schema;

/**
 * This class represents the statement
 * DROP ALIAS
 */
public class DropFunctionAlias extends SchemaOwnerCommand {

    private String aliasName;
    private boolean ifExists;

    public DropFunctionAlias(SessionLocal session, Schema schema) {
        super(session, schema);
    }

    @Override
    long update(Schema schema) {
        Database db = session.getDatabase();
        FunctionAlias functionAlias = schema.findFunction(aliasName);
        if (functionAlias == null) {
            if (!ifExists) {
                throw DbException.get(ErrorCode.FUNCTION_ALIAS_NOT_FOUND_1, aliasName);
            }
        } else {
            db.removeSchemaObject(session, functionAlias);
        }
        return 0;
    }

    public void setAliasName(String name) {
        this.aliasName = name;
    }

    public void setIfExists(boolean ifExists) {
        this.ifExists = ifExists;
    }

    @Override
    public int getType() {
        return CommandInterface.DROP_ALIAS;
    }

}
