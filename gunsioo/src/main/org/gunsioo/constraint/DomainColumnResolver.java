/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://h2database.com/html/license.html).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.constraint;

import org.gunsioo.table.Column;
import org.gunsioo.table.ColumnResolver;
import org.gunsioo.value.TypeInfo;
import org.gunsioo.value.Value;

/**
 * The single column resolver resolves the VALUE column.
 * It is used to parse a domain constraint.
 */
public class DomainColumnResolver implements ColumnResolver {

    private final Column column;
    private Value value;
    private String name;

    public DomainColumnResolver(TypeInfo typeInfo) {
        this.column = new Column("VALUE", typeInfo);
    }

    public void setValue(Value value) {
        this.value = value;
    }

    @Override
    public Value getValue(Column col) {
        return value;
    }

    @Override
    public Column[] getColumns() {
        return new Column[] { column };
    }

    @Override
    public Column findColumn(String name) {
        return null;
    }

    void setColumnName(String newName) {
        name = newName;
    }

    void resetColumnName() {
        name = null;
    }

    /**
     * Return column name to use or null.
     *
     * @return column name to use or null
     */
    public String getColumnName() {
        return name;
    }

    /**
     * Return the type of the column.
     *
     * @return the type of the column
     */
    public TypeInfo getValueType() {
        return column.getType();
    }

}
