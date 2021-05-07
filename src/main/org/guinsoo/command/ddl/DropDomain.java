/*
 * Copyright 2004-2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.command.ddl;

import java.util.ArrayList;

import org.guinsoo.api.ErrorCode;
import org.guinsoo.command.CommandInterface;
import org.guinsoo.constraint.ConstraintActionType;
import org.guinsoo.constraint.ConstraintDomain;
import org.guinsoo.engine.SessionLocal;
import org.guinsoo.expression.Expression;
import org.guinsoo.message.DbException;
import org.guinsoo.schema.Domain;
import org.guinsoo.schema.Schema;
import org.guinsoo.table.Column;
import org.guinsoo.table.ColumnTemplate;
import org.guinsoo.table.Table;

/**
 * This class represents the statement DROP DOMAIN
 */
public class DropDomain extends SchemaOwnerCommand {

    private String typeName;
    private boolean ifExists;
    private ConstraintActionType dropAction;

    public DropDomain(SessionLocal session, Schema schema) {
        super(session, schema);
        dropAction = session.getDatabase().getSettings().dropRestrict ? ConstraintActionType.RESTRICT
                : ConstraintActionType.CASCADE;
    }

    public void setIfExists(boolean ifExists) {
        this.ifExists = ifExists;
    }

    public void setDropAction(ConstraintActionType dropAction) {
        this.dropAction = dropAction;
    }

    @Override
    long update(Schema schema) {
        Domain domain = schema.findDomain(typeName);
        if (domain == null) {
            if (!ifExists) {
                throw DbException.get(ErrorCode.DOMAIN_NOT_FOUND_1, typeName);
            }
        } else {
            AlterDomain.forAllDependencies(session, domain, this::copyColumn, this::copyDomain, true);
            session.getDatabase().removeSchemaObject(session, domain);
        }
        return 0;
    }

    private boolean copyColumn(Domain domain, Column targetColumn) {
        Table targetTable = targetColumn.getTable();
        if (dropAction == ConstraintActionType.RESTRICT) {
            throw DbException.get(ErrorCode.CANNOT_DROP_2, typeName, targetTable.getCreateSQL());
        }
        String columnName = targetColumn.getName();
        ArrayList<ConstraintDomain> constraints = domain.getConstraints();
        if (constraints != null && !constraints.isEmpty()) {
            for (ConstraintDomain constraint : constraints) {
                Expression checkCondition = constraint.getCheckConstraint(session, columnName);
                AlterTableAddConstraint check = new AlterTableAddConstraint(session, targetTable.getSchema(),
                        CommandInterface.ALTER_TABLE_ADD_CONSTRAINT_CHECK, false);
                check.setTableName(targetTable.getName());
                check.setCheckExpression(checkCondition);
                check.update();
            }
        }
        copyExpressions(session, domain, targetColumn);
        return true;
    }

    private boolean copyDomain(Domain domain, Domain targetDomain) {
        if (dropAction == ConstraintActionType.RESTRICT) {
            throw DbException.get(ErrorCode.CANNOT_DROP_2, typeName, targetDomain.getTraceSQL());
        }
        ArrayList<ConstraintDomain> constraints = domain.getConstraints();
        if (constraints != null && !constraints.isEmpty()) {
            for (ConstraintDomain constraint : constraints) {
                Expression checkCondition = constraint.getCheckConstraint(session, null);
                AlterDomainAddConstraint check = new AlterDomainAddConstraint(session, targetDomain.getSchema(), //
                        false);
                check.setDomainName(targetDomain.getName());
                check.setCheckExpression(checkCondition);
                check.update();
            }
        }
        copyExpressions(session, domain, targetDomain);
        return true;
    }

    private static boolean copyExpressions(SessionLocal session, Domain domain, ColumnTemplate targetColumn) {
        targetColumn.setDomain(domain.getDomain());
        Expression e = domain.getDefaultExpression();
        boolean modified = false;
        if (e != null && targetColumn.getDefaultExpression() == null) {
            targetColumn.setDefaultExpression(session, e);
            modified = true;
        }
        e = domain.getOnUpdateExpression();
        if (e != null && targetColumn.getOnUpdateExpression() == null) {
            targetColumn.setOnUpdateExpression(session, e);
            modified = true;
        }
        return modified;
    }

    public void setTypeName(String name) {
        this.typeName = name;
    }

    @Override
    public int getType() {
        return CommandInterface.DROP_DOMAIN;
    }

}
