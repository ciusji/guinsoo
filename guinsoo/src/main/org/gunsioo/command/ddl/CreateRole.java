/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.command.ddl;

import org.gunsioo.api.ErrorCode;
import org.gunsioo.command.CommandInterface;
import org.gunsioo.engine.Database;
import org.gunsioo.engine.RightOwner;
import org.gunsioo.engine.Role;
import org.gunsioo.engine.SessionLocal;
import org.gunsioo.message.DbException;

/**
 * This class represents the statement
 * CREATE ROLE
 */
public class CreateRole extends DefineCommand {

    private String roleName;
    private boolean ifNotExists;

    public CreateRole(SessionLocal session) {
        super(session);
    }

    public void setIfNotExists(boolean ifNotExists) {
        this.ifNotExists = ifNotExists;
    }

    public void setRoleName(String name) {
        this.roleName = name;
    }

    @Override
    public long update() {
        session.getUser().checkAdmin();
        Database db = session.getDatabase();
        RightOwner rightOwner = db.findUserOrRole(roleName);
        if (rightOwner != null) {
            if (rightOwner instanceof Role) {
                if (ifNotExists) {
                    return 0;
                }
                throw DbException.get(ErrorCode.ROLE_ALREADY_EXISTS_1, roleName);
            }
            throw DbException.get(ErrorCode.USER_ALREADY_EXISTS_1, roleName);
        }
        int id = getObjectId();
        Role role = new Role(db, id, roleName, false);
        db.addDatabaseObject(session, role);
        return 0;
    }

    @Override
    public int getType() {
        return CommandInterface.CREATE_ROLE;
    }

}
