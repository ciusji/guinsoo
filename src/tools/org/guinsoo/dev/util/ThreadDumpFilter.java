/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.dev.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.LineNumberReader;
import java.io.PrintWriter;

/**
 * Filter full thread dumps from a log file.
 */
public class ThreadDumpFilter {

    /**
     * Usage: java ThreadDumpFilter &lt;log.txt &gt;threadDump.txt
     *
     * @param a the file name
     */
    public static void main(String... a) throws Exception {
        String fileName = a[0];
        LineNumberReader in = new LineNumberReader(
                new BufferedReader(new FileReader(fileName)));
        PrintWriter writer = new PrintWriter(new BufferedWriter(
                new FileWriter(fileName + ".filtered.txt")));
        for (String s; (s = in.readLine()) != null;) {
            if (s.startsWith("Full thread")) {
                do {
                    writer.println(s);
                    s = in.readLine();
                } while(s != null && (s.length() == 0 || " \t\"".indexOf(s.charAt(0)) >= 0));
            }
        }
        writer.close();
        in.close();
    }
}
