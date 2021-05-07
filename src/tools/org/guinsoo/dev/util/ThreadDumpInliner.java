/*
 * Copyright 2004-2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
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
 * Convert a list of thread dumps into one line per thread.
 */
public class ThreadDumpInliner {

    /**
     * Usage: java ThreadDumpInliner threadDump.txt
     *
     * @param a the file name
     */
    public static void main(String... a) throws Exception {
        String fileName = a[0];
        LineNumberReader in = new LineNumberReader(
                new BufferedReader(new FileReader(fileName)));
        PrintWriter writer = new PrintWriter(new BufferedWriter(
                new FileWriter(fileName + ".lines.txt")));

        StringBuilder buff = new StringBuilder();
        for (String s; (s = in.readLine()) != null;) {
            if (s.trim().length() == 0) {
                continue;
            }
            if (s.startsWith(" ") || s.startsWith("\t")) {
                buff.append('\t').append(s.trim());
            } else {
                printNonEmpty(writer, buff.toString());
                buff = new StringBuilder(s);
            }
        }
        printNonEmpty(writer, buff.toString());
        in.close();
        writer.close();
    }

    private static void printNonEmpty(PrintWriter writer, String s) {
        s = s.trim();
        if (!s.isEmpty()) {
            writer.println(s);
        }
    }
}
