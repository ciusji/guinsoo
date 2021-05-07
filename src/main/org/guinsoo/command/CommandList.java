/*
 * Copyright 2004-2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.command;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.guinsoo.expression.Parameter;
import org.guinsoo.expression.ParameterInterface;
import org.guinsoo.engine.DbObject;
import org.guinsoo.engine.SessionLocal;
import org.guinsoo.result.ResultInterface;
import org.guinsoo.result.ResultWithGeneratedKeys;

/**
 * Represents a list of SQL statements.
 */
class CommandList extends Command {

    private CommandContainer command;
    private final ArrayList<Prepared> commands;
    private final ArrayList<Parameter> parameters;
    private String remaining;
    private Command remainingCommand;

    CommandList(SessionLocal session, String sql, CommandContainer command, ArrayList<Prepared> commands,
            ArrayList<Parameter> parameters, String remaining) {
        super(session, sql);
        this.command = command;
        this.commands = commands;
        this.parameters = parameters;
        this.remaining = remaining;
    }

    @Override
    public ArrayList<? extends ParameterInterface> getParameters() {
        return parameters;
    }

    private void executeRemaining() {
        for (Prepared prepared : commands) {
            prepared.prepare();
            if (prepared.isQuery()) {
                prepared.query(0);
            } else {
                prepared.update();
            }
        }
        if (remaining != null) {
            remainingCommand = session.prepareLocal(remaining);
            remaining = null;
            if (remainingCommand.isQuery()) {
                remainingCommand.query(0);
            } else {
                remainingCommand.update(null);
            }
        }
    }

    @Override
    public ResultWithGeneratedKeys update(Object generatedKeysRequest) {
        ResultWithGeneratedKeys result = command.executeUpdate(null);
        executeRemaining();
        return result;
    }

    @Override
    public ResultInterface query(long maxrows) {
        ResultInterface result = command.query(maxrows);
        executeRemaining();
        return result;
    }

    @Override
    public void stop() {
        command.stop();
        for (Prepared prepared : commands) {
            CommandContainer.clearCTE(session, prepared);
        }
        if (remainingCommand != null) {
            remainingCommand.stop();
        }
    }

    @Override
    public boolean isQuery() {
        return command.isQuery();
    }

    @Override
    public boolean isTransactional() {
        return true;
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

    @Override
    public ResultInterface queryMeta() {
        return command.queryMeta();
    }

    @Override
    public int getCommandType() {
        return command.getCommandType();
    }

    @Override
    public Set<DbObject> getDependencies() {
        HashSet<DbObject> dependencies = new HashSet<>();
        for (Prepared prepared : commands) {
            prepared.collectDependencies(dependencies);
        }
        return dependencies;
    }

    @Override
    protected boolean isCurrentCommandADefineCommand() {
        return command.isCurrentCommandADefineCommand();
    }
}
