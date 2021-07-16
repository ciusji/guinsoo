/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.dev.ftp.server;

/**
 * Describes an FTP event. This class is used by the FtpEventListener.
 */
public class FtpEvent {
    private final FtpControl control;
    private final String command;
    private final String param;

    FtpEvent(FtpControl control, String command, String param) {
        this.control = control;
        this.command = command;
        this.param = param;
    }

    /**
     * Get the FTP command. Example: RETR
     *
     * @return the command
     */
    public String getCommand() {
        return command;
    }

    /**
     * Get the FTP control object.
     *
     * @return the control object
     */
    public FtpControl getControl() {
        return control;
    }

    /**
     * Get the parameter of the FTP command (if any).
     *
     * @return the parameter
     */
    public String getParam() {
        return param;
    }
}
