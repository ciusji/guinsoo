/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.test.db;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

import org.guinsoo.test.utils.SelfDestructor;

/**
 * A task that can be run as a separate process.
 */
public abstract class TaskDef {

    /**
     * Run the class. This method is called by the task framework, and should
     * not be called directly from the application.
     *
     * @param args the command line arguments
     */
    public static void main(String... args) {
        SelfDestructor.startCountdown(60);
        TaskDef task;
        try {
            String className = args[0];
            task = (TaskDef) Class.forName(className).getDeclaredConstructor().newInstance();
            System.out.println("running");
        } catch (Throwable t) {
            System.out.println("init error: " + t);
            t.printStackTrace();
            return;
        }
        try {
            task.run(Arrays.copyOf(args, args.length - 1));
        } catch (Throwable t) {
            System.out.println("error: " + t);
            t.printStackTrace();
        }
    }

    /**
     * Run the task.
     *
     * @param args the command line arguments
     */
    abstract void run(String... args) throws Exception;

    /**
     * Receive a message from the process over the standard output.
     *
     * @return the message
     */
    protected String receive() {
        try {
            return new BufferedReader(new InputStreamReader(System.in)).readLine();
        } catch (IOException e) {
            throw new RuntimeException("Error reading from input", e);
        }
    }

    /**
     * Send a message to the process over the standard input.
     *
     * @param message the message
     */
    protected void send(String message) {
        System.out.println(message);
        System.out.flush();
    }

}
