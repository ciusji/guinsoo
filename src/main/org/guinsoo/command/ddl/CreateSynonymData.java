/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.command.ddl;

import org.guinsoo.engine.SessionLocal;
import org.guinsoo.schema.Schema;

/**
 * The data required to create a synonym.
 */
public class CreateSynonymData {

    /**
     * The schema.
     */
    public Schema schema;

    /**
     * The synonyms name.
     */
    public String synonymName;

    /**
     * The name of the table the synonym is created for.
     */
    public String synonymFor;

    /** Schema synonymFor is located in. */
    public Schema synonymForSchema;

    /**
     * The object id.
     */
    public int id;

    /**
     * The session.
     */
    public SessionLocal session;

}
