/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.schema;

import org.guinsoo.message.DbException;
import org.guinsoo.table.Table;

/**
 * User-defined Java function or aggregate function.
 */
public abstract class UserDefinedFunction extends SchemaObject {

    String className;

    UserDefinedFunction(Schema newSchema, int id, String name, int traceModuleId) {
        super(newSchema, id, name, traceModuleId);
    }

    @Override
    public final String getCreateSQLForCopy(Table table, String quotedName) {
        throw DbException.getInternalError(toString());
    }

    @Override
    public final void checkRename() {
        throw DbException.getUnsupportedException("RENAME");
    }

    public final String getJavaClassName() {
        return className;
    }

}
