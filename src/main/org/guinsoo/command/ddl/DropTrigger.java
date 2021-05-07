/*
 * Copyright 2004-2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.command.ddl;

import org.guinsoo.api.ErrorCode;
import org.guinsoo.command.CommandInterface;
import org.guinsoo.engine.Database;
import org.guinsoo.engine.Right;
import org.guinsoo.engine.SessionLocal;
import org.guinsoo.message.DbException;
import org.guinsoo.schema.Schema;
import org.guinsoo.schema.TriggerObject;
import org.guinsoo.table.Table;

/**
 * This class represents the statement
 * DROP TRIGGER
 */
public class DropTrigger extends SchemaCommand {

    private String triggerName;
    private boolean ifExists;

    public DropTrigger(SessionLocal session, Schema schema) {
        super(session, schema);
    }

    public void setIfExists(boolean b) {
        ifExists = b;
    }

    public void setTriggerName(String triggerName) {
        this.triggerName = triggerName;
    }

    @Override
    public long update() {
        Database db = session.getDatabase();
        TriggerObject trigger = getSchema().findTrigger(triggerName);
        if (trigger == null) {
            if (!ifExists) {
                throw DbException.get(ErrorCode.TRIGGER_NOT_FOUND_1, triggerName);
            }
        } else {
            Table table = trigger.getTable();
            session.getUser().checkTableRight(table, Right.SCHEMA_OWNER);
            db.removeSchemaObject(session, trigger);
        }
        return 0;
    }

    @Override
    public int getType() {
        return CommandInterface.DROP_TRIGGER;
    }

}
