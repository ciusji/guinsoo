/*
 * Copyright 2004-2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.command.ddl;

import java.util.ArrayList;
import org.guinsoo.api.ErrorCode;
import org.guinsoo.command.CommandInterface;
import org.guinsoo.engine.SessionLocal;
import org.guinsoo.expression.Expression;
import org.guinsoo.message.DbException;
import org.guinsoo.schema.Domain;
import org.guinsoo.schema.Schema;
import org.guinsoo.table.Table;
import org.guinsoo.util.HasSQL;
import org.guinsoo.util.Utils;
import org.guinsoo.value.DataType;
import org.guinsoo.value.TypeInfo;
import org.guinsoo.value.Value;

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
