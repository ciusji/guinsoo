/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.test.synth.sql;

/**
 * Represents an index.
 */
public class Index {
    private final Table table;
    private final String name;
    private final Column[] columns;
    private final boolean unique;

    Index(Table table, String name, Column[] columns, boolean unique) {
        this.table = table;
        this.name = name;
        this.columns = columns;
        this.unique = unique;
    }

    String getName() {
        return name;
    }

    String getCreateSQL() {
        String sql = "CREATE ";
        if (unique) {
            sql += "UNIQUE ";
        }
        sql += "INDEX " + name + " ON " + table.getName() + "(";
        for (int i = 0; i < columns.length; i++) {
            if (i > 0) {
                sql += ", ";
            }
            sql += columns[i].getName();
        }
        sql += ")";
        return sql;
    }

    String getDropSQL() {
        return "DROP INDEX " + name;
    }

    Table getTable() {
        return table;
    }

}
