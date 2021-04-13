/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://h2database.com/html/license.html).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.dev.ftp.server;

/**
 * Event listener for the FTP Server.
 */
public interface FtpEventListener {

    /**
     * Called before the given command is processed.
     *
     * @param event the event
     */
    void beforeCommand(FtpEvent event);

    /**
     * Called after the command has been processed.
     *
     * @param event the event
     */
    void afterCommand(FtpEvent event);

    /**
     * Called when an unsupported command is processed.
     * This method is called after beforeCommand.
     *
     * @param event the event
     */
    void onUnsupportedCommand(FtpEvent event);
}
