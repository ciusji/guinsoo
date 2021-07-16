/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.samples;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

import org.guinsoo.store.fs.FileUtils;
import org.guinsoo.tools.Csv;
import org.guinsoo.tools.SimpleResultSet;

/**
 * This sample application shows how to use the CSV tool
 * to write CSV (comma separated values) files, and
 * how to use the tool to read such files.
 * See also the section CSV (Comma Separated Values) Support in the Tutorial.
 */
public class CsvSample {

    /**
     * This method is called when executing this sample application from the
     * command line.
     *
     * @param args the command line parameters
     */
    public static void main(String... args) throws SQLException {
        CsvSample.write();
        CsvSample.read();
        FileUtils.delete("data/test.csv");
    }

    /**
     * Write a CSV file.
     */
    static void write() throws SQLException {
        SimpleResultSet rs = new SimpleResultSet();
        rs.addColumn("NAME", Types.VARCHAR, 255, 0);
        rs.addColumn("EMAIL", Types.VARCHAR, 255, 0);
        rs.addColumn("PHONE", Types.VARCHAR, 255, 0);
        rs.addRow("Bob Meier", "bob.meier@abcde.abc", "+41123456789");
        rs.addRow("John Jones", "john.jones@abcde.abc", "+41976543210");
        new Csv().write("data/test.csv", rs, null);
    }

    /**
     * Read a CSV file.
     */
    static void read() throws SQLException {
        ResultSet rs = new Csv().read("data/test.csv", null, null);
        ResultSetMetaData meta = rs.getMetaData();
        while (rs.next()) {
            for (int i = 0; i < meta.getColumnCount(); i++) {
                System.out.println(
                        meta.getColumnLabel(i + 1) + ": " +
                        rs.getString(i + 1));
            }
            System.out.println();
        }
        rs.close();
    }

}
