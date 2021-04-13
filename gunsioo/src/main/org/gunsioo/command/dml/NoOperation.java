/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.command.dml;

import org.gunsioo.command.CommandInterface;
import org.gunsioo.command.Prepared;
import org.gunsioo.engine.SessionLocal;
import org.gunsioo.result.ResultInterface;

/**
 * Represents an empty statement or a statement that has no effect.
 */
public class NoOperation extends Prepared {

    public NoOperation(SessionLocal session) {
        super(session);
    }

    @Override
    public long update() {
        return 0;
    }

    @Override
    public boolean isTransactional() {
        return true;
    }

    @Override
    public boolean needRecompile() {
        return false;
    }

    @Override
    public boolean isReadOnly() {
        return true;
    }

    @Override
    public ResultInterface queryMeta() {
        return null;
    }

    @Override
    public int getType() {
        return CommandInterface.NO_OPERATION;
    }

}
