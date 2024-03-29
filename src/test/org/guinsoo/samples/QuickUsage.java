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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * QuickUsage
 *
 * @author cius.ji
 * @since 1.8+
 */
public class QuickUsage {

    public static void main(String[] args) throws Exception {

        String url = "jdbc:guinsoo:file:/Users/admin/Git/Public/spotrix/spotrix;MODE=PostgreSQL";

        Connection conn = DriverManager.getConnection(url, "root", "123456");

        Statement stmt = conn.createStatement();

        stmt.execute("select 90 + 100;");

        System.out.println("It's OK!");

    }
}
