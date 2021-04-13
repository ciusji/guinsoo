/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.command.ddl;

import org.gunsioo.api.ErrorCode;
import org.gunsioo.command.CommandInterface;
import org.gunsioo.engine.Database;
import org.gunsioo.engine.SessionLocal;
import org.gunsioo.message.DbException;
import org.gunsioo.schema.Domain;
import org.gunsioo.schema.Schema;

/**
 * This class represents the statement
 * ALTER DOMAIN RENAME
 */
public class AlterDomainRename extends SchemaOwnerCommand {

    private boolean ifDomainExists;
    private String oldDomainName;
    private String newDomainName;

    public AlterDomainRename(SessionLocal session, Schema schema) {
        super(session, schema);
    }

    public void setIfDomainExists(boolean b) {
        ifDomainExists = b;
    }

    public void setOldDomainName(String name) {
        oldDomainName = name;
    }

    public void setNewDomainName(String name) {
        newDomainName = name;
    }

    @Override
    long update(Schema schema) {
        Database db = session.getDatabase();
        Domain oldDomain = schema.findDomain(oldDomainName);
        if (oldDomain == null) {
            if (ifDomainExists) {
                return 0;
            }
            throw DbException.get(ErrorCode.DOMAIN_NOT_FOUND_1, oldDomainName);
        }
        Domain d = schema.findDomain(newDomainName);
        if (d != null) {
            if (oldDomain != d) {
                throw DbException.get(ErrorCode.DOMAIN_ALREADY_EXISTS_1, newDomainName);
            }
            if (newDomainName.equals(oldDomain.getName())) {
                return 0;
            }
        }
        db.renameSchemaObject(session, oldDomain, newDomainName);
        AlterDomain.forAllDependencies(session, oldDomain, null, null, false);
        return 0;
    }

    @Override
    public int getType() {
        return CommandInterface.ALTER_DOMAIN_RENAME;
    }

}
