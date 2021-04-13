/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.schema;

import java.sql.Connection;
import java.sql.SQLException;

import org.gunsioo.api.Aggregate;
import org.gunsioo.api.AggregateFunction;
import org.gunsioo.engine.DbObject;
import org.gunsioo.engine.SessionLocal;
import org.gunsioo.message.DbException;
import org.gunsioo.message.Trace;
import org.gunsioo.util.JdbcUtils;
import org.gunsioo.util.StringUtils;
import org.gunsioo.value.DataType;
import org.gunsioo.value.TypeInfo;

/**
 * Represents a user-defined aggregate function.
 */
public final class UserAggregate extends UserDefinedFunction {

    private Class<?> javaClass;

    public UserAggregate(Schema schema, int id, String name, String className,
            boolean force) {
        super(schema, id, name, Trace.FUNCTION);
        this.className = className;
        if (!force) {
            getInstance();
        }
    }

    public Aggregate getInstance() {
        if (javaClass == null) {
            javaClass = JdbcUtils.loadUserClass(className);
        }
        Object obj;
        try {
            obj = javaClass.getDeclaredConstructor().newInstance();
            Aggregate agg;
            if (obj instanceof Aggregate) {
                agg = (Aggregate) obj;
            } else {
                agg = new AggregateWrapper((AggregateFunction) obj);
            }
            return agg;
        } catch (Exception e) {
            throw DbException.convert(e);
        }
    }

    @Override
    public String getDropSQL() {
        StringBuilder builder = new StringBuilder("DROP AGGREGATE IF EXISTS ");
        return getSQL(builder, DEFAULT_SQL_FLAGS).toString();
    }

    @Override
    public String getCreateSQL() {
        StringBuilder builder = new StringBuilder("CREATE FORCE AGGREGATE ");
        getSQL(builder, DEFAULT_SQL_FLAGS).append(" FOR ");
        return StringUtils.quoteStringSQL(builder, className).toString();
    }

    @Override
    public int getType() {
        return DbObject.AGGREGATE;
    }

    @Override
    public synchronized void removeChildrenAndResources(SessionLocal session) {
        database.removeMeta(session, getId());
        className = null;
        javaClass = null;
        invalidate();
    }

    /**
     * Wrap {@link AggregateFunction} in order to behave as
     * {@link org.gunsioo.api.Aggregate}
     **/
    private static class AggregateWrapper implements Aggregate {
        private final AggregateFunction aggregateFunction;

        AggregateWrapper(AggregateFunction aggregateFunction) {
            this.aggregateFunction = aggregateFunction;
        }

        @Override
        public void init(Connection conn) throws SQLException {
            aggregateFunction.init(conn);
        }

        @Override
        public int getInternalType(int[] inputTypes) throws SQLException {
            int[] sqlTypes = new int[inputTypes.length];
            for (int i = 0; i < inputTypes.length; i++) {
                sqlTypes[i] = DataType.convertTypeToSQLType(TypeInfo.getTypeInfo(inputTypes[i]));
            }
            return  DataType.convertSQLTypeToValueType(aggregateFunction.getType(sqlTypes));
        }

        @Override
        public void add(Object value) throws SQLException {
            aggregateFunction.add(value);
        }

        @Override
        public Object getResult() throws SQLException {
            return aggregateFunction.getResult();
        }
    }

}
