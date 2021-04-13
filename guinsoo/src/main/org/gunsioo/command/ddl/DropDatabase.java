/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.command.ddl;

import java.util.ArrayList;
import java.util.Collection;

import org.gunsioo.command.CommandInterface;
import org.gunsioo.engine.Database;
import org.gunsioo.engine.DbObject;
import org.gunsioo.engine.Right;
import org.gunsioo.engine.RightOwner;
import org.gunsioo.engine.Role;
import org.gunsioo.engine.SessionLocal;
import org.gunsioo.engine.User;
import org.gunsioo.schema.Schema;
import org.gunsioo.schema.SchemaObject;
import org.gunsioo.schema.Sequence;
import org.gunsioo.table.Table;
import org.gunsioo.table.TableType;
import org.gunsioo.value.ValueNull;

/**
 * This class represents the statement
 * DROP ALL OBJECTS
 */
public class DropDatabase extends DefineCommand {

    private boolean dropAllObjects;
    private boolean deleteFiles;

    public DropDatabase(SessionLocal session) {
        super(session);
    }

    @Override
    public long update() {
        if (dropAllObjects) {
            dropAllObjects();
        }
        if (deleteFiles) {
            session.getDatabase().setDeleteFilesOnDisconnect(true);
        }
        return 0;
    }

    private void dropAllObjects() {
        User user = session.getUser();
        user.checkAdmin();
        Database db = session.getDatabase();
        db.lockMeta(session);

        // There can be dependencies between tables e.g. using computed columns,
        // so we might need to loop over them multiple times.
        boolean runLoopAgain;
        do {
            ArrayList<Table> tables = db.getAllTablesAndViews();
            ArrayList<Table> toRemove = new ArrayList<>(tables.size());
            for (Table t : tables) {
                if (t.getName() != null &&
                        TableType.VIEW == t.getTableType()) {
                    toRemove.add(t);
                }
            }
            for (Table t : tables) {
                if (t.getName() != null &&
                        TableType.TABLE_LINK == t.getTableType()) {
                    toRemove.add(t);
                }
            }
            for (Table t : tables) {
                if (t.getName() != null &&
                        TableType.TABLE == t.getTableType() &&
                        !t.isHidden()) {
                    toRemove.add(t);
                }
            }
            for (Table t : tables) {
                if (t.getName() != null &&
                        TableType.EXTERNAL_TABLE_ENGINE == t.getTableType() &&
                        !t.isHidden()) {
                    toRemove.add(t);
                }
            }
            runLoopAgain = false;
            for (Table t : toRemove) {
                if (t.getName() == null) {
                    // ignore, already dead
                } else if (db.getDependentTable(t, t) == null) {
                    db.removeSchemaObject(session, t);
                } else {
                    runLoopAgain = true;
                }
            }
        } while (runLoopAgain);

        // TODO session-local temp tables are not removed
        Collection<Schema> schemas = db.getAllSchemasNoMeta();
        for (Schema schema : schemas) {
            if (schema.canDrop()) {
                db.removeDatabaseObject(session, schema);
            }
        }
        ArrayList<SchemaObject> list = new ArrayList<>();
        for (Schema schema : schemas) {
            for (Sequence sequence : schema.getAllSequences()) {
                // ignore these. the ones we want to drop will get dropped when we
                // drop their associated tables, and we will ignore the problematic
                // ones that belong to session-local temp tables.
                if (!sequence.getBelongsToTable()) {
                    list.add(sequence);
                }
            }
        }
        // maybe constraints and triggers on system tables will be allowed in
        // the future
        addAll(schemas, DbObject.CONSTRAINT, list);
        addAll(schemas, DbObject.TRIGGER, list);
        addAll(schemas, DbObject.CONSTANT, list);
        // Function aliases and aggregates are stored together
        addAll(schemas, DbObject.FUNCTION_ALIAS, list);
        addAll(schemas, DbObject.DOMAIN, list);
        for (SchemaObject obj : list) {
            if (!obj.getSchema().isValid() || obj.isHidden()) {
                continue;
            }
            db.removeSchemaObject(session, obj);
        }
        Role publicRole = db.getPublicRole();
        for (RightOwner rightOwner : db.getAllUsersAndRoles()) {
            if (rightOwner != user && rightOwner != publicRole) {
                db.removeDatabaseObject(session, rightOwner);
            }
        }
        for (Right right : db.getAllRights()) {
            db.removeDatabaseObject(session, right);
        }
        for (SessionLocal s : db.getSessions(false)) {
            s.setLastIdentity(ValueNull.INSTANCE);
        }
    }

    private static void addAll(Collection<Schema> schemas, int type, ArrayList<SchemaObject> list) {
        for (Schema schema : schemas) {
            schema.getAll(type, list);
        }
    }

    public void setDropAllObjects(boolean b) {
        this.dropAllObjects = b;
    }

    public void setDeleteFiles(boolean b) {
        this.deleteFiles = b;
    }

    @Override
    public int getType() {
        return CommandInterface.DROP_ALL_OBJECTS;
    }

}
