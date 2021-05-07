/*
 * Copyright 2004-2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.command.ddl;

import java.util.ArrayList;
import org.guinsoo.api.ErrorCode;
import org.guinsoo.command.CommandInterface;
import org.guinsoo.engine.Database;
import org.guinsoo.engine.RightOwner;
import org.guinsoo.engine.SessionLocal;
import org.guinsoo.message.DbException;
import org.guinsoo.schema.Schema;

/**
 * This class represents the statement
 * CREATE SCHEMA
 */
public class CreateSchema extends DefineCommand {

    private String schemaName;
    private String authorization;
    private boolean ifNotExists;
    private ArrayList<String> tableEngineParams;

    public CreateSchema(SessionLocal session) {
        super(session);
    }

    public void setIfNotExists(boolean ifNotExists) {
        this.ifNotExists = ifNotExists;
    }

    @Override
    public long update() {
        session.getUser().checkSchemaAdmin();
        Database db = session.getDatabase();
        RightOwner owner = db.findUserOrRole(authorization);
        if (owner == null) {
            throw DbException.get(ErrorCode.USER_OR_ROLE_NOT_FOUND_1, authorization);
        }
        if (db.findSchema(schemaName) != null) {
            if (ifNotExists) {
                return 0;
            }
            throw DbException.get(ErrorCode.SCHEMA_ALREADY_EXISTS_1, schemaName);
        }
        int id = getObjectId();
        Schema schema = new Schema(db, id, schemaName, owner, false);
        schema.setTableEngineParams(tableEngineParams);
        db.addDatabaseObject(session, schema);
        return 0;
    }

    public void setSchemaName(String name) {
        this.schemaName = name;
    }

    public void setAuthorization(String userName) {
        this.authorization = userName;
    }

    public void setTableEngineParams(ArrayList<String> tableEngineParams) {
        this.tableEngineParams = tableEngineParams;
    }

    @Override
    public int getType() {
        return CommandInterface.CREATE_SCHEMA;
    }

}
