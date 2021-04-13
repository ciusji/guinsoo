/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.command.ddl;

import org.gunsioo.api.ErrorCode;
import org.gunsioo.command.CommandInterface;
import org.gunsioo.constraint.Constraint;
import org.gunsioo.constraint.Constraint.Type;
import org.gunsioo.constraint.ConstraintDomain;
import org.gunsioo.engine.SessionLocal;
import org.gunsioo.message.DbException;
import org.gunsioo.schema.Domain;
import org.gunsioo.schema.Schema;

/**
 * This class represents the statement ALTER DOMAIN DROP CONSTRAINT
 */
public class AlterDomainDropConstraint extends SchemaOwnerCommand {

    private String constraintName;
    private String domainName;
    private boolean ifDomainExists;
    private final boolean ifConstraintExists;

    public AlterDomainDropConstraint(SessionLocal session, Schema schema, boolean ifConstraintExists) {
        super(session, schema);
        this.ifConstraintExists = ifConstraintExists;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public void setIfDomainExists(boolean b) {
        ifDomainExists = b;
    }

    public void setConstraintName(String string) {
        constraintName = string;
    }

    @Override
    long update(Schema schema) {
        Domain domain = schema.findDomain(domainName);
        if (domain == null) {
            if (ifDomainExists) {
                return 0;
            }
            throw DbException.get(ErrorCode.DOMAIN_NOT_FOUND_1, domainName);
        }
        Constraint constraint = schema.findConstraint(session, constraintName);
        if (constraint == null || constraint.getConstraintType() != Type.DOMAIN
                || ((ConstraintDomain) constraint).getDomain() != domain) {
            if (!ifConstraintExists) {
                throw DbException.get(ErrorCode.CONSTRAINT_NOT_FOUND_1, constraintName);
            }
        } else {
            session.getDatabase().removeSchemaObject(session, constraint);
        }
        return 0;
    }

    @Override
    public int getType() {
        return CommandInterface.ALTER_DOMAIN_DROP_CONSTRAINT;
    }

}
