/*
 * Copyright 2004-2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.command.ddl;

import java.util.ArrayList;

import org.guinsoo.command.query.Query;
import org.guinsoo.api.ErrorCode;
import org.guinsoo.command.CommandInterface;
import org.guinsoo.engine.Database;
import org.guinsoo.engine.SessionLocal;
import org.guinsoo.expression.Parameter;
import org.guinsoo.message.DbException;
import org.guinsoo.schema.Schema;
import org.guinsoo.table.Column;
import org.guinsoo.table.Table;
import org.guinsoo.table.TableType;
import org.guinsoo.table.TableView;
import org.guinsoo.util.HasSQL;
import org.guinsoo.value.TypeInfo;

/**
 * This class represents the statement
 * CREATE VIEW
 */
public class CreateView extends SchemaOwnerCommand {

    private Query select;
    private String viewName;
    private boolean ifNotExists;
    private String selectSQL;
    private String[] columnNames;
    private String comment;
    private boolean orReplace;
    private boolean force;
    private boolean isTableExpression;

    public CreateView(SessionLocal session, Schema schema) {
        super(session, schema);
    }

    public void setViewName(String name) {
        viewName = name;
    }

    public void setSelect(Query select) {
        this.select = select;
    }

    public void setIfNotExists(boolean ifNotExists) {
        this.ifNotExists = ifNotExists;
    }

    public void setSelectSQL(String selectSQL) {
        this.selectSQL = selectSQL;
    }

    public void setColumnNames(String[] cols) {
        this.columnNames = cols;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setOrReplace(boolean orReplace) {
        this.orReplace = orReplace;
    }

    public void setForce(boolean force) {
        this.force = force;
    }

    public void setTableExpression(boolean isTableExpression) {
        this.isTableExpression = isTableExpression;
    }

    @Override
    long update(Schema schema) {
        Database db = session.getDatabase();
        TableView view = null;
        Table old = schema.findTableOrView(session, viewName);
        if (old != null) {
            if (ifNotExists) {
                return 0;
            }
            if (!orReplace || TableType.VIEW != old.getTableType()) {
                throw DbException.get(ErrorCode.VIEW_ALREADY_EXISTS_1, viewName);
            }
            view = (TableView) old;
        }
        int id = getObjectId();
        String querySQL;
        if (select == null) {
            querySQL = selectSQL;
        } else {
            ArrayList<Parameter> params = select.getParameters();
            if (params != null && !params.isEmpty()) {
                throw DbException.getUnsupportedException("parameters in views");
            }
            querySQL = select.getPlanSQL(HasSQL.DEFAULT_SQL_FLAGS);
        }
        Column[] columnTemplatesAsUnknowns = null;
        Column[] columnTemplatesAsStrings = null;
        if (columnNames != null) {
            columnTemplatesAsUnknowns = new Column[columnNames.length];
            columnTemplatesAsStrings = new Column[columnNames.length];
            for (int i = 0; i < columnNames.length; ++i) {
                // non table expressions are fine to use unknown column type
                columnTemplatesAsUnknowns[i] = new Column(columnNames[i], TypeInfo.TYPE_UNKNOWN);
                // table expressions can't have unknown types - so we use string instead
                columnTemplatesAsStrings[i] = new Column(columnNames[i], TypeInfo.TYPE_VARCHAR);
            }
        }
        if (view == null) {
            if (isTableExpression) {
                view = TableView.createTableViewMaybeRecursive(schema, id, viewName, querySQL, null,
                        columnTemplatesAsStrings, session, false /* literalsChecked */, isTableExpression,
                        false/*isTemporary*/, db);
            } else {
                view = new TableView(schema, id, viewName, querySQL, null, columnTemplatesAsUnknowns, session,
                        false/* allow recursive */, false/* literalsChecked */, isTableExpression, false/*temporary*/);
            }
        } else {
            // TODO support isTableExpression in replace function...
            view.replace(querySQL, columnTemplatesAsUnknowns, session, false, force, false);
            view.setModified();
        }
        if (comment != null) {
            view.setComment(comment);
        }
        if (old == null) {
            db.addSchemaObject(session, view);
            db.unlockMeta(session);
        } else {
            db.updateMeta(session, view);
        }

        // TODO: if we added any table expressions that aren't used by this view, detect them
        // and drop them - otherwise they will leak and never get cleaned up.

        return 0;
    }

    @Override
    public int getType() {
        return CommandInterface.CREATE_VIEW;
    }

}
