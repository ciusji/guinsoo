/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.command.ddl;

import java.util.ArrayList;
import java.util.HashSet;

import org.guinsoo.command.dml.Insert;
import org.guinsoo.command.query.Query;
import org.guinsoo.api.ErrorCode;
import org.guinsoo.command.CommandInterface;
import org.guinsoo.engine.Database;
import org.guinsoo.engine.DbObject;
import org.guinsoo.engine.SessionLocal;
import org.guinsoo.expression.Expression;
import org.guinsoo.message.DbException;
import org.guinsoo.schema.Schema;
import org.guinsoo.schema.Sequence;
import org.guinsoo.table.Column;
import org.guinsoo.table.Table;
import org.guinsoo.value.Value;

/**
 * This class represents the statement
 * CREATE TABLE
 */
public class CreateTable extends CommandWithColumns {

    private final CreateTableData data = new CreateTableData();
    private boolean ifNotExists;
    private boolean onCommitDrop;
    private boolean onCommitTruncate;
    private Query asQuery;
    private String comment;
    private boolean sortedInsertMode;
    private boolean withNoData;

    public CreateTable(SessionLocal session, Schema schema) {
        super(session, schema);
        data.persistIndexes = true;
        data.persistData = true;
    }

    public void setQuery(Query query) {
        this.asQuery = query;
    }

    public void setTemporary(boolean temporary) {
        data.temporary = temporary;
    }

    public void setTableName(String tableName) {
        data.tableName = tableName;
    }

    @Override
    public void addColumn(Column column) {
        data.columns.add(column);
    }

    public ArrayList<Column> getColumns() {
        return data.columns;
    }

    public void setIfNotExists(boolean ifNotExists) {
        this.ifNotExists = ifNotExists;
    }

    @Override
    public long update() {
        Schema schema = getSchema();
        boolean isSessionTemporary = data.temporary && !data.globalTemporary;
        if (!isSessionTemporary) {
            session.getUser().checkSchemaOwner(schema);
        }
        Database db = session.getDatabase();
        if (!db.isPersistent()) {
            data.persistIndexes = false;
        }
        if (!isSessionTemporary) {
            db.lockMeta(session);
        }
        if (schema.resolveTableOrView(session, data.tableName) != null) {
            if (ifNotExists) {
                return 0;
            }
            throw DbException.get(ErrorCode.TABLE_OR_VIEW_ALREADY_EXISTS_1, data.tableName);
        }
        if (asQuery != null) {
            // OPTIMIZED_SQL
            asQuery.prepare();
            if (data.columns.isEmpty()) {
                generateColumnsFromQuery();
            } else if (data.columns.size() != asQuery.getColumnCount()) {
                throw DbException.get(ErrorCode.COLUMN_COUNT_DOES_NOT_MATCH);
            } else {
                ArrayList<Column> columns = data.columns;
                for (int i = 0; i < columns.size(); i++) {
                    Column column = columns.get(i);
                    if (column.getType().getValueType() == Value.UNKNOWN) {
                        columns.set(i, new Column(column.getName(), asQuery.getExpressions().get(i).getType()));
                    }
                }
            }
        }
        changePrimaryKeysToNotNull(data.columns);
        data.id = getObjectId();
        data.create = create;
        data.session = session;
        /// !!!
        Table table = schema.createTable(data);
        ArrayList<Sequence> sequences = generateSequences(data.columns, data.temporary);
        table.setComment(comment);
        if (isSessionTemporary) {
            if (onCommitDrop) {
                table.setOnCommitDrop(true);
            }
            if (onCommitTruncate) {
                table.setOnCommitTruncate(true);
            }
            session.addLocalTempTable(table);
        } else {
            db.lockMeta(session);
            db.addSchemaObject(session, table);
        }
        try {
            for (Column c : data.columns) {
                c.prepareExpressions(session);
            }
            for (Sequence sequence : sequences) {
                table.addSequence(sequence);
            }
            createConstraints();
            HashSet<DbObject> set = new HashSet<>();
            table.addDependencies(set);
            for (DbObject obj : set) {
                if (obj == table) {
                    continue;
                }
                if (obj.getType() == DbObject.TABLE_OR_VIEW) {
                    if (obj instanceof Table) {
                        Table t = (Table) obj;
                        if (t.getId() > table.getId()) {
                            throw DbException.get(
                                    ErrorCode.FEATURE_NOT_SUPPORTED_1,
                                    "Table depends on another table " +
                                    "with a higher ID: " + t +
                                    ", this is currently not supported, " +
                                    "as it would prevent the database from " +
                                    "being re-opened");
                        }
                    }
                }
            }
            if (asQuery != null && !withNoData) {
                boolean flushSequences = false;
                if (!isSessionTemporary) {
                    db.unlockMeta(session);
                    for (Column c : table.getColumns()) {
                        Sequence s = c.getSequence();
                        if (s != null) {
                            flushSequences = true;
                            s.setTemporary(true);
                        }
                    }
                }
                boolean old = session.isUndoLogEnabled();
                try {
                    session.setUndoLogEnabled(false);
                    session.startStatementWithinTransaction(null);
                    Insert insert = new Insert(session);
                    insert.setSortedInsertMode(sortedInsertMode);
                    insert.setQuery(asQuery);
                    insert.setTable(table);
                    insert.setInsertFromSelect(true);
                    insert.prepare();
                    insert.update();
                } finally {
                    session.endStatement();
                    session.setUndoLogEnabled(old);
                }
                if (flushSequences) {
                    db.lockMeta(session);
                    for (Column c : table.getColumns()) {
                        Sequence s = c.getSequence();
                        if (s != null) {
                            s.setTemporary(false);
                            s.flush(session);
                        }
                    }
                }
            }
        } catch (DbException e) {
            try {
                db.checkPowerOff();
                db.removeSchemaObject(session, table);
                if (!transactional) {
                    session.commit(true);
                }
            } catch (Throwable ex) {
                e.addSuppressed(ex);
            }
            throw e;
        }
        return 0;
    }

    private void generateColumnsFromQuery() {
        int columnCount = asQuery.getColumnCount();
        ArrayList<Expression> expressions = asQuery.getExpressions();
        for (int i = 0; i < columnCount; i++) {
            Expression expr = expressions.get(i);
            addColumn(new Column(expr.getColumnNameForView(session, i), expr.getType()));
        }
    }

    public void setPersistIndexes(boolean persistIndexes) {
        data.persistIndexes = persistIndexes;
    }

    public void setGlobalTemporary(boolean globalTemporary) {
        data.globalTemporary = globalTemporary;
    }

    /**
     * This temporary table is dropped on commit.
     */
    public void setOnCommitDrop() {
        this.onCommitDrop = true;
    }

    /**
     * This temporary table is truncated on commit.
     */
    public void setOnCommitTruncate() {
        this.onCommitTruncate = true;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setPersistData(boolean persistData) {
        data.persistData = persistData;
        if (!persistData) {
            data.persistIndexes = false;
        }
    }

    public void setSortedInsertMode(boolean sortedInsertMode) {
        this.sortedInsertMode = sortedInsertMode;
    }

    public void setWithNoData(boolean withNoData) {
        this.withNoData = withNoData;
    }

    public void setTableEngine(String tableEngine) {
        data.tableEngine = tableEngine;
    }

    public void setTableEngineParams(ArrayList<String> tableEngineParams) {
        data.tableEngineParams = tableEngineParams;
    }

    public void setHidden(boolean isHidden) {
        data.isHidden = isHidden;
    }

    @Override
    public int getType() {
        return CommandInterface.CREATE_TABLE;
    }

}
