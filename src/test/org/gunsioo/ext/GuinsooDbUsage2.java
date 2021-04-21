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

package org.gunsioo.ext;

import java.sql.*;

/**
 * GuinsooDbUsage2
 *
 * @author cius.ji
 * @blame guinsoo Group
 * @since 1.8+
 */
public class GuinsooDbUsage2 {

    public void loadData1() throws ClassNotFoundException, SQLException {
        Class.forName("org.guinsoodb.GuinsooDBDriver");
        Connection conn = DriverManager.getConnection("jdbc:guinsoodb:");
        Statement stmt = conn.createStatement();
        long start = System.currentTimeMillis();

        // String path = "/Users/admin/Desktop/relations5.csv";
        // stmt.execute("create table tt as select * from read_csv_auto('" + path + "')");
        // stmt.execute("select count(distinct aor_id) from tt;");

        String path = "/Users/admin/Desktop/huge.csv";
        // stmt.execute("create table huge as select * from read_csv_auto('" + path + "')");
        stmt.execute("create table huge(num integer);");
        stmt.execute("copy huge from '" + path + "';");
        System.out.println("Duration1: " + (System.currentTimeMillis() - start));
        long start2 = System.currentTimeMillis();
        stmt.execute("select count(distinct num) from huge;");
        System.out.println("Duration2: " + (System.currentTimeMillis() - start2));

        System.out.println("Duration All: " + (System.currentTimeMillis() - start));
    }

    public void loadData2() throws ClassNotFoundException, SQLException {
        Class.forName("org.guinsoodb.GuinsooDBDriver");
        Connection conn = DriverManager.getConnection("jdbc:guinsoodb:");
        Statement stmt = conn.createStatement();
        long start = System.currentTimeMillis();

        String path = "/Users/admin/Desktop/huge.csv";
        stmt.execute("create table huge as select * from read_csv_auto('" + path + "')");
        System.out.println("Duration1: " + (System.currentTimeMillis() - start));
        long start2 = System.currentTimeMillis();
        stmt.execute("select count(distinct num) from huge;");
        System.out.println("Duration2: " + (System.currentTimeMillis() - start2));

        System.out.println("Duration All: " + (System.currentTimeMillis() - start));
    }

    public void caclData() throws ClassNotFoundException, SQLException {
        Class.forName("org.guinsoodb.GuinsooDBDriver");
        Connection conn = DriverManager.getConnection("jdbc:guinsoodb:");
        Statement stmt = conn.createStatement();
        long start = System.currentTimeMillis();

        String path1 = "/Users/admin/Desktop/table_relation_1_c1_2596";
        String path2 = "/Users/admin/Desktop/table_config_2_6852";
        String path3 = "/Users/admin/Desktop/table_config_1_12399";
        stmt.execute("create table table_relation_1_c1_2596 as select * from read_csv_auto('" + path1 + "')");
        stmt.execute("create table table_config_2_6852 as select * from read_csv_auto('" + path2 + "')");
        stmt.execute("create table table_config_1_12399 as select * from read_csv_auto('" + path3 + "')");
        // stmt.execute("copy huge from '" + path + "';");
        ResultSet resultSet = stmt.executeQuery("SELECT\n" +
                "  lt.ENTITY_FIELD,\n" +
                "  lt.DATE_FIELD,(\n" +
                "    CASE\n" +
                "      WHEN (1 = 1) THEN c1\n" +
                "    END\n" +
                "  ) as config_1_12399\n" +
                "FROM\n" +
                "  (\n" +
                "    SELECT\n" +
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
                "      DATE_FIELD\n" +
                "  ) as lt");

//        while (resultSet.next()) {
//            System.out.println(resultSet.getObject("ENTITY_FIELD"));
//        }

        System.out.println("Duration6666: " + (System.currentTimeMillis() - start));
    }

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        GuinsooDbUsage2 usage = new GuinsooDbUsage2();
        // usage.loadData1();
        usage.loadData2();
        // usage.caclData();
    }
}
