/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.engine;

import java.util.ArrayList;
import java.util.Arrays;

import org.guinsoo.message.DbException;
import org.guinsoo.message.Trace;
import org.guinsoo.schema.Schema;
import org.guinsoo.security.SHA256;
import org.guinsoo.api.ErrorCode;
import org.guinsoo.table.DualTable;
import org.guinsoo.table.MetaTable;
import org.guinsoo.table.RangeTable;
import org.guinsoo.table.Table;
import org.guinsoo.table.TableType;
import org.guinsoo.table.TableView;
import org.guinsoo.util.MathUtils;
import org.guinsoo.util.StringUtils;
import org.guinsoo.util.Utils;

/**
 * Represents a user object.
 */
public final class User extends RightOwner {

    private final boolean systemUser;
    private byte[] salt;
    private byte[] passwordHash;
    private boolean admin;

    public User(Database database, int id, String userName, boolean systemUser) {
        super(database, id, userName, Trace.USER);
        this.systemUser = systemUser;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public boolean isAdmin() {
        return admin;
    }

    /**
     * Set the salt and hash of the password for this user.
     *
     * @param salt the salt
     * @param hash the password hash
     */
    public void setSaltAndHash(byte[] salt, byte[] hash) {
        this.salt = salt;
        this.passwordHash = hash;
    }

    /**
     * Set the user name password hash. A random salt is generated as well.
     * The parameter is filled with zeros after use.
     *
     * @param userPasswordHash the user name password hash
     */
    public void setUserPasswordHash(byte[] userPasswordHash) {
        if (userPasswordHash != null) {
            if (userPasswordHash.length == 0) {
                salt = passwordHash = userPasswordHash;
            } else {
                salt = new byte[Constants.SALT_LEN];
                MathUtils.randomBytes(salt);
                passwordHash = SHA256.getHashWithSalt(userPasswordHash, salt);
            }
        }
    }

    @Override
    public String getCreateSQLForCopy(Table table, String quotedName) {
        throw DbException.getInternalError(toString());
    }

    @Override
    public String getCreateSQL() {
        return getCreateSQL(true);
    }

    /**
     * Get the CREATE SQL statement for this object.
     *
     * @param password true if the password (actually the salt and hash) should
     *            be returned
     * @return the SQL statement
     */
    public String getCreateSQL(boolean password) {
        StringBuilder buff = new StringBuilder("CREATE USER IF NOT EXISTS ");
        getSQL(buff, DEFAULT_SQL_FLAGS);
        if (comment != null) {
            buff.append(" COMMENT ");
            StringUtils.quoteStringSQL(buff, comment);
        }
        if (password) {
            buff.append(" SALT '");
            StringUtils.convertBytesToHex(buff, salt).
                append("' HASH '");
            StringUtils.convertBytesToHex(buff, passwordHash).
                append('\'');
        } else {
            buff.append(" PASSWORD ''");
        }
        if (admin) {
            buff.append(" ADMIN");
        }
        return buff.toString();
    }

    /**
     * Check the password of this user.
     *
     * @param userPasswordHash the password data (the user password hash)
     * @return true if the user password hash is correct
     */
    boolean validateUserPasswordHash(byte[] userPasswordHash) {
        if (userPasswordHash.length == 0 && passwordHash.length == 0) {
            return true;
        }
        if (userPasswordHash.length == 0) {
            userPasswordHash = SHA256.getKeyPasswordHash(getName(), new char[0]);
        }
        byte[] hash = SHA256.getHashWithSalt(userPasswordHash, salt);
        return Utils.compareSecure(hash, passwordHash);
    }

    /**
     * Checks if this user has admin rights. An exception is thrown if user
     * doesn't have them.
     *
     * @throws DbException if this user is not an admin
     */
    public void checkAdmin() {
        if (!admin) {
            throw DbException.get(ErrorCode.ADMIN_RIGHTS_REQUIRED);
        }
    }

    /**
     * Checks if this user has schema admin rights for every schema. An
     * exception is thrown if user doesn't have them.
     *
     * @throws DbException if this user is not a schema admin
     */
    public void checkSchemaAdmin() {
        if (!hasSchemaRight(null)) {
            throw DbException.get(ErrorCode.ADMIN_RIGHTS_REQUIRED);
        }
    }

    /**
     * Checks if this user has schema owner rights for the specified schema. An
     * exception is thrown if user doesn't have them.
     *
     * @param schema the schema
     * @throws DbException if this user is not a schema owner
     */
    public void checkSchemaOwner(Schema schema) {
        if (!hasSchemaRight(schema)) {
            throw DbException.get(ErrorCode.NOT_ENOUGH_RIGHTS_FOR_1, schema.getTraceSQL());
        }
    }

    /**
     * See if this user has owner rights for the specified schema
     *
     * @param schema the schema
     * @return true if the user has the rights
     */
    private boolean hasSchemaRight(Schema schema) {
        if (admin) {
            return true;
        }
        Role publicRole = database.getPublicRole();
        if (publicRole.isSchemaRightGrantedRecursive(schema)) {
            return true;
        }
        return isSchemaRightGrantedRecursive(schema);
    }

    /**
     * Checks that this user has the given rights for the specified table.
     *
     * @param table the table
     * @param rightMask the rights required
     * @throws DbException if this user does not have the required rights
     */
    public void checkTableRight(Table table, int rightMask) {
        if (!hasTableRight(table, rightMask)) {
            throw DbException.get(ErrorCode.NOT_ENOUGH_RIGHTS_FOR_1, table.getTraceSQL());
        }
    }

    /**
     * See if this user has the given rights for this database object.
     *
     * @param table the database object, or null for schema-only check
     * @param rightMask the rights required
     * @return true if the user has the rights
     */
    public boolean hasTableRight(Table table, int rightMask) {
        if (rightMask != Right.SELECT && !systemUser) {
            table.checkWritingAllowed();
        }
        if (admin) {
            return true;
        }
        Role publicRole = database.getPublicRole();
        if (publicRole.isTableRightGrantedRecursive(table, rightMask)) {
            return true;
        }
        if (table instanceof MetaTable || table instanceof DualTable || table instanceof RangeTable) {
            // everybody has access to the metadata information
            return true;
        }
        TableType tableType = table.getTableType();
        if (TableType.VIEW == tableType) {
            TableView v = (TableView) table;
            if (v.getOwner() == this) {
                // the owner of a view has access:
                // SELECT * FROM (SELECT * FROM ...)
                return true;
            }
        } else if (tableType == null) {
            // function table
            return true;
        }
        if (table.isTemporary() && !table.isGlobalTemporary()) {
            // the owner has all rights on local temporary tables
            return true;
        }
        return isTableRightGrantedRecursive(table, rightMask);
    }

    @Override
    public int getType() {
        return DbObject.USER;
    }

    @Override
    public ArrayList<DbObject> getChildren() {
        ArrayList<DbObject> children = new ArrayList<>();
        for (Right right : database.getAllRights()) {
            if (right.getGrantee() == this) {
                children.add(right);
            }
        }
        for (Schema schema : database.getAllSchemas()) {
            if (schema.getOwner() == this) {
                children.add(schema);
            }
        }
        return children;
    }

    @Override
    public void removeChildrenAndResources(SessionLocal session) {
        for (Right right : database.getAllRights()) {
            if (right.getGrantee() == this) {
                database.removeDatabaseObject(session, right);
            }
        }
        database.removeMeta(session, getId());
        salt = null;
        Arrays.fill(passwordHash, (byte) 0);
        passwordHash = null;
        invalidate();
    }

}
