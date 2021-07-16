/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.command.ddl;

import java.util.function.BiPredicate;

import org.guinsoo.api.ErrorCode;
import org.guinsoo.command.CommandInterface;
import org.guinsoo.engine.Database;
import org.guinsoo.engine.SessionLocal;
import org.guinsoo.expression.Expression;
import org.guinsoo.message.DbException;
import org.guinsoo.schema.Domain;
import org.guinsoo.schema.Schema;
import org.guinsoo.table.Column;
import org.guinsoo.table.ColumnTemplate;
import org.guinsoo.table.Table;

/**
 * This class represents the statements
 * ALTER DOMAIN SET DEFAULT
 * ALTER DOMAIN DROP DEFAULT
 * ALTER DOMAIN SET ON UPDATE
 * ALTER DOMAIN DROP ON UPDATE
 */
public class AlterDomain extends SchemaOwnerCommand {

    /**
     * Processes all columns and domains that use the specified domain.
     *
     * @param session
     *            the session
     * @param domain
     *            the domain to process
     * @param columnProcessor
     *            column handler
     * @param domainProcessor
     *            domain handler
     * @param recompileExpressions
     *            whether processed expressions need to be recompiled
     */
    public static void forAllDependencies(SessionLocal session, Domain domain,
            BiPredicate<Domain, Column> columnProcessor, BiPredicate<Domain, Domain> domainProcessor,
            boolean recompileExpressions) {
        Database db = session.getDatabase();
        for (Schema schema : db.getAllSchemasNoMeta()) {
            for (Domain targetDomain : schema.getAllDomains()) {
                if (targetDomain.getDomain() == domain) {
                    if (domainProcessor == null || domainProcessor.test(domain, targetDomain)) {
                        if (recompileExpressions) {
                            domain.prepareExpressions(session);
                        }
                        db.updateMeta(session, targetDomain);
                    }
                }
            }
            for (Table t : schema.getAllTablesAndViews(null)) {
                if (forTable(session, domain, columnProcessor, recompileExpressions, t)) {
                    db.updateMeta(session, t);
                }
            }
        }
        for (Table t : session.getLocalTempTables()) {
            forTable(session, domain, columnProcessor, recompileExpressions, t);
        }
    }

    private static boolean forTable(SessionLocal session, Domain domain, BiPredicate<Domain, Column> columnProcessor,
            boolean recompileExpressions, Table t) {
        boolean modified = false;
        for (Column targetColumn : t.getColumns()) {
            if (targetColumn.getDomain() == domain) {
                boolean m = columnProcessor == null || columnProcessor.test(domain, targetColumn);
                if (m) {
                    if (recompileExpressions) {
                        targetColumn.prepareExpressions(session);
                    }
                    modified = true;
                }
            }
        }
        return modified;
    }

    private final int type;

    private Expression expression;

    private String domainName;
    private boolean ifDomainExists;

    public AlterDomain(SessionLocal session, Schema schema, int type) {
        super(session, schema);
        this.type = type;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public void setIfDomainExists(boolean b) {
        ifDomainExists = b;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    @Override
    long update(Schema schema) {
        Domain domain = getSchema().findDomain(domainName);
        if (domain == null) {
            if (ifDomainExists) {
                return 0;
            }
            throw DbException.get(ErrorCode.DOMAIN_NOT_FOUND_1, domainName);
        }
        switch (type) {
        case CommandInterface.ALTER_DOMAIN_DEFAULT:
            domain.setDefaultExpression(session, expression);
            break;
        case CommandInterface.ALTER_DOMAIN_ON_UPDATE:
            domain.setOnUpdateExpression(session, expression);
            break;
        default:
            throw DbException.getInternalError("type=" + type);
        }
        if (expression != null) {
            AlterDomain.forAllDependencies(session, domain, this::copyColumn, this::copyDomain, true);
        }
        session.getDatabase().updateMeta(session, domain);
        return 0;
    }

    private boolean copyColumn(Domain domain, Column targetColumn) {
        return copyExpressions(session, domain, targetColumn);
    }

    private boolean copyDomain(Domain domain, Domain targetDomain) {
        return copyExpressions(session, domain, targetDomain);
    }

    private boolean copyExpressions(SessionLocal session, Domain domain, ColumnTemplate targetColumn) {
        switch (type) {
        case CommandInterface.ALTER_DOMAIN_DEFAULT: {
            Expression e = domain.getDefaultExpression();
            if (e != null && targetColumn.getDefaultExpression() == null) {
                targetColumn.setDefaultExpression(session, e);
                return true;
            }
            break;
        }
        case CommandInterface.ALTER_DOMAIN_ON_UPDATE: {
            Expression e = domain.getOnUpdateExpression();
            if (e != null && targetColumn.getOnUpdateExpression() == null) {
                targetColumn.setOnUpdateExpression(session, e);
                return true;
            }
        }
        }
        return false;
    }

    @Override
    public int getType() {
        return CommandInterface.ALTER_DOMAIN_DROP_CONSTRAINT;
    }

}
