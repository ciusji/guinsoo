/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.pagestore;

import java.lang.ref.WeakReference;
import java.security.AccessControlException;
import org.guinsoo.Driver;
import org.guinsoo.engine.Constants;
import org.guinsoo.engine.Database;
import org.guinsoo.message.Trace;
import org.guinsoo.message.TraceSystem;

/**
 * The writer thread is responsible to flush the transaction log
 * from time to time.
 */
class WriterThread implements Runnable {

    /**
     * The reference to the database.
     *
     * Thread objects are not garbage collected
     * until they returned from the run() method
     * (even if they where never started)
     * so if the connection was not closed,
     * the database object cannot get reclaimed
     * by the garbage collector if we use a hard reference.
     */
    private volatile WeakReference<Database> databaseRef;

    private int writeDelay;
    private Thread thread;
    private volatile boolean stop;

    private WriterThread(Database database, int writeDelay) {
        this.databaseRef = new WeakReference<>(database);
        this.writeDelay = writeDelay;
    }

    /**
     * Change the write delay
     *
     * @param writeDelay the new write delay
     */
    void setWriteDelay(int writeDelay) {
        this.writeDelay = writeDelay;
    }

    /**
     * Create and start a new writer thread for the given database. If the
     * thread can't be created, this method returns null.
     *
     * @param database the database
     * @param writeDelay the delay
     * @return the writer thread object or null
     */
    static WriterThread create(Database database, int writeDelay) {
        try {
            WriterThread writer = new WriterThread(database, writeDelay);
            writer.thread = new Thread(writer, "Guinsoo Log Writer " + database.getShortName());
            Driver.setThreadContextClassLoader(writer.thread);
            writer.thread.setDaemon(true);
            return writer;
        } catch (AccessControlException e) {
            // // Google App Engine does not allow threads
            return null;
        }
    }

    @Override
    public void run() {
        while (!stop) {
            Database database = databaseRef.get();
            if (database == null) {
                break;
            }
            int wait = writeDelay;
            try {
                database.flush();
            } catch (Exception e) {
                TraceSystem traceSystem = database.getTraceSystem();
                if (traceSystem != null) {
                    traceSystem.getTrace(Trace.DATABASE).error(e, "flush");
                }
            }

            // wait 0 mean wait forever, which is not what we want
            wait = Math.max(wait, Constants.MIN_WRITE_DELAY);
            synchronized (this) {
                while (!stop && wait > 0) {
                    // wait 100 ms at a time
                    int w = Math.min(wait, 100);
                    try {
                        wait(w);
                    } catch (InterruptedException e) {
                        // ignore
                    }
                    wait -= w;
                }
            }
        }
        databaseRef = null;
    }

    /**
     * Stop the thread. This method is called when closing the database.
     */
    void stopThread() {
        stop = true;
        synchronized (this) {
            notify();
        }
        // can't do thread.join(), because this thread might be holding
        // a lock that the writer thread is waiting for
    }

    /**
     * Start the thread. This method is called after opening the database
     * (to avoid deadlocks)
     */
    void startThread() {
        thread.start();
        thread = null;
    }

}
