/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.test.synth.sql;

import java.util.ArrayList;

/**
 * Represents a connection to a simulated database.
 */
public class DbState implements DbInterface {

    private boolean connected;
    private boolean autoCommit;
    private final TestSynth config;
    private ArrayList<Table> tables = new ArrayList<>();
    private ArrayList<Index> indexes = new ArrayList<>();

    DbState(TestSynth config) {
        this.config = config;
    }

    @Override
    public void reset() {
        tables = new ArrayList<>();
        indexes = new ArrayList<>();
    }

    @Override
    public void connect() {
        connected = true;
    }

    @Override
    public void disconnect() {
        connected = false;
    }

    @Override
    public void createTable(Table table) {
        tables.add(table);
    }

    @Override
    public void dropTable(Table table) {
        tables.remove(table);
    }

    @Override
    public void createIndex(Index index) {
        indexes.add(index);
    }

    @Override
    public void dropIndex(Index index) {
        indexes.remove(index);
    }

    @Override
    public Result insert(Table table, Column[] c, Value[] v) {
        return null;
    }

    @Override
    public Result select(String sql) {
        return null;
    }

    @Override
    public Result delete(Table table, String condition) {
        return null;
    }

    @Override
    public Result update(Table table, Column[] columns, Value[] values,
            String condition) {
        return null;
    }

    @Override
    public void setAutoCommit(boolean b) {
        autoCommit = b;
    }

    @Override
    public void commit() {
        // nothing to do
    }

    @Override
    public void rollback() {
        // nothing to do
    }

    /**
     * Get a random table.
     *
     * @return the table
     */
    Table randomTable() {
        if (tables.size() == 0) {
            return null;
        }
        int i = config.random().getInt(tables.size());
        return tables.get(i);
    }

    @Override
    public void end() {
        // nothing to do
    }

    @Override
    public String toString() {
        return "autocommit: " + autoCommit + " connected: " + connected;
    }

}
