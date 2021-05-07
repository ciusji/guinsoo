/*
 * Copyright 2004-2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.schema;

import java.sql.Connection;
import java.sql.SQLException;

import org.guinsoo.message.DbException;
import org.guinsoo.message.Trace;
import org.guinsoo.api.Aggregate;
import org.guinsoo.api.AggregateFunction;
import org.guinsoo.engine.DbObject;
import org.guinsoo.engine.SessionLocal;
import org.guinsoo.util.JdbcUtils;
import org.guinsoo.util.StringUtils;
import org.guinsoo.value.DataType;
import org.guinsoo.value.TypeInfo;

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
     * {@link Aggregate}
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
