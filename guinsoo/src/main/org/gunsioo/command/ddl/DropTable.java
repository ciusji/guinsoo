/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.command.ddl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.gunsioo.api.ErrorCode;
import org.gunsioo.command.CommandInterface;
import org.gunsioo.constraint.Constraint;
import org.gunsioo.constraint.ConstraintActionType;
import org.gunsioo.engine.Database;
import org.gunsioo.engine.Right;
import org.gunsioo.engine.SessionLocal;
import org.gunsioo.message.DbException;
import org.gunsioo.schema.Schema;
import org.gunsioo.table.Table;
import org.gunsioo.table.TableView;
import org.gunsioo.util.StringUtils;
import org.gunsioo.util.Utils;

/**
 * This class represents the statement
 * DROP TABLE
 */
public class DropTable extends DefineCommand {

    private boolean ifExists;
    private ConstraintActionType dropAction;

    private final ArrayList<SchemaAndTable> tables = Utils.newSmallArrayList();

    public DropTable(SessionLocal session) {
        super(session);
        dropAction = session.getDatabase().getSettings().dropRestrict ?
                ConstraintActionType.RESTRICT :
                    ConstraintActionType.CASCADE;
    }

    public void setIfExists(boolean b) {
        ifExists = b;
    }

    /**
     * Add a table to drop.
     *
     * @param schema the schema
     * @param tableName the table name
     */
    public void addTable(Schema schema, String tableName) {
        tables.add(new SchemaAndTable(schema, tableName));
    }

    private boolean prepareDrop() {
        HashSet<Table> tablesToDrop = new HashSet<>();
        for (SchemaAndTable schemaAndTable : tables) {
            String tableName = schemaAndTable.tableName;
            Table table = schemaAndTable.schema.findTableOrView(session, tableName);
            if (table == null) {
                if (!ifExists) {
                    throw DbException.get(ErrorCode.TABLE_OR_VIEW_NOT_FOUND_1, tableName);
                }
            } else {
                session.getUser().checkTableRight(table, Right.SCHEMA_OWNER);
                if (!table.canDrop()) {
                    throw DbException.get(ErrorCode.CANNOT_DROP_TABLE_1, tableName);
                }
                tablesToDrop.add(table);
            }
        }
        if (tablesToDrop.isEmpty()) {
            return false;
        }
        for (Table table : tablesToDrop) {
            ArrayList<String> dependencies = new ArrayList<>();
            if (dropAction == ConstraintActionType.RESTRICT) {
                CopyOnWriteArrayList<TableView> dependentViews = table.getDependentViews();
                if (dependentViews != null && !dependentViews.isEmpty()) {
                    for (TableView v : dependentViews) {
                        if (!tablesToDrop.contains(v)) {
                            dependencies.add(v.getName());
                        }
                    }
                }
                final List<Constraint> constraints = table.getConstraints();
                if (constraints != null && !constraints.isEmpty()) {
                    for (Constraint c : constraints) {
                        if (!tablesToDrop.contains(c.getTable())) {
                            dependencies.add(c.getName());
                        }
                    }
                }
                if (!dependencies.isEmpty()) {
                    throw DbException.get(ErrorCode.CANNOT_DROP_2, table.getName(),
                            StringUtils.join(new StringBuilder(), dependencies, ", ").toString());
                }
            }
            table.lock(session, true, true);
        }
        return true;
    }

    private void executeDrop() {
        for (SchemaAndTable schemaAndTable : tables) {
            // need to get the table again, because it may be dropped already
            // meanwhile (dependent object, or same object)
            Table table = schemaAndTable.schema.findTableOrView(session, schemaAndTable.tableName);
            if (table != null) {
                table.setModified();
                Database db = session.getDatabase();
                db.lockMeta(session);
                db.removeSchemaObject(session, table);
            }
        }
    }

    @Override
    public long update() {
        if (prepareDrop()) {
            executeDrop();
        }
        return 0;
    }

    public void setDropAction(ConstraintActionType dropAction) {
        this.dropAction = dropAction;
    }

    @Override
    public int getType() {
        return CommandInterface.DROP_TABLE;
    }

    private static final class SchemaAndTable {

        final Schema schema;

        final String tableName;

        SchemaAndTable(Schema schema, String tableName) {
            this.schema = schema;
            this.tableName = tableName;
        }

    }

}
