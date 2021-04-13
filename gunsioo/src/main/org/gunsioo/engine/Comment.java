/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://h2database.com/html/license.html).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.engine;

import org.gunsioo.message.DbException;
import org.gunsioo.message.Trace;
import org.gunsioo.table.Table;
import org.gunsioo.util.StringUtils;

/**
 * Represents a database object comment.
 */
public final class Comment extends DbObject {

    private final int objectType;
    private final String quotedObjectName;
    private String commentText;

    public Comment(Database database, int id, DbObject obj) {
        super(database, id,  getKey(obj), Trace.DATABASE);
        this.objectType = obj.getType();
        this.quotedObjectName = obj.getSQL(DEFAULT_SQL_FLAGS);
    }

    @Override
    public String getCreateSQLForCopy(Table table, String quotedName) {
        throw DbException.getInternalError(toString());
    }

    private static String getTypeName(int type) {
        switch (type) {
        case DbObject.CONSTANT:
            return "CONSTANT";
        case DbObject.CONSTRAINT:
            return "CONSTRAINT";
        case DbObject.FUNCTION_ALIAS:
            return "ALIAS";
        case DbObject.INDEX:
            return "INDEX";
        case DbObject.ROLE:
            return "ROLE";
        case DbObject.SCHEMA:
            return "SCHEMA";
        case DbObject.SEQUENCE:
            return "SEQUENCE";
        case DbObject.TABLE_OR_VIEW:
            return "TABLE";
        case DbObject.TRIGGER:
            return "TRIGGER";
        case DbObject.USER:
            return "USER";
        case DbObject.DOMAIN:
            return "DOMAIN";
        default:
            // not supported by parser, but required when trying to find a
            // comment
            return "type" + type;
        }
    }

    @Override
    public String getCreateSQL() {
        StringBuilder buff = new StringBuilder("COMMENT ON ");
        buff.append(getTypeName(objectType)).append(' ').
                append(quotedObjectName).append(" IS ");
        if (commentText == null) {
            buff.append("NULL");
        } else {
            StringUtils.quoteStringSQL(buff, commentText);
        }
        return buff.toString();
    }

    @Override
    public int getType() {
        return DbObject.COMMENT;
    }

    @Override
    public void removeChildrenAndResources(SessionLocal session) {
        database.removeMeta(session, getId());
    }

    @Override
    public void checkRename() {
        throw DbException.getInternalError();
    }

    /**
     * Get the comment key name for the given database object. This key name is
     * used internally to associate the comment to the object.
     *
     * @param obj the object
     * @return the key name
     */
    static String getKey(DbObject obj) {
        StringBuilder builder = new StringBuilder(getTypeName(obj.getType())).append(' ');
        obj.getSQL(builder, DEFAULT_SQL_FLAGS);
        return builder.toString();
    }

    /**
     * Set the comment text.
     *
     * @param comment the text
     */
    public void setCommentText(String comment) {
        this.commentText = comment;
    }

}
