/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.command.ddl;

import org.guinsoo.api.ErrorCode;
import org.guinsoo.command.CommandInterface;
import org.guinsoo.engine.SessionLocal;
import org.guinsoo.message.DbException;
import org.guinsoo.schema.Schema;
import org.guinsoo.table.TableSynonym;

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
