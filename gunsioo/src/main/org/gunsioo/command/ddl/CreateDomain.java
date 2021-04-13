/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://h2database.com/html/license.html).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.command.ddl;

import java.util.ArrayList;
import org.gunsioo.api.ErrorCode;
import org.gunsioo.command.CommandInterface;
import org.gunsioo.engine.SessionLocal;
import org.gunsioo.expression.Expression;
import org.gunsioo.message.DbException;
import org.gunsioo.schema.Domain;
import org.gunsioo.schema.Schema;
import org.gunsioo.table.Table;
import org.gunsioo.util.HasSQL;
import org.gunsioo.util.Utils;
import org.gunsioo.value.DataType;
import org.gunsioo.value.TypeInfo;
import org.gunsioo.value.Value;

/**
 * This class represents the statement
 * CREATE DOMAIN
 */
public class CreateDomain extends SchemaOwnerCommand {

    private String typeName;
    private boolean ifNotExists;

    private TypeInfo dataType;

    private Domain parentDomain;

    private Expression defaultExpression;

    private Expression onUpdateExpression;

    private String comment;

    private ArrayList<AlterDomainAddConstraint> constraintCommands;

    public CreateDomain(SessionLocal session, Schema schema) {
        super(session, schema);
    }

    public void setTypeName(String name) {
        this.typeName = name;
    }

    public void setIfNotExists(boolean ifNotExists) {
        this.ifNotExists = ifNotExists;
    }

    public void setDataType(TypeInfo dataType) {
        this.dataType = dataType;
    }

    public void setParentDomain(Domain parentDomain) {
        this.parentDomain = parentDomain;
    }

    public void setDefaultExpression(Expression defaultExpression) {
        this.defaultExpression = defaultExpression;
    }

    public void setOnUpdateExpression(Expression onUpdateExpression) {
        this.onUpdateExpression = onUpdateExpression;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    long update(Schema schema) {
        if (schema.findDomain(typeName) != null) {
            if (ifNotExists) {
                return 0;
            }
            throw DbException.get(ErrorCode.DOMAIN_ALREADY_EXISTS_1, typeName);
        }
        if (typeName.indexOf(' ') < 0) {
            DataType builtIn = DataType.getTypeByName(typeName, session.getDatabase().getMode());
            if (builtIn != null) {
                if (session.getDatabase().equalsIdentifiers(typeName, Value.getTypeName(builtIn.type))) {
                    throw DbException.get(ErrorCode.DOMAIN_ALREADY_EXISTS_1, typeName);
                }
                Table table = session.getDatabase().getFirstUserTable();
                if (table != null) {
                    StringBuilder builder = new StringBuilder(typeName).append(" (");
                    table.getSQL(builder, HasSQL.TRACE_SQL_FLAGS).append(')');
                    throw DbException.get(ErrorCode.DOMAIN_ALREADY_EXISTS_1, builder.toString());
                }
            }
        }
        int id = getObjectId();
        Domain domain = new Domain(schema, id, typeName);
        domain.setDataType(dataType != null ? dataType : parentDomain.getDataType());
        domain.setDomain(parentDomain);
        domain.setDefaultExpression(session, defaultExpression);
        domain.setOnUpdateExpression(session, onUpdateExpression);
        domain.setComment(comment);
        schema.getDatabase().addSchemaObject(session, domain);
        if (constraintCommands != null) {
            for (AlterDomainAddConstraint command : constraintCommands) {
                command.update();
            }
        }
        return 0;
    }

    @Override
    public int getType() {
        return CommandInterface.CREATE_DOMAIN;
    }

    /**
     * Add a constraint command.
     *
     * @param command the command to add
     */
    public void addConstraintCommand(AlterDomainAddConstraint command) {
        if (constraintCommands == null) {
            constraintCommands = Utils.newSmallArrayList();
        }
        constraintCommands.add(command);
    }

}
