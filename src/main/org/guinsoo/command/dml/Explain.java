/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.command.dml;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.guinsoo.mvstore.db.Store;
import org.guinsoo.command.CommandInterface;
import org.guinsoo.command.Prepared;
import org.guinsoo.engine.Database;
import org.guinsoo.engine.DbObject;
import org.guinsoo.engine.SessionLocal;
import org.guinsoo.expression.Expression;
import org.guinsoo.expression.ExpressionColumn;
import org.guinsoo.pagestore.PageStore;
import org.guinsoo.result.LocalResult;
import org.guinsoo.result.ResultInterface;
import org.guinsoo.table.Column;
import org.guinsoo.util.HasSQL;
import org.guinsoo.value.TypeInfo;
import org.guinsoo.value.ValueVarchar;

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
