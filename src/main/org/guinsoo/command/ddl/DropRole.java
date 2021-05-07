/*
 * Copyright 2004-2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.command.ddl;

import org.guinsoo.api.ErrorCode;
import org.guinsoo.command.CommandInterface;
import org.guinsoo.engine.Database;
import org.guinsoo.engine.Role;
import org.guinsoo.engine.SessionLocal;
import org.guinsoo.message.DbException;

/**
 * This class represents the statement
 * DROP ROLE
 */
public class DropRole extends DefineCommand {

    private String roleName;
    private boolean ifExists;

    public DropRole(SessionLocal session) {
        super(session);
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    @Override
    public long update() {
        session.getUser().checkAdmin();
        Database db = session.getDatabase();
        Role role = db.findRole(roleName);
        if (role == null) {
            if (!ifExists) {
                throw DbException.get(ErrorCode.ROLE_NOT_FOUND_1, roleName);
            }
        } else {
            if (role == db.getPublicRole()) {
                throw DbException.get(ErrorCode.ROLE_CAN_NOT_BE_DROPPED_1, roleName);
            }
            role.checkOwnsNoSchemas();
            db.removeDatabaseObject(session, role);
        }
        return 0;
    }

    public void setIfExists(boolean ifExists) {
        this.ifExists = ifExists;
    }

    @Override
    public int getType() {
        return CommandInterface.DROP_ROLE;
    }

}
