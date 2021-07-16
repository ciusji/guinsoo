/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.command.ddl;

import org.guinsoo.engine.SessionLocal;
import org.guinsoo.schema.Schema;

/**
 * This class represents a non-transaction statement that involves a schema and
 * requires schema owner rights.
 */
abstract class SchemaOwnerCommand extends SchemaCommand {

    /**
     * Create a new command.
     *
     * @param session
     *            the session
     * @param schema
     *            the schema
     */
    SchemaOwnerCommand(SessionLocal session, Schema schema) {
        super(session, schema);
    }

    @Override
    public final long update() {
        Schema schema = getSchema();
        session.getUser().checkSchemaOwner(schema);
        return update(schema);
    }

    abstract long update(Schema schema);

}
