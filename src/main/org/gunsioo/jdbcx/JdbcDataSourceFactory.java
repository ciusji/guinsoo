/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.jdbcx;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;

import org.gunsioo.engine.Constants;
import org.gunsioo.engine.SysProperties;
import org.gunsioo.message.Trace;
import org.gunsioo.message.TraceSystem;

/**
 * This class is used to create new DataSource objects.
 * An application should not use this class directly.
 */
public final class JdbcDataSourceFactory implements ObjectFactory {

    private static final TraceSystem traceSystem;

    private final Trace trace;

    static {
        traceSystem = new TraceSystem(SysProperties.CLIENT_TRACE_DIRECTORY + "h2datasource"
                + Constants.SUFFIX_TRACE_FILE);
        traceSystem.setLevelFile(SysProperties.DATASOURCE_TRACE_LEVEL);
    }

    /**
     * The public constructor to create new factory objects.
     */
    public JdbcDataSourceFactory() {
        trace = traceSystem.getTrace(Trace.JDBCX);
    }

    /**
     * Creates a new object using the specified location or reference
     * information.
     *
     * @param obj the reference (this factory only supports objects of type
     *            javax.naming.Reference)
     * @param name unused
     * @param nameCtx unused
     * @param environment unused
     * @return the new JdbcDataSource, or null if the reference class name is
     *         not JdbcDataSource.
     */
    @Override
    public synchronized Object getObjectInstance(Object obj, Name name,
            Context nameCtx, Hashtable<?, ?> environment) {
        if (trace.isDebugEnabled()) {
            trace.debug("getObjectInstance obj={0} name={1} " +
                    "nameCtx={2} environment={3}", obj, name, nameCtx, environment);
        }
        if (obj instanceof Reference) {
            Reference ref = (Reference) obj;
            if (ref.getClassName().equals(JdbcDataSource.class.getName())) {
                JdbcDataSource dataSource = new JdbcDataSource();
                dataSource.setURL((String) ref.get("url").getContent());
                dataSource.setUser((String) ref.get("user").getContent());
                dataSource.setPassword((String) ref.get("password").getContent());
                dataSource.setDescription((String) ref.get("description").getContent());
                String s = (String) ref.get("loginTimeout").getContent();
                dataSource.setLoginTimeout(Integer.parseInt(s));
                return dataSource;
            }
        }
        return null;
    }

    /**
     * INTERNAL
     */
    public static TraceSystem getTraceSystem() {
        return traceSystem;
    }

    Trace getTrace() {
        return trace;
    }

}
