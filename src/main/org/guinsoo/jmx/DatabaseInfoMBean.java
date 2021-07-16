/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.jmx;

/**
 * Information and management operations for the given database.
 * @guinsoo.resource
 *
 * @author Eric Dong
 * @author Thomas Mueller
 */
public interface DatabaseInfoMBean {

    /**
     * Is the database open in exclusive mode?
     * @guinsoo.resource
     *
     * @return true if the database is open in exclusive mode, false otherwise
     */
    boolean isExclusive();

    /**
     * Is the database read-only?
     * @guinsoo.resource
     *
     * @return true if the database is read-only, false otherwise
     */
    boolean isReadOnly();

    /**
     * The database compatibility mode (REGULAR if no compatibility mode is
     * used).
     * @guinsoo.resource
     *
     * @return the database mode
     */
    String getMode();

    /**
     * Is multi-threading enabled?
     * @guinsoo.resource
     *
     * @return true if multi-threading is enabled, false otherwise
     */
    @Deprecated
    boolean isMultiThreaded();

    /**
     * Is MVCC (multi version concurrency) enabled?
     * @guinsoo.resource
     *
     * @return true if MVCC is enabled, false otherwise
     */
    @Deprecated
    boolean isMvcc();

    /**
     * The transaction log mode (0 disabled, 1 without sync, 2 enabled).
     * @guinsoo.resource
     *
     * @return the transaction log mode
     */
    int getLogMode();

    /**
     * Set the transaction log mode.
     *
     * @param value the new log mode
     */
    void setLogMode(int value);

    /**
     * The number of write operations since the database was created.
     * @guinsoo.resource
     *
     * @return the total write count
     */
    long getFileWriteCountTotal();

    /**
     * The number of write operations since the database was opened.
     * @guinsoo.resource
     *
     * @return the write count
     */
    long getFileWriteCount();

    /**
     * The file read count since the database was opened.
     * @guinsoo.resource
     *
     * @return the read count
     */
    long getFileReadCount();

    /**
     * The database file size in KB.
     * @guinsoo.resource
     *
     * @return the number of pages
     */
    long getFileSize();

    /**
     * The maximum cache size in KB.
     * @guinsoo.resource
     *
     * @return the maximum size
     */
    int getCacheSizeMax();

    /**
     * Change the maximum size.
     *
     * @param kb the cache size in KB.
     */
    void setCacheSizeMax(int kb);

    /**
     * The current cache size in KB.
     * @guinsoo.resource
     *
     * @return the current size
     */
    int getCacheSize();

    /**
     * The database version.
     * @guinsoo.resource
     *
     * @return the version
     */
    String getVersion();

    /**
     * The trace level (0 disabled, 1 error, 2 info, 3 debug).
     * @guinsoo.resource
     *
     * @return the level
     */
    int getTraceLevel();

    /**
     * Set the trace level.
     *
     * @param level the new value
     */
    void setTraceLevel(int level);

    /**
     * List the database settings.
     * @guinsoo.resource
     *
     * @return the database settings
     */
    String listSettings();

    /**
     * List sessions, including the queries that are in
     * progress, and locked tables.
     * @guinsoo.resource
     *
     * @return information about the sessions
     */
    String listSessions();

}
