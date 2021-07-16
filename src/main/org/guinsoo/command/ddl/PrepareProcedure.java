/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.command.ddl;

import java.util.ArrayList;

import org.guinsoo.command.CommandInterface;
import org.guinsoo.command.Prepared;
import org.guinsoo.engine.Procedure;
import org.guinsoo.engine.SessionLocal;
import org.guinsoo.expression.Parameter;

/**
 * This class represents the statement
 * PREPARE
 */
public class PrepareProcedure extends DefineCommand {

    private String procedureName;
    private Prepared prepared;

    public PrepareProcedure(SessionLocal session) {
        super(session);
    }

    @Override
    public void checkParameters() {
        // no not check parameters
    }

    @Override
    public long update() {
        Procedure proc = new Procedure(procedureName, prepared);
        prepared.setParameterList(parameters);
        prepared.setPrepareAlways(prepareAlways);
        prepared.prepare();
        session.addProcedure(proc);
        return 0;
    }

    public void setProcedureName(String name) {
        this.procedureName = name;
    }

    public void setPrepared(Prepared prep) {
        this.prepared = prep;
    }

    @Override
    public ArrayList<Parameter> getParameters() {
        return new ArrayList<>(0);
    }

    @Override
    public int getType() {
        return CommandInterface.PREPARE;
    }

}
