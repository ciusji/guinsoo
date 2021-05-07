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

package org.guinsoo.ext;

import java.sql.*;

/**
 * GuinsooDBUsage
 *
 * @author cius.ji
 * @since 1.8+
 */
public class GuinsooDBUsage {

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        Class.forName("org.guinsoodb.GuinsooDBDriver");
        Connection conn = DriverManager.getConnection("jdbc:guinsoodb:");
        Statement stmt = conn.createStatement();
        stmt.execute("CREATE TABLE test (a INTEGER, b VARCHAR)");

        PreparedStatement p_stmt = conn.prepareStatement("INSERT INTO test VALUES (?, ?);");

        p_stmt.setInt(1, 42);
        p_stmt.setString(2, "Hello");
        p_stmt.execute();

        p_stmt.setInt(1, 43);
        p_stmt.setString(2, "World");
        p_stmt.execute();

        p_stmt.close();

        ResultSet rs = stmt.executeQuery("SELECT * FROM test");
        ResultSetMetaData md = rs.getMetaData();
        int row = 1;
        while (rs.next()) {
            for (int col = 1; col <= md.getColumnCount(); col++) {
                System.out.println(md.getColumnName(col) + "[" + row + "]=" + rs.getString(col) + " ("
                        + md.getColumnTypeName(col) + ")");
            }
            row++;
        }

        rs.close();
        stmt.close();
        conn.close();
    }
}
