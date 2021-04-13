/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://h2database.com/html/license.html).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.command.dml;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import org.gunsioo.command.CommandInterface;
import org.gunsioo.command.Prepared;
import org.gunsioo.engine.Database;
import org.gunsioo.engine.DbObject;
import org.gunsioo.engine.SessionLocal;
import org.gunsioo.expression.Expression;
import org.gunsioo.expression.ExpressionColumn;
import org.gunsioo.mvstore.db.Store;
import org.gunsioo.pagestore.PageStore;
import org.gunsioo.result.LocalResult;
import org.gunsioo.result.ResultInterface;
import org.gunsioo.table.Column;
import org.gunsioo.util.HasSQL;
import org.gunsioo.value.TypeInfo;
import org.gunsioo.value.ValueVarchar;

/**
 * This class represents the statement
 * EXPLAIN
 */
public class Explain extends Prepared {

    private Prepared command;
    private LocalResult result;
    private boolean executeCommand;

    public Explain(SessionLocal session) {
        super(session);
    }

    public void setCommand(Prepared command) {
        this.command = command;
    }

    public Prepared getCommand() {
        return command;
    }

    @Override
    public void prepare() {
        command.prepare();
    }

    public void setExecuteCommand(boolean executeCommand) {
        this.executeCommand = executeCommand;
    }

    @Override
    public ResultInterface queryMeta() {
        return query(-1);
    }

    @Override
    protected void checkParameters() {
        // Check params only in case of EXPLAIN ANALYZE
        if (executeCommand) {
            super.checkParameters();
        }
    }

    @Override
    public ResultInterface query(long maxrows) {
        Database db = session.getDatabase();
        Expression[] expressions = { new ExpressionColumn(db, new Column("PLAN", TypeInfo.TYPE_VARCHAR)) };
        result = new LocalResult(session, expressions, 1, 1);
        int sqlFlags = HasSQL.ADD_PLAN_INFORMATION;
        if (maxrows >= 0) {
            String plan;
            if (executeCommand) {
                Store store = null;
                PageStore pageStore = null;
                if (db.isPersistent()) {
                    store = db.getStore();
                    if (store != null) {
                        store.statisticsStart();
                    } else {
                        pageStore = db.getPageStore();
                        if (pageStore != null) {
                            pageStore.statisticsStart();
                        }
                    }
                }
                if (command.isQuery()) {
                    command.query(maxrows);
                } else {
                    command.update();
                }
                plan = command.getPlanSQL(sqlFlags);
                Map<String, Integer> statistics = null;
                if (store != null) {
                    statistics = store.statisticsEnd();
                } else if (pageStore != null) {
                    statistics = pageStore.statisticsEnd();
                }
                if (statistics != null) {
                    int total = 0;
                    for (Entry<String, Integer> e : statistics.entrySet()) {
                        total += e.getValue();
                    }
                    if (total > 0) {
                        statistics = new TreeMap<>(statistics);
                        StringBuilder buff = new StringBuilder();
                        if (statistics.size() > 1) {
                            buff.append("total: ").append(total).append('\n');
                        }
                        for (Entry<String, Integer> e : statistics.entrySet()) {
                            int value = e.getValue();
                            int percent = (int) (100L * value / total);
                            buff.append(e.getKey()).append(": ").append(value);
                            if (statistics.size() > 1) {
                                buff.append(" (").append(percent).append("%)");
                            }
                            buff.append('\n');
                        }
                        plan += "\n/*\n" + buff.toString() + "*/";
                    }
                }
            } else {
                plan = command.getPlanSQL(sqlFlags);
            }
            add(plan);
        }
        result.done();
        return result;
    }

    private void add(String text) {
        result.addRow(ValueVarchar.get(text));
    }

    @Override
    public boolean isQuery() {
        return true;
    }

    @Override
    public boolean isTransactional() {
        return true;
    }

    @Override
    public boolean isReadOnly() {
        return command.isReadOnly();
    }

    @Override
    public int getType() {
        return executeCommand ? CommandInterface.EXPLAIN_ANALYZE : CommandInterface.EXPLAIN;
    }

    @Override
    public void collectDependencies(HashSet<DbObject> dependencies) {
        command.collectDependencies(dependencies);
    }

}
