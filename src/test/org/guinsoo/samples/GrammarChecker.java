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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * GrammarChecker
 *
 * @author cius.ji
 * @since 1.8+
 */
public class GrammarChecker {

    private static final String url = "jdbc:guinsoo:file:~/tb;STORE=2";
    private static Connection conn = null;

    static {
        try {
            conn = ConnectionBuilder
                    .getInstance()
                    .setUrl(url)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void checkExplainAnalyze() throws SQLException {
        Statement stat = conn.createStatement();
        // `analyze` would execute physical plan (fetch all rows)
        String sql = "explain analyze select * from relations;";
        ResultSet resultSet = stat.executeQuery(sql);
        while (resultSet.next()) {
            System.out.println(resultSet.getString(1));
        }
    }

    public void checkFromTables() throws SQLException {
        Statement stat = conn.createStatement();

    }

    public void checkJoinTables() {

    }

    public void checkWithTables() {

    }

    public void checkSubFrom() {

    }

    public void checkGroupBy() {

    }

    public void checkUniq() {

    }

    public static void main(String[] args) throws SQLException {
        GrammarChecker grammarChecker = new GrammarChecker();
        grammarChecker.checkExplainAnalyze();
    }
}
