/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.samples;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

import org.guinsoo.tools.RunScript;
import org.guinsoo.util.StringUtils;

/**
 * The newsfeed application uses XML functions to create an RSS and Atom feed
 * from a simple SQL script. A textual representation of the data is created as
 * well.
 */
public class Newsfeed {

    /**
     * This method is called when executing this sample application from the
     * command line.
     *
     * @param args the command line parameters
     */
    public static void main(String... args) throws Exception {
        Path targetDir = Paths.get(args.length == 0 ? "." : args[0]);
        Class.forName("org.guinsoo.Driver");
        Connection conn = DriverManager.getConnection("jdbc:guinsoo:mem:", "sa", "");
        InputStream in = Newsfeed.class.getResourceAsStream("newsfeed.sql");
        ResultSet rs = RunScript.execute(conn, new InputStreamReader(in, StandardCharsets.ISO_8859_1));
        in.close();
        while (rs.next()) {
            String file = rs.getString("FILE");
            String content = rs.getString("CONTENT");
            if (file.endsWith(".txt")) {
                content = convertHtml2Text(content);
            }
            Files.createDirectories(targetDir);
            Files.write(targetDir.resolve(file), content.getBytes(StandardCharsets.UTF_8));
        }
        conn.close();
    }

    /**
     * Convert HTML text to plain text.
     *
     * @param html the html text
     * @return the plain text
     */
    private static String convertHtml2Text(String html) {
        String s = html;
        s = StringUtils.replaceAll(s, "<b>", "");
        s = StringUtils.replaceAll(s, "</b>", "");
        s = StringUtils.replaceAll(s, "<ul>", "");
        s = StringUtils.replaceAll(s, "</ul>", "");
        s = StringUtils.replaceAll(s, "<li>", "- ");
        s = StringUtils.replaceAll(s, "</li>", "");
        s = StringUtils.replaceAll(s, "<a href=\"", "( ");
        s = StringUtils.replaceAll(s, "\">", " ) ");
        s = StringUtils.replaceAll(s, "</a>", "");
        s = StringUtils.replaceAll(s, "<br />", "");
        s = StringUtils.replaceAll(s, "<br/>", "");
        s = StringUtils.replaceAll(s, "<br>", "");
        if (s.indexOf('<') >= 0 || s.indexOf('>') >= 0) {
            throw new RuntimeException("Unsupported HTML Tag: < or > in " + s);
        }
        return s;
    }
}
