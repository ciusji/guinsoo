/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.util;

import org.gunsioo.message.Trace;

/**
 * The cache writer is called by the cache to persist changed data that needs to
 * be removed from the cache.
 */
public interface CacheWriter {

    /**
     * Persist a record.
     *
     * @param entry the cache entry
     */
    void writeBack(CacheObject entry);

    /**
     * Flush the transaction log, so that entries can be removed from the cache.
     * This is only required if the cache is full and contains data that is not
     * yet written to the log. It is required to write the log entries to the
     * log first, because the log is 'write ahead'.
     */
    void flushLog();

    /**
     * Get the trace writer.
     *
     * @return the trace writer
     */
    Trace getTrace();

}
