/*
 * Copyright 2004-2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.command.ddl;

import org.guinsoo.api.ErrorCode;
import org.guinsoo.command.CommandInterface;
import org.guinsoo.constraint.ConstraintReferential;
import org.guinsoo.engine.Database;
import org.guinsoo.engine.DbObject;
import org.guinsoo.engine.Right;
import org.guinsoo.engine.SessionLocal;
import org.guinsoo.message.DbException;
import org.guinsoo.schema.Schema;
import org.guinsoo.table.Column;
import org.guinsoo.table.Table;

/**
 * This class represents the statement
 * ALTER TABLE ALTER COLUMN RENAME
 */
public class AlterTableRenameColumn extends SchemaCommand {

    private boolean ifTableExists;
    private boolean ifExists;
    private String tableName;
    private String oldName;
    private String newName;

    public AlterTableRenameColumn(SessionLocal session, Schema schema) {
        super(session, schema);
    }

    public void setIfTableExists(boolean b) {
        this.ifTableExists = b;
    }

    public void setIfExists(boolean b) {
        this.ifExists = b;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public void setOldColumnName(String oldName) {
        this.oldName = oldName;
    }

    public void setNewColumnName(String newName) {
        this.newName = newName;
    }

    @Override
    public long update() {
        Database db = session.getDatabase();
        Table table = getSchema().findTableOrView(session, tableName);
        if (table == null) {
            if (ifTableExists) {
                return 0;
            }
            throw DbException.get(ErrorCode.TABLE_OR_VIEW_NOT_FOUND_1, tableName);
        }
        Column column = table.getColumn(oldName, ifExists);
        if (column == null) {
            return 0;
        }
        session.getUser().checkTableRight(table, Right.SCHEMA_OWNER);
        table.checkSupportAlter();
        table.renameColumn(column, newName);
        table.setModified();
        db.updateMeta(session, table);

        // if we have foreign key constraints pointing at this table, we need to update them
        for (DbObject childDbObject : table.getChildren()) {
            if (childDbObject instanceof ConstraintReferential) {
                ConstraintReferential ref = (ConstraintReferential) childDbObject;
                ref.updateOnTableColumnRename();
            }
        }

        for (DbObject child : table.getChildren()) {
            if (child.getCreateSQL() != null) {
                db.updateMeta(session, child);
            }
        }
        return 0;
    }

    @Override
    public int getType() {
        return CommandInterface.ALTER_TABLE_ALTER_COLUMN_RENAME;
    }

}
