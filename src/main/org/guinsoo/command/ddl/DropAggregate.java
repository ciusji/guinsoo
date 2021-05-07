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
import org.guinsoo.schema.Schema;
import org.guinsoo.schema.UserAggregate;

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
