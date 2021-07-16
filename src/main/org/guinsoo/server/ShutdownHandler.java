/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.server;

/**
 * A shutdown handler is a listener for shutdown events.
 */
public interface ShutdownHandler {

    /**
     * Tell the listener to shut down.
     */
    void shutdown();
}
