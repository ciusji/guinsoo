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

package org.gunsioo.samples;

import org.gunsioo.mvstore.MVMap;
import org.gunsioo.mvstore.MVStore;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.TreeMap;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.gunsioo.mvstore.OffHeapStore;
import org.gunsioo.tools.DeleteDbFiles;

/**
 * TreeMapAndBTree
 *
 * @author cius.ji
 * @blame guinsoo Group
 * @since 1.8+
 */
public class TreeMapAndBTree {

    // private int limit = 1_000_000;
    // private int limit = 3_000_000;
    private int limit = 5_000_000;
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
        long startTime = System.currentTimeMillis();
        OffHeapStore offHeapStore = new OffHeapStore();
        MVStore store = new MVStore.Builder()
                .fileStore(offHeapStore)
                .open();
        MVMap<Integer, String> bTree = store.openMap("data");

        // MVMap<Integer, String> bTree = MVStore.open(null).openMap("data");

        for (int i=0; i<limit; i++) {
            bTree.put(i, "Hello World-" + i);
        }

        // (heap memory) Duration: ~ 51903
        // (off-heap memory) Duration: ~ 14470
        System.out.println("Duration: ~ " + (System.currentTimeMillis() - startTime));
    }

    public void sqlInsert() throws Exception {
        long startTime = System.currentTimeMillis();
        String path = "/Users/admin/Desktop/relations2.csv";
        String name = "relations";

        Class.forName("org.gunsioo.Driver");
        // String url = "jdbc:gunsioo:mem:db;LOCK_MODE=0;UNDO_LOG=0";
        // String url = "jdbc:gunsioo:mem:db;LOCK_MODE=0;UNDO_LOG=0;CACHE_SIZE=4096";
        String url = "jdbc:gunsioo:mem:db;LOG=0;UNDO_LOG=0;CACHE_SIZE=65536";
        // String url = "jdbc:gunsioo:file:~/test;UNDO_LOG=0;CACHE_SIZE=4096";
        Connection conn = DriverManager.getConnection(url);
        Statement stat = conn.createStatement();
        long startTime2 = System.currentTimeMillis();
        System.out.println("Duration2: ~ " + (startTime2 - startTime));

        // stat.execute("drop table if exists " + name);
        // table: relations
        // TTT 6306 ms
        // TTT 3225 ms (without special data typed key)
        // stat.execute("create table " + name + "(poi_id long primary key, dt varchar, aor_id long) as select * from csvread('" + path + "');");
        stat.execute("create table " + name + " as select * from csvread('" + path + "');");
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

    public void sqlInsertByHikari() throws Exception {
        long startTime = System.currentTimeMillis();
        String path = "/Users/admin/Desktop/relations2.csv";
        String name = "relations";

        HikariConfig config = new HikariConfig();
        config.setDriverClassName("org.gunsioo.Driver");
        config.setJdbcUrl("jdbc:gunsioo:mem:db;UNDO_LOG=0;CACHE_SIZE=4096");
        HikariDataSource dataSource = new HikariDataSource(config);

//        Class.forName("org.gunsioo.Driver");
//        // String url = "jdbc:gunsioo:mem:db;LOCK_MODE=0;UNDO_LOG=0";
//        // String url = "jdbc:gunsioo:mem:db;LOCK_MODE=0;UNDO_LOG=0;CACHE_SIZE=4096";
//        String url = "jdbc:gunsioo:mem:db;UNDO_LOG=0;CACHE_SIZE=4096";
//        /// String url = "jdbc:gunsioo:file:~/test;UNDO_LOG=0;CACHE_SIZE=4096";
//        Connection conn = DriverManager.getConnection(url);

        Connection conn = dataSource.getConnection();
        Statement stat = conn.createStatement();
        long startTime2 = System.currentTimeMillis();
        System.out.println("Duration2: ~ " + (startTime2 - startTime));

        // stat.execute("drop table if exists " + name);
        // table: relations
        // TTT 6306 ms
        stat.execute("create table " + name + "(poi_id long primary key, dt varchar, aor_id long) as select * from csvread('" + path + "');");
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
        String path = "/Users/admin/Desktop/relations2.csv";
        // String path = "/Users/admin/Desktop/relations3.csv";

        Class.forName("org.gunsioo.Driver");
        String url = "jdbc:gunsioo:mem:db;UNDO_LOG=0;CACHE_SIZE=4096";
        Connection conn = DriverManager.getConnection(url);
        Statement stat = conn.createStatement();

        long startTime = System.currentTimeMillis();
        // ??? why cost more than 1 seconds
        // !!! char to row object cost more that 1 seconds.
        stat.execute("call csvread('" + path + "');");
        long endTime = System.currentTimeMillis();
        System.out.println("Duration666: ~ " + (endTime - startTime));
    }

    public void insertDirect() throws ClassNotFoundException, SQLException {
        Class.forName("org.gunsioo.Driver");
        String url = "jdbc:gunsioo:mem:db;UNDO_LOG=0;CACHE_SIZE=4096";
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
        String path = "/Users/admin/Desktop/relations2.csv";
        // String path = "/Users/admin/Desktop/relations4.csv";
        String name = "relations";

        Class.forName("org.gunsioo.Driver");
        // unsupported "MVSTORE && LOG"
        // STORE: 1==pagestore, 2==mvstore, 3==quickstore
        String url = "jdbc:gunsioo:file:~/tb;UNDO_LOG=0;CACHE_SIZE=4096;STORE=1";
        // String url = "jdbc:gunsioo:file:~/test;UNDO_LOG=0;CACHE_SIZE=8192;LOG=1;MV_STORE=FALSE";
        // String url = "jdbc:gunsioo:file:~/test;MV_STORE=FALSE;LOG=0";
        // String url = "jdbc:gunsioo:mem:db;MV_STORE=FALSE;LOG=0";
        // String url = "jdbc:gunsioo:mem:db;UNDO_LOG=0;CACHE_SIZE=65536";
        Connection conn = DriverManager.getConnection(url);
        Statement stat = conn.createStatement();

        long startTime = System.currentTimeMillis();
        // ??? why cost more than 1 seconds
        // !!! char to row object cost more that 1 seconds.
        // stat.execute("call csvload('" + path + "');");
        // create table ... as select ... from => tableAlias
        stat.execute("create table " + name + " as select * from csvread('" + path + "');");
        System.out.println("Duration600: ~ " + (System.currentTimeMillis() - startTime));

        long startTime2 = System.currentTimeMillis();
        stat.execute("create index idx1 on " + name + "(poi_id);");
        System.out.println("Duration601: ~ " + (System.currentTimeMillis() - startTime2));

        long startTime3 = System.currentTimeMillis();
        stat.execute("select count(distinct poi_id) from relations;");
        System.out.println("Duration602: ~ " + (System.currentTimeMillis() - startTime3));

        conn.commit();

        stat.close();
        conn.close();;

        System.out.println("Duration666: ~ " + (System.currentTimeMillis() - startTime));
    }

    public static void main(String[] args) throws Exception {
        TreeMapAndBTree tab = new TreeMapAndBTree();
        // tab.btreeMapUsage();
        // tab.sqlInsert();
        // tab.sqlInsertByHikari();
        // tab.btreeMapUsage();
        // tab.callFunction();
        // tab.insertDirect();
        tab.loadFunction();

    }
}
