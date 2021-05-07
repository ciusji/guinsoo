/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.guinsoo.samples;

import org.guinsoo.ConnectionBuilder;
import org.guinsoo.mvstore.MVMap;
import org.guinsoo.mvstore.MVStore;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.TreeMap;

import org.guinsoo.mvstore.OffHeapStore;
import org.guinsoo.tools.DeleteDbFiles;

/**
 * TreeMapAndBTree
 *
 * @author cius.ji
 * @since 1.8+
 */
public class TreeMapAndBTree {

    // private int limit = 1_000_000;
    private int limit = 3_000_000;
    // private int limit = 5_000_000;
    // private int limit = 30_000_000;

    public void treeMapUsage() {
        long startTime = System.currentTimeMillis();
        TreeMap<Integer, String> rbTree = new TreeMap<>();
        for (int i=0; i<limit; i++) {
            rbTree.put(i, "Hello World-" + i);
        }

        // java.lang.OutOfMemoryError: Java heap space
        System.out.println("Duration: " + (System.currentTimeMillis() - startTime));
    }

    // Duration(1_000_000): 703
    // Duration(3_000_000): 2496
    // Duration(5_000_000): 4206
    public void btreeMapUsage() {
        OffHeapStore offHeapStore = new OffHeapStore();
        MVStore store = new MVStore.Builder()
                .fileStore(offHeapStore)
                .pageSplitSize(8 * 4)
                .keysPerPage(64)
                .open();
        MVMap<Integer, String> bTree = store.openMap("data");

        // MVMap<Integer, String> bTree = MVStore.open(null).openMap("data");

        ArrayList<Integer> lists = new ArrayList<>();
        for (int i = 0; i < limit; i++) {
            lists.add(i);
        }

        long startTime = System.currentTimeMillis();
        // empty put, cost 800ms about each 3_000_000 entries.
        // <int, string> put, cost 3000ms about each 3_000_000 entries.
        lists.parallelStream().forEach(it -> bTree.put(it, "Hello World-" + it));

        // (heap memory) Duration: ~ 51903
        // (off-heap memory) Duration: ~ 14470
        System.out.println("Duration: ~ " + (System.currentTimeMillis() - startTime));
    }

    public void sqlInsert() throws Exception {
        long startTime = System.currentTimeMillis();
        String path = "/Users/admin/Desktop/relations2.csv";
        String name = "relations";

        Class.forName("org.guinsoo.Driver");
        // String url = "jdbc:guinsoo:mem:db;LOCK_MODE=0;UNDO_LOG=0";
        // String url = "jdbc:guinsoo:mem:db;LOCK_MODE=0;UNDO_LOG=0;CACHE_SIZE=4096";
        String url = "jdbc:guinsoo:mem:db;CACHE_SIZE=65536";
        // String url = "jdbc:guinsoo:file:~/test;UNDO_LOG=0;CACHE_SIZE=4096";
        Connection conn = DriverManager.getConnection(url);
        Statement stat = conn.createStatement();
        long startTime2 = System.currentTimeMillis();
        System.out.println("Duration2: ~ " + (startTime2 - startTime));

        // stat.execute("drop table if exists " + name);
        // table: relations
        // TTT 6306 ms
        // TTT 3225 ms (without special data typed key)
        // stat.execute("create table " + name + "(poi_id long primary key, dt varchar, aor_id long) as select * from csvread('" + path + "');");
        stat.execute("create table " + name + " as select * from read_csv('" + path + "');");
        // stat.execute("create table " + name + "(poi_id long, dt varchar, aor_id long) as select * from csvread('" + path + "');");
        long startTime3 = System.currentTimeMillis();
        System.out.println("Duration3: ~ " + (startTime3 - startTime2));
        stat.execute("create index ix_3 on " + name + "(dt, aor_id);");
        long startTime4 = System.currentTimeMillis();
        System.out.println("Duration4: ~ " + (startTime4 - startTime3));

        conn.commit();
        long startTime5 = System.currentTimeMillis();
        System.out.println("Duration5: ~ " + (startTime5 - startTime4));
        stat.close();
        long startTime6 = System.currentTimeMillis();
        System.out.println("Duration6: ~ " + (startTime6 - startTime5));
        conn.close();

        System.out.println("Duration7: ~ " + (System.currentTimeMillis() - startTime6));

    }

    public void callFunction() throws Exception {
        // String path = "/Users/admin/Desktop/relations.csv";
        // String path = "/Users/admin/Desktop/relations2.csv";
        // String path = "/Users/admin/Desktop/relations3.csv";

        // load driver(s)
        // Class.forName("org.guinsoo.Driver");

        // mem:<databaseName>
        // String url = "jdbc:guinsoo:mem:db;UNDO_LOG=0;CACHE_SIZE=4096";
        String url = "jdbc:guinsoo:file:~/tb;STORE=2";

        Connection conn = ConnectionBuilder
                .getInstance()
                .setUrl(url)
                .build();

        Statement stat = conn.createStatement();

        long startTime = System.currentTimeMillis();
        // ??? why cost more than 1 seconds
        // !!! char to row object cost more that 1 seconds.
        stat.execute("select * from relations;");
        stat.execute("select count(distinct poi_id) from relations;");
        long endTime = System.currentTimeMillis();
        System.out.println("Duration666: ~ " + (endTime - startTime));

    }

    public void insertDirect() throws ClassNotFoundException, SQLException {
        Class.forName("org.guinsoo.Driver");
        String url = "jdbc:guinsoo:mem:db;UNDO_LOG=0;CACHE_SIZE=4096";
        Connection conn = DriverManager.getConnection(url);
        Statement stat = conn.createStatement();

        long startTime = System.currentTimeMillis();
        stat.execute("create table test(poi_id long primary key, dt varchar, aor_id long);");
        stat.execute("insert into test values (1, '20210606', 80000);");

        conn.commit();

        stat.close();
        conn.close();
        long endTime = System.currentTimeMillis();
        System.out.println("Duration666: ~ " + (endTime - startTime));
    }

    public void loadFunction() throws Exception {
        DeleteDbFiles.execute("~", "tb", true);
        // String path = "/Users/admin/Desktop/relations.csv";
        String path = "/Users/admin/Desktop/workflow/temp/relations2.csv";
        // String path = "/Users/admin/Desktop/relations4.csv";
        String name = "relations";

        Class.forName("org.guinsoo.Driver");
        // unsupported "MVSTORE && LOG"
        // STORE: 1==pagestore, 2==mvstore, 3==quickstore
        String url = "jdbc:guinsoo:file:~/tb;UNDO_LOG=0;CACHE_SIZE=4096;STORE=2";
        // String url = "jdbc:guinsoo:file:~/test;UNDO_LOG=0;CACHE_SIZE=8192;LOG=1;MV_STORE=FALSE";
        // String url = "jdbc:guinsoo:file:~/test;MV_STORE=FALSE;LOG=0";
        // String url = "jdbc:guinsoo:mem:db;MV_STORE=FALSE;LOG=0";
        // String url = "jdbc:guinsoo:mem:db;UNDO_LOG=0;CACHE_SIZE=65536";
        Connection conn = DriverManager.getConnection(url);
        Statement stat = conn.createStatement();

        long startTime = System.currentTimeMillis();
        // ??? why cost more than 1 seconds
        // !!! char to row object cost more that 1 seconds.
        // stat.execute("call csvload('" + path + "');");
        // create table ... as select ... from => tableAlias
        stat.execute("create table " + name + " as select * from read_csv('" + path + "');");
        System.out.println("Duration600: ~ " + (System.currentTimeMillis() - startTime));

        long startTime2 = System.currentTimeMillis();
        stat.execute("create index idx1 on " + name + "(poi_id);");
        System.out.println("Duration601: ~ " + (System.currentTimeMillis() - startTime2));

        long startTime3 = System.currentTimeMillis();
        stat.execute("select count(distinct poi_id) from relations;");
        System.out.println("Duration602: ~ " + (System.currentTimeMillis() - startTime3));

        conn.commit();

        stat.close();
        conn.close();

        System.out.println("Duration666: ~ " + (System.currentTimeMillis() - startTime));
    }

    public void queryFunction() throws Exception {
        DeleteDbFiles.execute("~", "test", true);

        // Class.forName("org.guinsoo.Driver");
        // unsupported "MVSTORE && LOG"
        // STORE: 1==pagestore, 2==mvstore, 3==quickstore
        // String url = "jdbc:guinsoo:file:~/test;UNDO_LOG=0;LOCK_MODE=0;CACHE_SIZE=65536;STORE=1;";
        String url = "jdbc:guinsoo:mem:;CACHE_SIZE=65536;STORE=3";
        // Connection conn = DriverManager.getConnection(url);
        Connection conn = ConnectionBuilder.getInstance()
                .setUrl(url)
                .build();
        Statement stat = conn.createStatement();

        long startTime = System.currentTimeMillis();
        String path1 = "/Users/admin/Desktop/table_relation_1_c1_2596";
        String patguinsoo = "/Users/admin/Desktop/table_config_2_6852";
        long startTime1 = System.currentTimeMillis();
        // String path3 = "/Users/admin/Desktop/table_config_1_12399";
        stat.execute("create table table_relation_1_c1_2596 as select * from read_csv('" + path1 + "', AUTO_DETECT=TRUE)");
        System.out.println("Duration500: ~ " + (System.currentTimeMillis() - startTime1));
//        stat.execute("create index idx1 on table_relation_1_c1_2596(ENTITY_FIELD);");
//        stat.execute("create index idx2 on table_relation_1_c1_2596(data_field);");
//        stat.execute("create index idx3 on table_relation_1_c1_2596(DATE_FIELD);");
        stat.execute("create table table_config_2_6852 as select * from read_csv('" + patguinsoo + "', AUTO_DETECT=TRUE)");
//        stat.execute("alter table table_config_2_6852 alter column config_2_6852 float");
//        stat.execute("create index idx4 on table_config_2_6852(ENTITY_FIELD);");
//        stat.execute("create index idx5 on table_config_2_6852(config_2_6852);");
//        stat.execute("create index idx6 on table_config_2_6852(DATE_FIELD);");
        // stat.execute("create table table_config_1_12399 as select * from csvread('" + path3 + "')");
        // stat.execute("alter table table_config_1_12399 alter column config_1_12399 float");
        // ??? why cost more than 1 seconds
        // !!! char to row object cost more that 1 seconds.
        // stat.execute("call csvload('" + path + "');");
        // create table ... as select ... from => tableAlias
        long startTime2 = System.currentTimeMillis();

        stat.execute("SELECT\n" +
                "      ENTITY_FIELD,\n" +
                "      DATE_FIELD,\n" +
                "      sum(config_2_6852) as c1\n" +
                "    FROM\n" +
                "      (\n" +
                "        SELECT\n" +
                "          lt.ENTITY_FIELD,\n" +
                "          lt.DATE_FIELD,\n" +
                "          rt.config_2_6852\n" +
                "        FROM\n" +
                "          (\n" +
                "            SELECT\n" +
                "              ENTITY_FIELD,\n" +
                "              data_field,\n" +
                "              DATE_FIELD\n" +
                "            FROM\n" +
                "              table_relation_1_c1_2596\n" +
                "            WHERE\n" +
                "              (\n" +
                "                DATE_FIELD BETWEEN '20210301'\n" +
                "                AND '20210330'\n" +
                "              )\n" +
                "          ) as lt\n" +
                "          LEFT OUTER JOIN (\n" +
                "            SELECT\n" +
                "              lt.ENTITY_FIELD,\n" +
                "              rt.origin_date as DATE_FIELD,\n" +
                "              config_2_6852\n" +
                "            FROM\n" +
                "              (\n" +
                "                SELECT\n" +
                "                  ENTITY_FIELD,\n" +
                "                  DATE_FIELD,\n" +
                "                  config_2_6852\n" +
                "                FROM\n" +
                "                  table_config_2_6852\n" +
                "                WHERE\n" +
                "                  (\n" +
                "                    DATE_FIELD BETWEEN '20210301'\n" +
                "                    AND '20210330'\n" +
                "                  )\n" +
                "              ) as lt\n" +
                "              INNER JOIN (\n" +
                "                SELECT\n" +
                "                  '20210301' as delta_date,\n" +
                "                  '20210301' as origin_date\n" +
                "                UNION ALL\n" +
                "                SELECT\n" +
                "                  '20210323' as delta_date,\n" +
                "                  '20210323' as origin_date\n" +
                "                UNION ALL\n" +
                "                SELECT\n" +
                "                  '20210322' as delta_date,\n" +
                "                  '20210322' as origin_date\n" +
                "                UNION ALL\n" +
                "                SELECT\n" +
                "                  '20210303' as delta_date,\n" +
                "                  '20210303' as origin_date\n" +
                "                UNION ALL\n" +
                "                SELECT\n" +
                "                  '20210325' as delta_date,\n" +
                "                  '20210325' as origin_date\n" +
                "                UNION ALL\n" +
                "                SELECT\n" +
                "                  '20210302' as delta_date,\n" +
                "                  '20210302' as origin_date\n" +
                "                UNION ALL\n" +
                "                SELECT\n" +
                "                  '20210324' as delta_date,\n" +
                "                  '20210324' as origin_date\n" +
                "                UNION ALL\n" +
                "                SELECT\n" +
                "                  '20210305' as delta_date,\n" +
                "                  '20210305' as origin_date\n" +
                "                UNION ALL\n" +
                "                SELECT\n" +
                "                  '20210327' as delta_date,\n" +
                "                  '20210327' as origin_date\n" +
                "                UNION ALL\n" +
                "                SELECT\n" +
                "                  '20210304' as delta_date,\n" +
                "                  '20210304' as origin_date\n" +
                "                UNION ALL\n" +
                "                SELECT\n" +
                "                  '20210326' as delta_date,\n" +
                "                  '20210326' as origin_date\n" +
                "                UNION ALL\n" +
                "                SELECT\n" +
                "                  '20210307' as delta_date,\n" +
                "                  '20210307' as origin_date\n" +
                "                UNION ALL\n" +
                "                SELECT\n" +
                "                  '20210329' as delta_date,\n" +
                "                  '20210329' as origin_date\n" +
                "                UNION ALL\n" +
                "                SELECT\n" +
                "                  '20210306' as delta_date,\n" +
                "                  '20210306' as origin_date\n" +
                "                UNION ALL\n" +
                "                SELECT\n" +
                "                  '20210328' as delta_date,\n" +
                "                  '20210328' as origin_date\n" +
                "                UNION ALL\n" +
                "                SELECT\n" +
                "                  '20210309' as delta_date,\n" +
                "                  '20210309' as origin_date\n" +
                "                UNION ALL\n" +
                "                SELECT\n" +
                "                  '20210308' as delta_date,\n" +
                "                  '20210308' as origin_date\n" +
                "                UNION ALL\n" +
                "                SELECT\n" +
                "                  '20210330' as delta_date,\n" +
                "                  '20210330' as origin_date\n" +
                "                UNION ALL\n" +
                "                SELECT\n" +
                "                  '20210310' as delta_date,\n" +
                "                  '20210310' as origin_date\n" +
                "                UNION ALL\n" +
                "                SELECT\n" +
                "                  '20210312' as delta_date,\n" +
                "                  '20210312' as origin_date\n" +
                "                UNION ALL\n" +
                "                SELECT\n" +
                "                  '20210311' as delta_date,\n" +
                "                  '20210311' as origin_date\n" +
                "                UNION ALL\n" +
                "                SELECT\n" +
                "                  '20210314' as delta_date,\n" +
                "                  '20210314' as origin_date\n" +
                "                UNION ALL\n" +
                "                SELECT\n" +
                "                  '20210313' as delta_date,\n" +
                "                  '20210313' as origin_date\n" +
                "                UNION ALL\n" +
                "                SELECT\n" +
                "                  '20210316' as delta_date,\n" +
                "                  '20210316' as origin_date\n" +
                "                UNION ALL\n" +
                "                SELECT\n" +
                "                  '20210315' as delta_date,\n" +
                "                  '20210315' as origin_date\n" +
                "                UNION ALL\n" +
                "                SELECT\n" +
                "                  '20210318' as delta_date,\n" +
                "                  '20210318' as origin_date\n" +
                "                UNION ALL\n" +
                "                SELECT\n" +
                "                  '20210317' as delta_date,\n" +
                "                  '20210317' as origin_date\n" +
                "                UNION ALL\n" +
                "                SELECT\n" +
                "                  '20210319' as delta_date,\n" +
                "                  '20210319' as origin_date\n" +
                "                UNION ALL\n" +
                "                SELECT\n" +
                "                  '20210321' as delta_date,\n" +
                "                  '20210321' as origin_date\n" +
                "                UNION ALL\n" +
                "                SELECT\n" +
                "                  '20210320' as delta_date,\n" +
                "                  '20210320' as origin_date\n" +
                "              ) as rt ON (lt.DATE_FIELD = rt.delta_date)\n" +
                "          ) as rt ON (\n" +
                "            (lt.data_field = rt.ENTITY_FIELD)\n" +
                "            AND (lt.DATE_FIELD = rt.DATE_FIELD)\n" +
                "          )\n" +
                "      ) as tmp\n" +
                "    GROUP BY\n" +
                "      ENTITY_FIELD,\n" +
                "      DATE_FIELD");

        // with as ...
//        stat.execute("with table_config_2_6852 \n" +
//                "as (\n" +
//                "SELECT lt.ENTITY_FIELD,\n" +
//                "                rt.origin_date AS DATE_FIELD,\n" +
//                "                config_2_6852\n" +
//                "         FROM\n" +
//                "           (SELECT ENTITY_FIELD,\n" +
//                "                   DATE_FIELD,\n" +
//                "                   config_2_6852\n" +
//                "            FROM table_config_2_6852\n" +
//                "            WHERE (DATE_FIELD BETWEEN '20210301' AND '20210330')) AS lt\n" +
//                "         INNER JOIN\n" +
//                "           (SELECT '20210301' AS delta_date,\n" +
//                "                   '20210301' AS origin_date\n" +
//                "            UNION ALL SELECT '20210323' AS delta_date,\n" +
//                "                             '20210323' AS origin_date\n" +
//                "            UNION ALL SELECT '20210322' AS delta_date,\n" +
//                "                             '20210322' AS origin_date\n" +
//                "            UNION ALL SELECT '20210303' AS delta_date,\n" +
//                "                             '20210303' AS origin_date\n" +
//                "            UNION ALL SELECT '20210325' AS delta_date,\n" +
//                "                             '20210325' AS origin_date\n" +
//                "            UNION ALL SELECT '20210302' AS delta_date,\n" +
//                "                             '20210302' AS origin_date\n" +
//                "            UNION ALL SELECT '20210324' AS delta_date,\n" +
//                "                             '20210324' AS origin_date\n" +
//                "            UNION ALL SELECT '20210305' AS delta_date,\n" +
//                "                             '20210305' AS origin_date\n" +
//                "            UNION ALL SELECT '20210327' AS delta_date,\n" +
//                "                             '20210327' AS origin_date\n" +
//                "            UNION ALL SELECT '20210304' AS delta_date,\n" +
//                "                             '20210304' AS origin_date\n" +
//                "            UNION ALL SELECT '20210326' AS delta_date,\n" +
//                "                             '20210326' AS origin_date\n" +
//                "            UNION ALL SELECT '20210307' AS delta_date,\n" +
//                "                             '20210307' AS origin_date\n" +
//                "            UNION ALL SELECT '20210329' AS delta_date,\n" +
//                "                             '20210329' AS origin_date\n" +
//                "            UNION ALL SELECT '20210306' AS delta_date,\n" +
//                "                             '20210306' AS origin_date\n" +
//                "            UNION ALL SELECT '20210328' AS delta_date,\n" +
//                "                             '20210328' AS origin_date\n" +
//                "            UNION ALL SELECT '20210309' AS delta_date,\n" +
//                "                             '20210309' AS origin_date\n" +
//                "            UNION ALL SELECT '20210308' AS delta_date,\n" +
//                "                             '20210308' AS origin_date\n" +
//                "            UNION ALL SELECT '20210330' AS delta_date,\n" +
//                "                             '20210330' AS origin_date\n" +
//                "            UNION ALL SELECT '20210310' AS delta_date,\n" +
//                "                             '20210310' AS origin_date\n" +
//                "            UNION ALL SELECT '20210312' AS delta_date,\n" +
//                "                             '20210312' AS origin_date\n" +
//                "            UNION ALL SELECT '20210311' AS delta_date,\n" +
//                "                             '20210311' AS origin_date\n" +
//                "            UNION ALL SELECT '20210314' AS delta_date,\n" +
//                "                             '20210314' AS origin_date\n" +
//                "            UNION ALL SELECT '20210313' AS delta_date,\n" +
//                "                             '20210313' AS origin_date\n" +
//                "            UNION ALL SELECT '20210316' AS delta_date,\n" +
//                "                             '20210316' AS origin_date\n" +
//                "            UNION ALL SELECT '20210315' AS delta_date,\n" +
//                "                             '20210315' AS origin_date\n" +
//                "            UNION ALL SELECT '20210318' AS delta_date,\n" +
//                "                             '20210318' AS origin_date\n" +
//                "            UNION ALL SELECT '20210317' AS delta_date,\n" +
//                "                             '20210317' AS origin_date\n" +
//                "            UNION ALL SELECT '20210319' AS delta_date,\n" +
//                "                             '20210319' AS origin_date\n" +
//                "            UNION ALL SELECT '20210321' AS delta_date,\n" +
//                "                             '20210321' AS origin_date\n" +
//                "            UNION ALL SELECT '20210320' AS delta_date,\n" +
//                "                             '20210320' AS origin_date) AS rt ON (lt.DATE_FIELD = rt.delta_date)\n" +
//                ")\n" +
//                "\n" +
//                "\n" +
//                "SELECT lt.ENTITY_FIELD,\n" +
//                "       lt.DATE_FIELD,\n" +
//                "       (CASE\n" +
//                "            WHEN (1=1) THEN c1\n" +
//                "        END) AS config_1_12399\n" +
//                "FROM\n" +
//                "  (SELECT ENTITY_FIELD,\n" +
//                "          DATE_FIELD,\n" +
//                "          sum(config_2_6852) AS c1\n" +
//                "   FROM\n" +
//                "     (SELECT lt.ENTITY_FIELD,\n" +
//                "             lt.DATE_FIELD,\n" +
//                "             rt.config_2_6852\n" +
//                "      FROM\n" +
//                "        (SELECT ENTITY_FIELD,\n" +
//                "                data_field,\n" +
//                "                DATE_FIELD\n" +
//                "         FROM table_relation_1_c1_2596\n" +
//                "         WHERE (DATE_FIELD BETWEEN '20210301' AND '20210330')) AS lt\n" +
//                "      LEFT OUTER JOIN\n" +
//                "        table_config_2_6852 AS rt ON ((lt.data_field = rt.ENTITY_FIELD)\n" +
//                "                                                                                                            AND (lt.DATE_FIELD = rt.DATE_FIELD))) AS tmp\n" +
//                "   GROUP BY ENTITY_FIELD,\n" +
//                "            DATE_FIELD) AS lt");

        System.out.println("Duration600: ~ " + (System.currentTimeMillis() - startTime2));

        // conn.commit();

        stat.close();
        conn.close();

        System.out.println("Duration666: ~ " + (System.currentTimeMillis() - startTime));
    }

    public static void main(String[] args) throws Exception {
        TreeMapAndBTree tab = new TreeMapAndBTree();
        // tab.btreeMapUsage();
        // tab.sqlInsert();
        // tab.sqlInsertByHikari();
        // tab.btreeMapUsage();
        tab.callFunction();
        // tab.insertDirect();
        // tab.loadFunction();
        // tab.queryFunction();

    }
}
