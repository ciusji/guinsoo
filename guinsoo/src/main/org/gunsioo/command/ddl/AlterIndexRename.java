/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.command.ddl;

import org.gunsioo.api.ErrorCode;
import org.gunsioo.command.CommandInterface;
import org.gunsioo.engine.Database;
import org.gunsioo.engine.Right;
import org.gunsioo.engine.SessionLocal;
import org.gunsioo.index.Index;
import org.gunsioo.message.DbException;
import org.gunsioo.schema.Schema;

/**
 * This class represents the statement
 * ALTER INDEX RENAME
 */
public class AlterIndexRename extends DefineCommand {

    private boolean ifExists;
    private Schema oldSchema;
    private String oldIndexName;
    private String newIndexName;

    public AlterIndexRename(SessionLocal session) {
        super(session);
    }

    public void setIfExists(boolean b) {
        ifExists = b;
    }

    public void setOldSchema(Schema old) {
        oldSchema = old;
    }

    public void setOldName(String name) {
        oldIndexName = name;
    }

    public void setNewName(String name) {
        newIndexName = name;
    }

    @Override
    public long update() {
        Database db = session.getDatabase();
        Index oldIndex = oldSchema.findIndex(session, oldIndexName);
        if (oldIndex == null) {
            if (!ifExists) {
                throw DbException.get(ErrorCode.INDEX_NOT_FOUND_1,
                        newIndexName);
            }
            return 0;
        }
        if (oldSchema.findIndex(session, newIndexName) != null ||
                newIndexName.equals(oldIndexName)) {
            throw DbException.get(ErrorCode.INDEX_ALREADY_EXISTS_1,
                    newIndexName);
        }
        session.getUser().checkTableRight(oldIndex.getTable(), Right.SCHEMA_OWNER);
        db.renameSchemaObject(session, oldIndex, newIndexName);
        return 0;
    }

    @Override
    public int getType() {
        return CommandInterface.ALTER_INDEX_RENAME;
    }

}
