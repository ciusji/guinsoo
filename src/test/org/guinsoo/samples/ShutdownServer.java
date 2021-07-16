/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.samples;

import org.guinsoo.tools.Server;

/**
 * This very simple sample application stops a Guinsoo TCP server
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
        Server.shutdownTcpServer("tcp://localhost:9094", "", false, false);
    }
}
