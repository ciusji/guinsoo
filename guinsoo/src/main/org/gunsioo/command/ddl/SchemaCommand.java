/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.command.ddl;

import org.gunsioo.engine.SessionLocal;
import org.gunsioo.schema.Schema;

/**
 * This class represents a non-transaction statement that involves a schema.
 */
public abstract class SchemaCommand extends DefineCommand {

    private final Schema schema;

    /**
     * Create a new command.
     *
     * @param session the session
     * @param schema the schema
     */
    public SchemaCommand(SessionLocal session, Schema schema) {
        super(session);
        this.schema = schema;
    }

    /**
     * Get the schema
     *
     * @return the schema
     */
    protected final Schema getSchema() {
        return schema;
    }

}
