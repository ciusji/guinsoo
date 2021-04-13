/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.server;

/**
 * A shutdown handler is a listener for shutdown events.
 */
public interface ShutdownHandler {

    /**
     * Tell the listener to shut down.
     */
    void shutdown();
}
