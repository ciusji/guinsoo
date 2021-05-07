/*
 * Copyright 2004-2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.engine;

import java.util.ArrayList;

import org.guinsoo.message.DbException;
import org.guinsoo.message.Trace;
import org.guinsoo.schema.Schema;
import org.guinsoo.table.Table;

/**
 * Represents a role. Roles can be granted to users, and to other roles.
 */
public final class Role extends RightOwner {

    private final boolean system;

    public Role(Database database, int id, String roleName, boolean system) {
        super(database, id, roleName, Trace.USER);
        this.system = system;
    }

    @Override
    public String getCreateSQLForCopy(Table table, String quotedName) {
        throw DbException.getInternalError(toString());
    }

    /**
     * Get the CREATE SQL statement for this object.
     *
     * @param ifNotExists true if IF NOT EXISTS should be used
     * @return the SQL statement
     */
    public String getCreateSQL(boolean ifNotExists) {
        if (system) {
            return null;
        }
        StringBuilder builder = new StringBuilder("CREATE ROLE ");
        if (ifNotExists) {
            builder.append("IF NOT EXISTS ");
        }
        return getSQL(builder, DEFAULT_SQL_FLAGS).toString();
    }

    @Override
    public String getCreateSQL() {
        return getCreateSQL(false);
    }

    @Override
    public int getType() {
        return ROLE;
    }

    @Override
    public ArrayList<DbObject> getChildren() {
        ArrayList<DbObject> children = new ArrayList<>();
        for (Schema schema : database.getAllSchemas()) {
            if (schema.getOwner() == this) {
                children.add(schema);
            }
        }
        return children;
    }

    @Override
    public void removeChildrenAndResources(SessionLocal session) {
        for (RightOwner rightOwner : database.getAllUsersAndRoles()) {
            Right right = rightOwner.getRightForRole(this);
            if (right != null) {
                database.removeDatabaseObject(session, right);
            }
        }
        for (Right right : database.getAllRights()) {
            if (right.getGrantee() == this) {
                database.removeDatabaseObject(session, right);
            }
        }
        database.removeMeta(session, getId());
        invalidate();
    }

}
