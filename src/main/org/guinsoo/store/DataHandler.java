/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.store;

import org.guinsoo.message.DbException;
import org.guinsoo.value.CompareMode;
import org.guinsoo.util.SmallLRUCache;
import org.guinsoo.util.TempFileDeleter;

/**
 * A data handler contains a number of callback methods, mostly related to CLOB
 * and BLOB handling. The most important implementing class is a database.
 */
public interface DataHandler {

    /**
     * Get the database path.
     *
     * @return the database path
     */
    String getDatabasePath();

    /**
     * Open a file at the given location.
     *
     * @param name the file name
     * @param mode the mode
     * @param mustExist whether the file must already exist
     * @return the file
     */
    FileStore openFile(String name, String mode, boolean mustExist);

    /**
     * Check if the simulated power failure occurred.
     * This call will decrement the countdown.
     *
     * @throws DbException if the simulated power failure occurred
     */
    void checkPowerOff() throws DbException;

    /**
     * Check if writing is allowed.
     *
     * @throws DbException if it is not allowed
     */
    void checkWritingAllowed() throws DbException;

    /**
     * Get the maximum length of a in-place large object
     *
     * @return the maximum size
     */
    int getMaxLengthInplaceLob();

    /**
     * Get the compression algorithm used for large objects.
     *
     * @param type the data type (CLOB or BLOB)
     * @return the compression algorithm, or null
     */
    String getLobCompressionAlgorithm(int type);

    /**
     * Get the temp file deleter mechanism.
     *
     * @return the temp file deleter
     */
    TempFileDeleter getTempFileDeleter();

    /**
     * Get the synchronization object for lob operations.
     *
     * @return the synchronization object
     */
    Object getLobSyncObject();

    /**
     * Get the lob file list cache if it is used.
     *
     * @return the cache or null
     */
    SmallLRUCache<String, String[]> getLobFileListCache();

    /**
     * Get the lob storage mechanism to use.
     *
     * @return the lob storage mechanism
     */
    LobStorageInterface getLobStorage();

    /**
     * Read from a lob.
     *
     * @param lobId the lob id
     * @param hmac the message authentication code
     * @param offset the offset within the lob
     * @param buff the target buffer
     * @param off the offset within the target buffer
     * @param length the number of bytes to read
     * @return the number of bytes read
     */
    int readLob(long lobId, byte[] hmac, long offset, byte[] buff, int off, int length);

    /**
     * Return compare mode.
     *
     * @return Compare mode.
     */
    CompareMode getCompareMode();
}
