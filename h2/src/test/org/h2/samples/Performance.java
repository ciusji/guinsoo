/*
 * Copyright 2004-2021 H2 Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://h2database.com/html/license.html).
 * Initial Developer: H2 Group
 */
package org.h2.samples;

import java.sql.*;

/**
 * Performance testing.
 */
public class Performance {

    /**
     * This method is called when executing this sample application from the
     * command line.
     *
     * @param args the command line parameters
     */
    public static void main(String... args) throws Exception {
        Class.forName("org.h2.Driver");
        // DeleteDbFiles.execute("~", "test", true);

        String url = "jdbc:h2:file:~/test";
        // String url = "jdbc:h2:mem:db";

        // For fast data import
        // Or use CSV import. Please not that create table(...) ... as select ... is faster that
        // create table(...); insert into ... select ...
        long startTime = System.currentTimeMillis();
        initialInsert_1(url);
        initialInsert_2(url);
        initialInsert_3(url);
        initialInsert_4(url);

        // (file mode) Duration: ~ 20115
        // (memory mode) Duration: ~ 5444
        System.out.println("Duration: ~ " + (System.currentTimeMillis() - startTime));

    }

    private static void initialInsert_1(String url) throws SQLException {
        String path = "/Users/admin/Desktop/submit_orders.csv";
        String name = "submit_orders";
        Connection conn = DriverManager.getConnection(url);
        Statement stat = conn.createStatement();
        stat.execute("drop table if exists " + name);
        // table: submit_orders
        stat.execute("create table " + name + "(poi_id long primary key, dt varchar, sub_ord_num long) as select * from csvread('" + path + "');");
        stat.execute("create index ix_1 on " + name + "(dt, sub_ord_num);");
        conn.commit();
        stat.close();
        conn.close();
    }

    private static void initialInsert_2(String url) throws SQLException {
        String path = "/Users/admin/Desktop/push_orders.csv";
        String name = "push_orders";
        Connection conn = DriverManager.getConnection(url);
        Statement stat = conn.createStatement();
        stat.execute("drop table if exists " + name);
        // table: push_orders
        stat.execute("create table " + name + "(poi_id long primary key, dt varchar, push_ord_num long) as select * from csvread('" + path + "');");
        stat.execute("create index ix_2 on " + name + "(dt, push_ord_num);");
        conn.commit();
        stat.close();
        conn.close();
    }

    private static void initialInsert_3(String url) throws SQLException {
        String path = "/Users/admin/Desktop/relations.csv";
        String name = "relations";
        Connection conn = DriverManager.getConnection(url);
        Statement stat = conn.createStatement();
        stat.execute("drop table if exists " + name);
        // table: relations
        stat.execute("create table " + name + "(poi_id long primary key, dt varchar, aor_id long) as select * from csvread('" + path + "');");
        stat.execute("create index ix_3 on " + name + "(dt, aor_id);");
        conn.commit();
        stat.close();
        conn.close();
    }

    private static void initialInsert_4(String url) throws SQLException {
        String path = "/Users/admin/Desktop/submit_cash.csv";
        String name = "submit_cash";
        Connection conn = DriverManager.getConnection(url);
        Statement stat = conn.createStatement();
        stat.execute("drop table if exists " + name);
        // table: submit_cash
        stat.execute("create table " + name + "(poi_id long primary key, dt varchar, poi_ord_amt double) as select * from csvread('" + path + "');");
        stat.execute("create index ix_4 on " + name + "(dt, poi_ord_amt);");
        conn.commit();
        stat.close();
        conn.close();
    }


}
