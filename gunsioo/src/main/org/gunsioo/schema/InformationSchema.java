/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.schema;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.gunsioo.engine.Constants;
import org.gunsioo.engine.Database;
import org.gunsioo.engine.SessionLocal;
import org.gunsioo.engine.User;
import org.gunsioo.table.InformationSchemaTable;
import org.gunsioo.table.InformationSchemaTableLegacy;
import org.gunsioo.table.Table;

/**
 * Information schema.
 */
public final class InformationSchema extends MetaSchema {

    private volatile HashMap<String, Table> newTables;

    private volatile HashMap<String, Table> oldTables;

    /**
     * Creates new instance of information schema.
     *
     * @param database
     *            the database
     * @param owner
     *            the owner of the schema (system user)
     */
    public InformationSchema(Database database, User owner) {
        super(database, Constants.INFORMATION_SCHEMA_ID, database.sysIdentifier("INFORMATION_SCHEMA"), owner);
    }

    @Override
    protected Map<String, Table> getMap(SessionLocal session) {
        if (session == null) {
            return Collections.emptyMap();
        }
        boolean old = session.isOldInformationSchema();
        HashMap<String, Table> map = old ? oldTables : newTables;
        if (map == null) {
            map = fillMap(old);
        }
        return map;
    }

    private synchronized HashMap<String, Table> fillMap(boolean old) {
        HashMap<String, Table> map = old ? oldTables : newTables;
        if (map == null) {
            map = database.newStringMap(64);
            if (old) {
                for (int type = 0; type < InformationSchemaTableLegacy.META_TABLE_TYPE_COUNT; type++) {
                    InformationSchemaTableLegacy table = new InformationSchemaTableLegacy(this,
                            Constants.INFORMATION_SCHEMA_ID - type, type);
                    map.put(table.getName(), table);
                }
                oldTables = map;
            } else {
                for (int type = 0; type < InformationSchemaTable.META_TABLE_TYPE_COUNT; type++) {
                    InformationSchemaTable table = new InformationSchemaTable(this,
                            Constants.INFORMATION_SCHEMA_ID - type, type);
                    map.put(table.getName(), table);
                }
                newTables = map;
            }
        }
        return map;
    }

}
