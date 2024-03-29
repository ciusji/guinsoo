/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.table;

import java.util.Collections;
import java.util.List;

import org.guinsoo.command.ddl.CreateTableData;
import org.guinsoo.engine.Database;
import org.guinsoo.result.SearchRow;
import org.guinsoo.result.SortOrder;
import org.guinsoo.value.Value;
import org.guinsoo.index.IndexType;
import org.guinsoo.util.StringUtils;

/**
 * The base class of a regular table, or a user defined table.
 *
 * @author Thomas Mueller
 * @author Sergi Vladykin
 */
public abstract class TableBase extends Table {

    /**
     * The table engine used (null for regular tables).
     */
    private final String tableEngine;
    /** Provided table parameters */
    private final List<String> tableEngineParams;

    private final boolean globalTemporary;

    /**
     * Returns main index column if index is an primary key index and has only
     * one column with _ROWID_ compatible data type.
     *
     * @param indexType type of an index
     * @param cols columns of the index
     * @return main index column or {@link SearchRow#ROWID_INDEX}
     */
    public static int getMainIndexColumn(IndexType indexType, IndexColumn[] cols) {
        if (!indexType.isPrimaryKey() || cols.length != 1) {
            return SearchRow.ROWID_INDEX;
        }
        IndexColumn first = cols[0];
        if ((first.sortType & SortOrder.DESCENDING) != 0) {
            return SearchRow.ROWID_INDEX;
        }
        switch (first.column.getType().getValueType()) {
        case Value.TINYINT:
        case Value.SMALLINT:
        case Value.INTEGER:
        case Value.BIGINT:
            return first.column.getColumnId();
        default:
            return SearchRow.ROWID_INDEX;
        }
    }

    public TableBase(CreateTableData data) {
        super(data.schema, data.id, data.tableName,
                data.persistIndexes, data.persistData);
        this.tableEngine = data.tableEngine;
        this.globalTemporary = data.globalTemporary;
        if (data.tableEngineParams != null) {
            this.tableEngineParams = data.tableEngineParams;
        } else {
            this.tableEngineParams = Collections.emptyList();
        }
        setTemporary(data.temporary);
        setColumns(data.columns.toArray(new Column[0]));
    }

    @Override
    public String getDropSQL() {
        StringBuilder builder = new StringBuilder("DROP TABLE IF EXISTS ");
        getSQL(builder, DEFAULT_SQL_FLAGS).append(" CASCADE");
        return builder.toString();
    }

    @Override
    public String getCreateSQLForMeta() {
        return getCreateSQL(true);
    }

    @Override
    public String getCreateSQL() {
        return getCreateSQL(false);
    }

    private String getCreateSQL(boolean forMeta) {
        Database db = getDatabase();
        if (db == null) {
            // closed
            return null;
        }
        StringBuilder buff = new StringBuilder("CREATE ");
        if (isTemporary()) {
            if (isGlobalTemporary()) {
                buff.append("GLOBAL ");
            } else {
                buff.append("LOCAL ");
            }
            buff.append("TEMPORARY ");
        } else if (isPersistIndexes()) {
            buff.append("CACHED ");
        } else {
            buff.append("MEMORY ");
        }
        buff.append("TABLE ");
        if (isHidden) {
            buff.append("IF NOT EXISTS ");
        }
        getSQL(buff, DEFAULT_SQL_FLAGS);
        if (comment != null) {
            buff.append(" COMMENT ");
            StringUtils.quoteStringSQL(buff, comment);
        }
        buff.append("(\n    ");
        for (int i = 0, l = columns.length; i < l; i++) {
            if (i > 0) {
                buff.append(",\n    ");
            }
            buff.append(columns[i].getCreateSQL(forMeta));
        }
        buff.append("\n)");
        if (tableEngine != null) {
            String d = db.getSettings().defaultTableEngine;
            if (d == null || !tableEngine.endsWith(d)) {
                buff.append("\nENGINE ");
                StringUtils.quoteIdentifier(buff, tableEngine);
            }
        }
        if (!tableEngineParams.isEmpty()) {
            buff.append("\nWITH ");
            for (int i = 0, l = tableEngineParams.size(); i < l; i++) {
                if (i > 0) {
                    buff.append(", ");
                }
                StringUtils.quoteIdentifier(buff, tableEngineParams.get(i));
            }
        }
        if (!isPersistIndexes() && !isPersistData()) {
            buff.append("\nNOT PERSISTENT");
        }
        if (isHidden) {
            buff.append("\nHIDDEN");
        }
        return buff.toString();
    }

    @Override
    public boolean isGlobalTemporary() {
        return globalTemporary;
    }

}
