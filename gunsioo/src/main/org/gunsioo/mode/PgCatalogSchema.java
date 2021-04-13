/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.mode;

import java.util.HashMap;
import java.util.Map;

import org.gunsioo.engine.Constants;
import org.gunsioo.engine.Database;
import org.gunsioo.engine.SessionLocal;
import org.gunsioo.engine.User;
import org.gunsioo.schema.MetaSchema;
import org.gunsioo.table.Table;

/**
 * {@code pg_catalog} schema.
 */
public final class PgCatalogSchema extends MetaSchema {

    private volatile HashMap<String, Table> tables;

    /**
     * Creates new instance of {@code pg_catalog} schema.
     *
     * @param database
     *            the database
     * @param owner
     *            the owner of the schema (system user)
     */
    public PgCatalogSchema(Database database, User owner) {
        super(database, Constants.PG_CATALOG_SCHEMA_ID, database.sysIdentifier(Constants.SCHEMA_PG_CATALOG), owner);
    }

    @Override
    protected Map<String, Table> getMap(SessionLocal session) {
        HashMap<String, Table> map = tables;
        if (map == null) {
            map = fillMap();
        }
        return map;
    }

    private synchronized HashMap<String, Table> fillMap() {
        HashMap<String, Table> map = tables;
        if (map == null) {
            map = database.newStringMap();
            for (int type = 0; type < PgCatalogTable.META_TABLE_TYPE_COUNT; type++) {
                PgCatalogTable table = new PgCatalogTable(this, Constants.PG_CATALOG_SCHEMA_ID - type, type);
                map.put(table.getName(), table);
            }
            tables = map;
        }
        return map;
    }

}
