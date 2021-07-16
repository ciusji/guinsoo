/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.command.ddl;

import org.guinsoo.api.ErrorCode;
import org.guinsoo.command.CommandInterface;
import org.guinsoo.constraint.Constraint;
import org.guinsoo.engine.Right;
import org.guinsoo.engine.SessionLocal;
import org.guinsoo.message.DbException;
import org.guinsoo.schema.Schema;

/**
 * This class represents the statement
 * ALTER TABLE RENAME CONSTRAINT
 */
public class AlterTableRenameConstraint extends SchemaCommand {

    private String constraintName;
    private String newConstraintName;

    public AlterTableRenameConstraint(SessionLocal session, Schema schema) {
        super(session, schema);
    }

    public void setConstraintName(String string) {
        constraintName = string;
    }
    public void setNewConstraintName(String newName) {
        this.newConstraintName = newName;
    }

    @Override
    public long update() {
        Constraint constraint = getSchema().findConstraint(session, constraintName);
        if (constraint == null) {
            throw DbException.get(ErrorCode.CONSTRAINT_NOT_FOUND_1, constraintName);
        }
        if (getSchema().findConstraint(session, newConstraintName) != null ||
                newConstraintName.equals(constraintName)) {
            throw DbException.get(ErrorCode.CONSTRAINT_ALREADY_EXISTS_1,
                    newConstraintName);
        }
        session.getUser().checkTableRight(constraint.getTable(), Right.SCHEMA_OWNER);
        session.getUser().checkTableRight(constraint.getRefTable(), Right.SCHEMA_OWNER);
        session.getDatabase().renameSchemaObject(session, constraint, newConstraintName);
        return 0;
    }

    @Override
    public int getType() {
        return CommandInterface.ALTER_TABLE_RENAME_CONSTRAINT;
    }

}
