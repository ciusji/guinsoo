/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.jmx;

import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;
import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.guinsoo.command.Command;
import org.guinsoo.engine.ConnectionInfo;
import org.guinsoo.engine.Constants;
import org.guinsoo.engine.Database;
import org.guinsoo.engine.SessionLocal;
import org.guinsoo.message.DbException;
import org.guinsoo.mvstore.db.Store;
import org.guinsoo.pagestore.PageStore;
import org.guinsoo.table.Table;
import org.guinsoo.util.NetworkConnectionInfo;

/**
 * The MBean implementation.
 *
 * @author Eric Dong
 * @author Thomas Mueller
 */
public class DatabaseInfo implements DatabaseInfoMBean {

    private static final Map<String, ObjectName> MBEANS = new HashMap<>();

    /** Database. */
    private final Database database;

    private DatabaseInfo(Database database) {
        if (database == null) {
            throw new IllegalArgumentException("Argument 'database' must not be null");
        }
        this.database = database;
    }

    /**
     * Returns a JMX new ObjectName instance.
     *
     * @param name name of the MBean
     * @param path the path
     * @return a new ObjectName instance
     * @throws JMException if the ObjectName could not be created
     */
    private static ObjectName getObjectName(String name, String path)
            throws JMException {
        name = name.replace(':', '_');
        path = path.replace(':', '_');
        Hashtable<String, String> map = new Hashtable<>();
        map.put("name", name);
        map.put("path", path);
        return new ObjectName("org.guinsoo", map);
    }

    /**
     * Registers an MBean for the database.
     *
     * @param connectionInfo connection info
     * @param database database
     */
    public static void registerMBean(ConnectionInfo connectionInfo,
                                     Database database) throws JMException {
        String path = connectionInfo.getName();
        if (!MBEANS.containsKey(path)) {
            MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
            String name = database.getShortName();
            ObjectName mbeanObjectName = getObjectName(name, path);
            MBEANS.put(path, mbeanObjectName);
            DatabaseInfo info = new DatabaseInfo(database);
            Object mbean = new DocumentedMBean(info, DatabaseInfoMBean.class);
            mbeanServer.registerMBean(mbean, mbeanObjectName);
        }
    }

    /**
     * Unregisters the MBean for the database if one is registered.
     *
     * @param name database name
     */
    public static void unregisterMBean(String name) throws Exception {
        ObjectName mbeanObjectName = MBEANS.remove(name);
        if (mbeanObjectName != null) {
            MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
            mbeanServer.unregisterMBean(mbeanObjectName);
        }
    }

    @Override
    public boolean isExclusive() {
        return database.getExclusiveSession() != null;
    }

    @Override
    public boolean isReadOnly() {
        return database.isReadOnly();
    }

    @Override
    public String getMode() {
        return database.getMode().getName();
    }

    @Deprecated
    @Override
    public boolean isMultiThreaded() {
        return database.isMVStore();
    }

    @Deprecated
    @Override
    public boolean isMvcc() {
        return database.isMVStore();
    }

    @Override
    public int getLogMode() {
        PageStore pageStore = database.getPageStore();
        if (pageStore != null) {
            return pageStore.getLogMode();
        }
        return PageStore.LOG_MODE_OFF;
    }

    @Override
    public void setLogMode(int value) {
        PageStore pageStore = database.getPageStore();
        if (pageStore == null) {
            throw DbException.getUnsupportedException("MV_STORE=FALSE && LOG");
        }
        if (database.isPersistent() && value != pageStore.getLogMode()) {
            pageStore.setLogMode(value);
        }
    }

    @Override
    public int getTraceLevel() {
        return database.getTraceSystem().getLevelFile();
    }

    @Override
    public void setTraceLevel(int level) {
        database.getTraceSystem().setLevelFile(level);
    }

    @Override
    public long getFileWriteCountTotal() {
        if (database.isPersistent()) {
            // TODO remove this method when removing the page store
            // (the MVStore doesn't support it)
            PageStore pageStore = database.getPageStore();
            if (pageStore != null) {
                return pageStore.getWriteCountTotal();
            }
        }
        return 0;
    }

    @Override
    public long getFileWriteCount() {
        if (database.isPersistent()) {
            Store store = database.getStore();
            if (store != null) {
                return store.getMvStore().getFileStore().getWriteCount();
            }
            PageStore pageStore = database.getPageStore();
            if (pageStore != null) {
                return pageStore.getWriteCount();
            }
        }
        return 0;
    }

    @Override
    public long getFileReadCount() {
        if (database.isPersistent()) {
            Store store = database.getStore();
            if (store != null) {
                return store.getMvStore().getFileStore().getReadCount();
            }
            PageStore pageStore = database.getPageStore();
            if (pageStore != null) {
                return pageStore.getReadCount();
            }
        }
        return 0;
    }

    @Override
    public long getFileSize() {
        long size = 0;
        if (database.isPersistent()) {
            Store store = database.getStore();
            if (store != null) {
                size = store.getMvStore().getFileStore().size();
            } else {
                PageStore pageStore = database.getPageStore();
                if (pageStore != null) {
                    size = pageStore.getPageCount() * pageStore.getPageSize();
                }
            }
        }
        return size / 1024;
    }

    @Override
    public int getCacheSizeMax() {
        if (database.isPersistent()) {
            Store store = database.getStore();
            if (store != null) {
                return store.getMvStore().getCacheSize() * 1024;
            }
            PageStore pageStore = database.getPageStore();
            if (pageStore != null) {
                return pageStore.getCache().getMaxMemory();
            }
        }
        return 0;
    }

    @Override
    public void setCacheSizeMax(int kb) {
        if (database.isPersistent()) {
            database.setCacheSize(kb);
        }
    }

    @Override
    public int getCacheSize() {
        if (database.isPersistent()) {
            Store store = database.getStore();
            if (store != null) {
                return store.getMvStore().getCacheSizeUsed() * 1024;
            }
            PageStore pageStore = database.getPageStore();
            if (pageStore != null) {
                return pageStore.getCache().getMemory();
            }
        }
        return 0;
    }

    @Override
    public String getVersion() {
        return Constants.FULL_VERSION;
    }

    @Override
    public String listSettings() {
        StringBuilder builder = new StringBuilder();
        for (Entry<String, String> e : database.getSettings().getSortedSettings()) {
            builder.append(e.getKey()).append(" = ").append(e.getValue()).append('\n');
        }
        return builder.toString();
    }

    @Override
    public String listSessions() {
        StringBuilder buff = new StringBuilder();
        for (SessionLocal session : database.getSessions(false)) {
            buff.append("session id: ").append(session.getId());
            buff.append(" user: ").
                    append(session.getUser().getName()).
                    append('\n');
            NetworkConnectionInfo networkConnectionInfo = session.getNetworkConnectionInfo();
            if (networkConnectionInfo != null) {
                buff.append("server: ").append(networkConnectionInfo.getServer()).append('\n') //
                        .append("clientAddr: ").append(networkConnectionInfo.getClient()).append('\n');
                String clientInfo = networkConnectionInfo.getClientInfo();
                if (clientInfo != null) {
                    buff.append("clientInfo: ").append(clientInfo).append('\n');
                }
            }
            buff.append("connected: ").
                    append(session.getSessionStart().getString()).
                    append('\n');
            Command command = session.getCurrentCommand();
            if (command != null) {
                buff.append("statement: ")
                        .append(command)
                        .append('\n')
                        .append("started: ")
                        .append(session.getCommandStartOrEnd().getString())
                        .append('\n');
            }
            for (Table table : session.getLocks()) {
                if (table.isLockedExclusivelyBy(session)) {
                    buff.append("write lock on ");
                } else {
                    buff.append("read lock on ");
                }
                buff.append(table.getSchema().getName()).
                        append('.').append(table.getName()).
                        append('\n');
            }
            buff.append('\n');
        }
        return buff.toString();
    }

}
