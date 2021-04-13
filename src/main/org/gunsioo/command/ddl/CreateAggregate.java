/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.command.ddl;

import org.gunsioo.api.ErrorCode;
import org.gunsioo.command.CommandInterface;
import org.gunsioo.engine.Database;
import org.gunsioo.engine.SessionLocal;
import org.gunsioo.message.DbException;
import org.gunsioo.schema.Schema;
import org.gunsioo.schema.UserAggregate;

/**
 * This class represents the statement
 * CREATE AGGREGATE
 */
public class CreateAggregate extends SchemaCommand {

    private String name;
    private String javaClassMethod;
    private boolean ifNotExists;
    private boolean force;

    public CreateAggregate(SessionLocal session, Schema schema) {
        super(session, schema);
    }

    @Override
    public long update() {
        session.getUser().checkAdmin();
        Database db = session.getDatabase();
        Schema schema = getSchema();
        if (schema.findFunctionOrAggregate(name) != null) {
            if (!ifNotExists) {
                throw DbException.get(ErrorCode.FUNCTION_ALIAS_ALREADY_EXISTS_1, name);
            }
        } else {
            int id = getObjectId();
            UserAggregate aggregate = new UserAggregate(schema, id, name, javaClassMethod, force);
            db.addSchemaObject(session, aggregate);
        }
        return 0;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setJavaClassMethod(String string) {
        this.javaClassMethod = string;
    }

    public void setIfNotExists(boolean ifNotExists) {
        this.ifNotExists = ifNotExists;
    }

    public void setForce(boolean force) {
        this.force = force;
    }

    @Override
    public int getType() {
        return CommandInterface.CREATE_AGGREGATE;
    }

}
