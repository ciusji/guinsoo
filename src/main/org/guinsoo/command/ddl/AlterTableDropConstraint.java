/*
 * Copyright 2004-2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.command.ddl;

import org.guinsoo.api.ErrorCode;
import org.guinsoo.command.CommandInterface;
import org.guinsoo.constraint.Constraint;
import org.guinsoo.constraint.Constraint.Type;
import org.guinsoo.constraint.ConstraintActionType;
import org.guinsoo.engine.Right;
import org.guinsoo.engine.SessionLocal;
import org.guinsoo.message.DbException;
import org.guinsoo.schema.Schema;

/**
 * This class represents the statement
 * ALTER TABLE DROP CONSTRAINT
 */
public class AlterTableDropConstraint extends SchemaCommand {

    private String constraintName;
    private final boolean ifExists;
    private ConstraintActionType dropAction;

    public AlterTableDropConstraint(SessionLocal session, Schema schema, boolean ifExists) {
        super(session, schema);
        this.ifExists = ifExists;
        dropAction = session.getDatabase().getSettings().dropRestrict ?
                ConstraintActionType.RESTRICT : ConstraintActionType.CASCADE;
    }

    public void setConstraintName(String string) {
        constraintName = string;
    }

    public void setDropAction(ConstraintActionType dropAction) {
        this.dropAction = dropAction;
    }

    @Override
    public long update() {
        Constraint constraint = getSchema().findConstraint(session, constraintName);
        Type constraintType;
        if (constraint == null || (constraintType = constraint.getConstraintType()) == Type.DOMAIN) {
            if (!ifExists) {
                throw DbException.get(ErrorCode.CONSTRAINT_NOT_FOUND_1, constraintName);
            }
        } else {
            session.getUser().checkTableRight(constraint.getTable(), Right.SCHEMA_OWNER);
            session.getUser().checkTableRight(constraint.getRefTable(), Right.SCHEMA_OWNER);
            if (constraintType == Type.PRIMARY_KEY || constraintType == Type.UNIQUE) {
                for (Constraint c : constraint.getTable().getConstraints()) {
                    if (c.getReferencedConstraint() == constraint) {
                        if (dropAction == ConstraintActionType.RESTRICT) {
                            throw DbException.get(ErrorCode.CONSTRAINT_IS_USED_BY_CONSTRAINT_2,
                                    constraint.getTraceSQL(), c.getTraceSQL());
                        }
                        session.getUser().checkTableRight(c.getTable(), Right.SCHEMA_OWNER);
                    }
                }
            }
            session.getDatabase().removeSchemaObject(session, constraint);
        }
        return 0;
    }

    @Override
    public int getType() {
        return CommandInterface.ALTER_TABLE_DROP_CONSTRAINT;
    }

}
