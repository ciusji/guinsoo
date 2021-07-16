/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.command.ddl;

import org.guinsoo.api.ErrorCode;
import org.guinsoo.command.CommandInterface;
import org.guinsoo.constraint.ConstraintDomain;
import org.guinsoo.engine.Database;
import org.guinsoo.engine.SessionLocal;
import org.guinsoo.expression.Expression;
import org.guinsoo.message.DbException;
import org.guinsoo.schema.Domain;
import org.guinsoo.schema.Schema;

/**
 * This class represents the statement ALTER DOMAIN ADD CONSTRAINT
 */
public class AlterDomainAddConstraint extends SchemaOwnerCommand {

    private String constraintName;
    private String domainName;
    private Expression checkExpression;
    private String comment;
    private boolean checkExisting;
    private boolean ifDomainExists;
    private final boolean ifNotExists;

    public AlterDomainAddConstraint(SessionLocal session, Schema schema, boolean ifNotExists) {
        super(session, schema);
        this.ifNotExists = ifNotExists;
    }

    public void setIfDomainExists(boolean b) {
        ifDomainExists = b;
    }

    private String generateConstraintName(Domain domain) {
        if (constraintName == null) {
            constraintName = getSchema().getUniqueDomainConstraintName(session, domain);
        }
        return constraintName;
    }

    @Override
    long update(Schema schema) {
        try {
            return tryUpdate(schema);
        } finally {
            getSchema().freeUniqueName(constraintName);
        }
    }

    /**
     * Try to execute the statement.
     *
     * @param schema the schema
     * @return the update count
     */
    private int tryUpdate(Schema schema) {
        Domain domain = schema.findDomain(domainName);
        if (domain == null) {
            if (ifDomainExists) {
                return 0;
            }
            throw DbException.get(ErrorCode.DOMAIN_NOT_FOUND_1, domainName);
        }
        if (constraintName != null && schema.findConstraint(session, constraintName) != null) {
            if (ifNotExists) {
                return 0;
            }
            throw DbException.get(ErrorCode.CONSTRAINT_ALREADY_EXISTS_1, constraintName);
        }
        Database db = session.getDatabase();
        db.lockMeta(session);

        int id = getObjectId();
        String name = generateConstraintName(domain);
        ConstraintDomain constraint = new ConstraintDomain(schema, id, name, domain);
        constraint.setExpression(session, checkExpression);
        if (checkExisting) {
            constraint.checkExistingData(session);
        }
        constraint.setComment(comment);
        db.addSchemaObject(session, constraint);
        domain.addConstraint(constraint);
        return 0;
    }

    public void setConstraintName(String constraintName) {
        this.constraintName = constraintName;
    }

    public String getConstraintName() {
        return constraintName;
    }

    @Override
    public int getType() {
        return CommandInterface.ALTER_DOMAIN_ADD_CONSTRAINT;
    }

    public void setCheckExpression(Expression expression) {
        this.checkExpression = expression;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setCheckExisting(boolean b) {
        this.checkExisting = b;
    }

}
