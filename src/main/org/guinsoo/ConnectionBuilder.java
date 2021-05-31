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

package org.guinsoo;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Enumeration;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static org.guinsoo.util.Utils.readSettingsFromURL;

/**
 * ConnectionBuilder
 *
 * @author cius.ji
 * @since 1.8+
 */
public class ConnectionBuilder {
    /**
     * database url
     */
    private String url;

    /**
     * database trace enable, default is false.
     */
    private boolean traceEnable;

    /**
     * database multiple connections enable, default is false.
     */
    private boolean multipleConnectionsEnable;

    public ConnectionBuilder() {
    }

    public ConnectionBuilder(String url) {
        this.url = url;
    }

    public static ConnectionBuilder getInstance() {
        return new ConnectionBuilder();
    }

    /**
     * establish a connection to the given database URL
     *
     * @return a connection to the URL
     * @throws Exception error occurs or the url is null or not start with `jdbc`
     */
    public Connection build() throws Exception {
        if (url == null || !url.startsWith("jdbc")) {
            throw new Exception("Connection url error, regular format like 'jdbc:guinsoo:mem:'");
        }

        String jdbcUrl;

        switch (parseEngine()) {
            case 1:
            case 2:
                Class.forName("org.guinsoo.Driver");
                jdbcUrl = url;
                break;
            case 3:
                long start = System.currentTimeMillis();
                Method add = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
                add.setAccessible(true);
                URLClassLoader classloader = (URLClassLoader)ClassLoader.getSystemClassLoader();
                /// System.out.println(ConnectionBuilder.getInstance().getClass().getResource(""));
                /// System.out.println(this.getClass().getResource("/"));
                String jarFile = "/Users/admin/.m2/repository/io/github/ciusji/guinsoo/0.2.0-SNAPSHOT/guinsoo-0.2.0-SNAPSHOT.jar";
                JarFile jar = new JarFile(jarFile);
                Enumeration<JarEntry> enumFiles = jar.entries();
                JarEntry entry;
                while (enumFiles.hasMoreElements()) {
                    entry = enumFiles.nextElement();
                    if (entry.getName().indexOf("META-INF/drivers/jdbc/guinsoodb_jdbc.jar") == 0) {
                        System.out.println(entry.getName());
                        InputStream inputStream = jar.getInputStream(entry);
                        Path path = Files.createTempFile("RemoteClassLoader", "jar");
                        path.toFile().deleteOnExit();
                        Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
                        URL classUrl = path.toUri().toURL();
                        add.invoke(classloader, classUrl);
                        String className = "org.guinsoodb.GuinsooDBDriver";
                        Class.forName(className);
                        System.out.println("Load Druation: " + (System.currentTimeMillis() - start));
                    }
                }
                jdbcUrl = "jdbc:guinsoodb:";
                break;
            default:
                throw new Exception("Database engine initialize failed");
        }

        return DriverManager.getConnection(jdbcUrl);
    }

    public String getUrl() {
        return url;
    }

    public ConnectionBuilder setUrl(String url) {
        this.url = url;
        return this;
    }

    private int parseEngine() {
        String key = "STORE";
        String pageStore = "1";
        String mvStore = "2";
        String quickStore = "3";
        Map<String, String> map = readSettingsFromURL(url.toUpperCase());
        if (map.size() == 0 || !map.containsKey(key)) {
            return -1;
        } else {
            String value = map.get(key);
            if (pageStore.equals(value)) {
                return 1;
            } else if (mvStore.equals(value)) {
                return 2;
            } else if (quickStore.equals(value)) {
                return 3;
            } else {
                return -1;
            }
        }
    }
}
