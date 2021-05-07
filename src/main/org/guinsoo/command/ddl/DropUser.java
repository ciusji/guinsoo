/*
 * Copyright 2004-2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.command.ddl;

import org.guinsoo.api.ErrorCode;
import org.guinsoo.command.CommandInterface;
import org.guinsoo.engine.Database;
import org.guinsoo.engine.RightOwner;
import org.guinsoo.engine.SessionLocal;
import org.guinsoo.engine.User;
import org.guinsoo.message.DbException;

/**
 * This class represents the statement
 * DROP USER
 */
public class DropUser extends DefineCommand {

    private boolean ifExists;
    private String userName;

    public DropUser(SessionLocal session) {
        super(session);
    }

    public void setIfExists(boolean b) {
        ifExists = b;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public long update() {
        session.getUser().checkAdmin();
        Database db = session.getDatabase();
        User user = db.findUser(userName);
        if (user == null) {
            if (!ifExists) {
                throw DbException.get(ErrorCode.USER_NOT_FOUND_1, userName);
            }
        } else {
            if (user == session.getUser()) {
                int adminUserCount = 0;
                for (RightOwner rightOwner : db.getAllUsersAndRoles()) {
                    if (rightOwner instanceof User && ((User) rightOwner).isAdmin()) {
                        adminUserCount++;
                    }
                }
                if (adminUserCount == 1) {
                    throw DbException.get(ErrorCode.CANNOT_DROP_CURRENT_USER);
                }
            }
            user.checkOwnsNoSchemas();
            db.removeDatabaseObject(session, user);
        }
        return 0;
    }

    @Override
    public boolean isTransactional() {
        return false;
    }

    @Override
    public int getType() {
        return CommandInterface.DROP_USER;
    }

}
