/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://h2database.com/html/license.html).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.command.ddl;

import java.util.ArrayList;

import org.gunsioo.engine.SessionLocal;
import org.gunsioo.schema.Schema;
import org.gunsioo.table.Column;

/**
 * The data required to create a table.
 */
public class CreateTableData {

    /**
     * The schema.
     */
    public Schema schema;

    /**
     * The table name.
     */
    public String tableName;

    /**
     * The object id.
     */
    public int id;

    /**
     * The column list.
     */
    public ArrayList<Column> columns = new ArrayList<>();

    /**
     * Whether this is a temporary table.
     */
    public boolean temporary;

    /**
     * Whether the table is global temporary.
     */
    public boolean globalTemporary;

    /**
     * Whether the indexes should be persisted.
     */
    public boolean persistIndexes;

    /**
     * Whether the data should be persisted.
     */
    public boolean persistData;

    /**
     * Whether to create a new table.
     */
    public boolean create;

    /**
     * The session.
     */
    public SessionLocal session;

    /**
     * The table engine to use for creating the table.
     */
    public String tableEngine;

    /**
     * The table engine params to use for creating the table.
     */
    public ArrayList<String> tableEngineParams;

    /**
     * The table is hidden.
     */
    public boolean isHidden;
}
