/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.engine;

import org.gunsioo.message.DbException;
import org.gunsioo.message.Trace;
import org.gunsioo.table.Table;

/**
 * A persistent database setting.
 */
public final class Setting extends DbObject {

    private int intValue;
    private String stringValue;

    public Setting(Database database, int id, String settingName) {
        super(database, id, settingName, Trace.SETTING);
    }

    @Override
    public String getSQL(int sqlFlags) {
        return getName();
    }

    @Override
    public StringBuilder getSQL(StringBuilder builder, int sqlFlags) {
        return builder.append(getName());
    }

    public void setIntValue(int value) {
        intValue = value;
    }

    public int getIntValue() {
        return intValue;
    }

    public void setStringValue(String value) {
        stringValue = value;
    }

    public String getStringValue() {
        return stringValue;
    }

    @Override
    public String getCreateSQLForCopy(Table table, String quotedName) {
        throw DbException.getInternalError(toString());
    }

    @Override
    public String getCreateSQL() {
        StringBuilder buff = new StringBuilder("SET ");
        getSQL(buff, DEFAULT_SQL_FLAGS).append(' ');
        if (stringValue != null) {
            buff.append(stringValue);
        } else {
            buff.append(intValue);
        }
        return buff.toString();
    }

    @Override
    public int getType() {
        return DbObject.SETTING;
    }

    @Override
    public void removeChildrenAndResources(SessionLocal session) {
        database.removeMeta(session, getId());
        invalidate();
    }

    @Override
    public void checkRename() {
        throw DbException.getUnsupportedException("RENAME");
    }

}
