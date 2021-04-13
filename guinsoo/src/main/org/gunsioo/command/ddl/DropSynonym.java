/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.command.ddl;

import org.gunsioo.api.ErrorCode;
import org.gunsioo.command.CommandInterface;
import org.gunsioo.engine.SessionLocal;
import org.gunsioo.message.DbException;
import org.gunsioo.schema.Schema;
import org.gunsioo.table.TableSynonym;

/**
 * This class represents the statement
 * DROP SYNONYM
 */
public class DropSynonym extends SchemaOwnerCommand {

    private String synonymName;
    private boolean ifExists;

    public DropSynonym(SessionLocal session, Schema schema) {
        super(session, schema);
    }

    public void setSynonymName(String name) {
        this.synonymName = name;
    }

    @Override
    long update(Schema schema) {
        TableSynonym synonym = schema.getSynonym(synonymName);
        if (synonym == null) {
            if (!ifExists) {
                throw DbException.get(ErrorCode.TABLE_OR_VIEW_NOT_FOUND_1, synonymName);
            }
        } else {
            session.getDatabase().removeSchemaObject(session, synonym);
        }
        return 0;
    }

    public void setIfExists(boolean ifExists) {
        this.ifExists = ifExists;
    }

    @Override
    public int getType() {
        return CommandInterface.DROP_SYNONYM;
    }

}
