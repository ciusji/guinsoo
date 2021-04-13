/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://h2database.com/html/license.html).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.command.ddl;

import org.gunsioo.api.ErrorCode;
import org.gunsioo.command.CommandInterface;
import org.gunsioo.engine.Database;
import org.gunsioo.engine.Right;
import org.gunsioo.engine.SessionLocal;
import org.gunsioo.message.DbException;
import org.gunsioo.schema.Schema;
import org.gunsioo.table.Table;

/**
 * This class represents the statement
 * ALTER TABLE RENAME
 */
public class AlterTableRename extends SchemaCommand {

    private boolean ifTableExists;
    private String oldTableName;
    private String newTableName;
    private boolean hidden;

    public AlterTableRename(SessionLocal session, Schema schema) {
        super(session, schema);
    }

    public void setIfTableExists(boolean b) {
        ifTableExists = b;
    }

    public void setOldTableName(String name) {
        oldTableName = name;
    }

    public void setNewTableName(String name) {
        newTableName = name;
    }

    @Override
    public long update() {
        Database db = session.getDatabase();
        Table oldTable = getSchema().findTableOrView(session, oldTableName);
        if (oldTable == null) {
            if (ifTableExists) {
                return 0;
            }
            throw DbException.get(ErrorCode.TABLE_OR_VIEW_NOT_FOUND_1, oldTableName);
        }
        if (oldTable.isView()) {
            session.getUser().checkSchemaOwner(oldTable.getSchema());
        } else {
            session.getUser().checkTableRight(oldTable, Right.SCHEMA_OWNER);
        }
        Table t = getSchema().findTableOrView(session, newTableName);
        if (t != null && hidden && newTableName.equals(oldTable.getName())) {
            if (!t.isHidden()) {
                t.setHidden(hidden);
                oldTable.setHidden(true);
                db.updateMeta(session, oldTable);
            }
            return 0;
        }
        if (t != null || newTableName.equals(oldTable.getName())) {
            throw DbException.get(ErrorCode.TABLE_OR_VIEW_ALREADY_EXISTS_1, newTableName);
        }
        if (oldTable.isTemporary()) {
            throw DbException.getUnsupportedException("temp table");
        }
        db.renameSchemaObject(session, oldTable, newTableName);
        return 0;
    }

    @Override
    public int getType() {
        return CommandInterface.ALTER_TABLE_RENAME;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

}
