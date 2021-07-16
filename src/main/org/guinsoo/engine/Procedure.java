/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.engine;

import org.guinsoo.command.Prepared;

/**
 * Represents a procedure. Procedures are implemented for PostgreSQL
 * compatibility.
 */
public class Procedure {

    private final String name;
    private final Prepared prepared;

    public Procedure(String name, Prepared prepared) {
        this.name = name;
        this.prepared = prepared;
    }

    public String getName() {
        return name;
    }

    public Prepared getPrepared() {
        return prepared;
    }

}
