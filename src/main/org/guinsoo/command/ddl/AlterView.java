/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.command.ddl;

import org.guinsoo.command.CommandInterface;
import org.guinsoo.engine.SessionLocal;
import org.guinsoo.message.DbException;
import org.guinsoo.table.TableView;

/**
 * This class represents the statement
 * ALTER VIEW
 */
public class AlterView extends DefineCommand {

    private boolean ifExists;
    private TableView view;

    public AlterView(SessionLocal session) {
        super(session);
    }

    public void setIfExists(boolean b) {
        ifExists = b;
    }

    public void setView(TableView view) {
        this.view = view;
    }

    @Override
    public long update() {
        if (view == null && ifExists) {
            return 0;
        }
        session.getUser().checkSchemaOwner(view.getSchema());
        DbException e = view.recompile(session, false, true);
        if (e != null) {
            throw e;
        }
        return 0;
    }

    @Override
    public int getType() {
        return CommandInterface.ALTER_VIEW;
    }

}
