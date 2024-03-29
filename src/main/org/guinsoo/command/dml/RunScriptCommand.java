/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.command.dml;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.guinsoo.command.CommandContainer;
import org.guinsoo.command.CommandInterface;
import org.guinsoo.command.Prepared;
import org.guinsoo.engine.SessionLocal;
import org.guinsoo.message.DbException;
import org.guinsoo.result.ResultInterface;
import org.guinsoo.util.ScriptReader;

/**
 * This class represents the statement
 * RUNSCRIPT
 */
public class RunScriptCommand extends ScriptBase {

    /**
     * The byte order mark.
     * 0xfeff because this is the Unicode char
     * represented by the UTF-8 byte order mark (EF BB BF).
     */
    private static final char UTF8_BOM = '\uFEFF';

    private Charset charset = StandardCharsets.UTF_8;

    private boolean quirksMode;

    private boolean variableBinary;

    public RunScriptCommand(SessionLocal session) {
        super(session);
    }

    @Override
    public long update() {
        session.getUser().checkAdmin();
        int count = 0;
        boolean oldQuirksMode = session.isQuirksMode();
        boolean oldVariableBinary = session.isVariableBinary();
        try {
            openInput();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, charset));
            // if necessary, strip the BOM from the front of the file
            reader.mark(1);
            if (reader.read() != UTF8_BOM) {
                reader.reset();
            }
            if (quirksMode) {
                session.setQuirksMode(true);
            }
            if (variableBinary) {
                session.setVariableBinary(true);
            }
            ScriptReader r = new ScriptReader(reader);
            while (true) {
                String sql = r.readStatement();
                if (sql == null) {
                    break;
                }
                execute(sql);
                count++;
                if ((count & 127) == 0) {
                    checkCanceled();
                }
            }
            r.close();
        } catch (IOException e) {
            throw DbException.convertIOException(e, null);
        } finally {
            if (quirksMode) {
                session.setQuirksMode(oldQuirksMode);
            }
            if (variableBinary) {
                session.setVariableBinary(oldVariableBinary);
            }
            closeIO();
        }
        return count;
    }

    private void execute(String sql) {
        try {
            Prepared command = session.prepare(sql);
            CommandContainer commandContainer = new CommandContainer(session, sql, command);
            if (commandContainer.isQuery()) {
                commandContainer.executeQuery(0, false);
            } else {
                commandContainer.executeUpdate(null);
            }
        } catch (DbException e) {
            throw e.addSQL(sql);
        }
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    /**
     * Enables or disables the quirks mode.
     *
     * @param quirksMode
     *            whether quirks mode should be enabled
     */
    public void setQuirksMode(boolean quirksMode) {
        this.quirksMode = quirksMode;
    }

    /**
     * Changes parsing of a BINARY data type.
     *
     * @param variableBinary
     *            {@code true} to parse BINARY as VARBINARY, {@code false} to
     *            parse it as is
     */
    public void setVariableBinary(boolean variableBinary) {
        this.variableBinary = variableBinary;
    }

    @Override
    public ResultInterface queryMeta() {
        return null;
    }

    @Override
    public int getType() {
        return CommandInterface.RUNSCRIPT;
    }

}
