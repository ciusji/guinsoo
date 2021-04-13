/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.engine;

import java.lang.ref.WeakReference;

import org.gunsioo.message.Trace;

/**
 * This class is responsible to close a database after the specified delay. A
 * database closer object only exists if there is no user connected to the
 * database.
 */
class DelayedDatabaseCloser extends Thread {

    private final Trace trace;
    private volatile WeakReference<Database> databaseRef;
    private int delayInMillis;

    DelayedDatabaseCloser(Database db, int delayInMillis) {
        databaseRef = new WeakReference<>(db);
        this.delayInMillis = delayInMillis;
        trace = db.getTrace(Trace.DATABASE);
        setName("Gunsioo Close Delay " + db.getShortName());
        setDaemon(true);
        start();
    }

    /**
     * Stop and disable the database closer. This method is called after a session
     * has been created.
     */
    void reset() {
        databaseRef = null;
    }

    @Override
    public void run() {
        while (delayInMillis > 0) {
            try {
                int step = 100;
                Thread.sleep(step);
                delayInMillis -= step;
            } catch (Exception e) {
                // ignore InterruptedException
            }
            WeakReference<Database> ref = databaseRef;
            if (ref == null || ref.get() == null) {
                return;
            }
        }
        Database database;
        WeakReference<Database> ref = databaseRef;
        if (ref != null && (database = ref.get()) != null) {
            try {
                database.close(false);
            } catch (RuntimeException e) {
                // this can happen when stopping a web application,
                // if loading classes is no longer allowed
                // it would throw an IllegalStateException
                try {
                    trace.error(e, "could not close the database");
                    // if this was successful, we ignore the exception
                    // otherwise not
                } catch (Throwable e2) {
                    e.addSuppressed(e2);
                    throw e;
                }
            }
        }
    }

}
