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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * DuckUsage
 *
 * @author cius.ji
 * @blame guinsoo Group
 * @since 1.8+
 */
public class DuckUsage {

    public void loadData() throws ClassNotFoundException, SQLException {
        Class.forName("org.duckdb.DuckDBDriver");
        Connection conn = DriverManager.getConnection("jdbc:duckdb:");
        Statement stmt = conn.createStatement();
        long start = System.currentTimeMillis();

        // String path = "/Users/admin/Desktop/relations5.csv";
        // stmt.execute("create table duck as select * from read_csv_auto('" + path + "')");
        // stmt.execute("select count(distinct aor_id) from duck;");

        String path = "/Users/admin/Desktop/huge.csv";
        // stmt.execute("create table huge as select * from read_csv_auto('" + path + "')");
        stmt.execute("create table huge(num integer);");
        stmt.execute("copy huge from '" + path + "';");
        long start2 = System.currentTimeMillis();
        stmt.execute("select count(distinct num) from huge;");
        System.out.println("Duration2: " + (System.currentTimeMillis() - start2));

        System.out.println("Duration6666: " + (System.currentTimeMillis() - start));
    }

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        DuckUsage usage = new DuckUsage();
        usage.loadData();
    }
}
