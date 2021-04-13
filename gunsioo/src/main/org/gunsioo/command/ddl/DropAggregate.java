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
import org.gunsioo.schema.UserAggregate;

/**
 * This class represents the statement
 * DROP AGGREGATE
 */
public class DropAggregate extends SchemaOwnerCommand {

    private String name;
    private boolean ifExists;

    public DropAggregate(SessionLocal session, Schema schema) {
        super(session, schema);
    }

    @Override
    long update(Schema schema) {
        Database db = session.getDatabase();
        UserAggregate aggregate = schema.findAggregate(name);
        if (aggregate == null) {
            if (!ifExists) {
                throw DbException.get(ErrorCode.AGGREGATE_NOT_FOUND_1, name);
            }
        } else {
            db.removeSchemaObject(session, aggregate);
        }
        return 0;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIfExists(boolean ifExists) {
        this.ifExists = ifExists;
    }

    @Override
    public int getType() {
        return CommandInterface.DROP_AGGREGATE;
    }

}
