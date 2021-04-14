/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.command.ddl;

import org.gunsioo.engine.SessionLocal;
import org.gunsioo.schema.Schema;

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