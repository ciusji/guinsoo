/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.jcr;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.guinsoo.bnf.Bnf;
import org.guinsoo.build.BuildBase;
import org.guinsoo.build.doc.BnfRailroad;
import org.guinsoo.build.doc.BnfSyntax;
import org.guinsoo.build.doc.RailroadImages;
import org.guinsoo.server.web.PageParser;
import org.guinsoo.tools.Csv;
import org.guinsoo.util.StringUtils;

/**
 * JCR 2.0 / SQL-2 railroad generator.
 */
public class Railroads {

    private Bnf bnf;
    private final HashMap<String, Object> session = new HashMap<>();

    /**
     * This method is called when executing this application from the command
     * line.
     *
     * @param args the command line parameters
     */
    public static void main(String... args) throws Exception {
        new Railroads().process();
    }

    private void process() throws Exception {
        RailroadImages.main();
        bnf = Bnf.getInstance(getReader());
        Csv csv = new Csv();
        csv.setLineCommentCharacter('#');
        ResultSet rs = csv.read(getReader(), null);
        map("grammar", rs, true);
        processHtml("jcr-sql2.html");
    }

    private void processHtml(String fileName) throws Exception {
        String source = "src/tools/org/guinsoo/jcr/";
        String target = "docs/html/";
        byte[] s = BuildBase.readFile(Paths.get(source + "stylesheet.css"));
        BuildBase.writeFile(Paths.get(target + "stylesheet.css"), s);
        Path inFile = Paths.get(source + fileName);
        Path outFile = Paths.get(target + fileName);
        Files.createDirectories(outFile.getParent());
        byte[] bytes = Files.readAllBytes(inFile) ;
        if (fileName.endsWith(".html")) {
            String page = new String(bytes);
            page = PageParser.parse(page, session);
            bytes = page.getBytes();
        }
        Files.write(outFile, bytes);
    }

    private static Reader getReader() {
        return new InputStreamReader(Railroads.class.getResourceAsStream("help.csv"));
    }

    private void map(String key, ResultSet rs, boolean railroads) throws Exception {
        ArrayList<HashMap<String, String>> list;
        list = new ArrayList<>();
        while (rs.next()) {
            HashMap<String, String> map = new HashMap<>();
            ResultSetMetaData meta = rs.getMetaData();
            for (int i = 0; i < meta.getColumnCount(); i++) {
                String k = StringUtils.toLowerEnglish(meta.getColumnLabel(i + 1));
                String value = rs.getString(i + 1);
                value = value.trim();
                map.put(k, PageParser.escapeHtml(value));
            }
            String topic = rs.getString("TOPIC");
            String syntax = rs.getString("SYNTAX").trim();
            if (railroads) {
                BnfRailroad r = new BnfRailroad();
                String railroad = r.getHtml(bnf, syntax);
                map.put("railroad", railroad);
            }
            BnfSyntax visitor = new BnfSyntax();
            String syntaxHtml = visitor.getHtml(bnf, syntax);
            map.put("syntax", syntaxHtml);
            // remove newlines in the regular text
            String text = map.get("text");
            if (text != null) {
                // text is enclosed in <p> .. </p> so this works.
                text = StringUtils.replaceAll(text, "<br /><br />", "</p><p>");
                text = StringUtils.replaceAll(text, "<br />", " ");
                map.put("text", text);
            }

            String link = topic.toLowerCase();
            link = link.replace(' ', '_');
            // link = StringUtils.replaceAll(link, "_", "");
            link = link.replace('@', '_');
            map.put("link", StringUtils.urlEncode(link));
            list.add(map);
        }
        session.put(key, list);
        int div = 3;
        int part = (list.size() + div - 1) / div;
        for (int i = 0, start = 0; i < div; i++, start += part) {
            List<HashMap<String, String>> listThird = list.subList(start,
                    Math.min(start + part, list.size()));
            session.put(key + "-" + i, listThird);
        }
        rs.close();
    }

}
