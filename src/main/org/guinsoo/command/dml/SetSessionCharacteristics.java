/*
 * Copyright 2004-2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.command.dml;

import org.guinsoo.command.CommandInterface;
import org.guinsoo.command.Prepared;
import org.guinsoo.engine.IsolationLevel;
import org.guinsoo.engine.SessionLocal;
import org.guinsoo.result.ResultInterface;

/**
 * This class represents the statement SET SESSION CHARACTERISTICS
 */
public class SetSessionCharacteristics extends Prepared {

    private final IsolationLevel isolationLevel;

    public SetSessionCharacteristics(SessionLocal session, IsolationLevel isolationLevel) {
        super(session);
        this.isolationLevel = isolationLevel;
    }

    @Override
    public boolean isTransactional() {
        return false;
    }

    @Override
    public long update() {
        session.setIsolationLevel(isolationLevel);
        return 0;
    }

    @Override
    public boolean needRecompile() {
        return false;
    }

    @Override
    public ResultInterface queryMeta() {
        return null;
    }

    @Override
    public int getType() {
        return CommandInterface.SET;
    }

}
