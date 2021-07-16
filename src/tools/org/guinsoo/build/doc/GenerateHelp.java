/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.build.doc;

import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Types;
import org.guinsoo.tools.Csv;
import org.guinsoo.tools.SimpleResultSet;

/**
 * Generates the help.csv file that is included in the jar file.
 */
public class GenerateHelp {

    /**
     * This method is called when executing this application from the command
     * line.
     *
     * @param args the command line parameters
     */
    public static void main(String... args) throws Exception {
        String in = "src/docsrc/help/help.csv";
        String out = "src/main/org/guinsoo/res/help.csv";
        Csv csv = new Csv();
        csv.setLineCommentCharacter('#');
        ResultSet rs = csv.read(in, null, null);
        SimpleResultSet rs2 = new SimpleResultSet();
        ResultSetMetaData meta = rs.getMetaData();
        int columnCount = meta.getColumnCount() - 1;
        for (int i = 0; i < columnCount; i++) {
            rs2.addColumn(meta.getColumnLabel(1 + i), Types.VARCHAR, 0, 0);
        }
        while (rs.next()) {
            Object[] row = new Object[columnCount];
            for (int i = 0; i < columnCount; i++) {
                String s = rs.getString(1 + i);
                switch (i) {
                case 2:
                    s = s.replaceAll("@c@ ", "").replaceAll("@guinsoo@ ", "").replaceAll("@c@", "").replaceAll("@guinsoo@", "");
                    break;
                case 3: {
                    int len = s.length();
                    int end = 0;
                    for (; end < len; end++) {
                        char ch = s.charAt(end);
                        if (ch == '.') {
                            end++;
                            break;
                        }
                        if (ch == '"') {
                            do {
                                end++;
                            } while (end < len && s.charAt(end) != '"');
                        }
                    }
                    s = s.substring(0, end);
                }
                }
                row[i] = s;
            }
            rs2.addRow(row);
        }
        BufferedWriter writer = Files.newBufferedWriter(Paths.get(out));
        writer.write("# Copyright 2021 Guinsoo Group. " +
                "Multiple-Licensed under the MPL 2.0,\n" +
                "# and the EPL 1.0 " +
                "(https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).\n" +
                "# Initial Developer: Guinsoo Group\n");
        csv = new Csv();
        csv.setLineSeparator("\n");
        csv.write(writer, rs2);
    }

}
