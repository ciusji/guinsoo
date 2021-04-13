/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.samples;

/**
 * This very simple sample application stops a Gunsioo TCP server
 * if it is running.
 */
public class ShutdownServer {

    /**
     * This method is called when executing this sample application from the
     * command line.
     *
     * @param args the command line parameters
     */
    public static void main(String... args) throws Exception {
        org.gunsioo.tools.Server.shutdownTcpServer("tcp://localhost:9094", "", false, false);
    }
}
