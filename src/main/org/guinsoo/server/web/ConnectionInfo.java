/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.server.web;

import org.guinsoo.util.StringUtils;

/**
 * The connection info object is a wrapper for database connection information
 * such as the database URL, user name and password.
 * This class is used by the Guinsoo Console.
 */
public class ConnectionInfo implements Comparable<ConnectionInfo> {
    /**
     * The driver class name.
     */
    public String driver;

    /**
     * The database URL.
     */
    public String url;

    /**
     * The user name.
     */
    public String user;

    /**
     * The connection display name.
     */
    String name;

    /**
     * The last time this connection was used.
     */
    int lastAccess;

    ConnectionInfo() {
        // nothing to do
    }

    public ConnectionInfo(String data) {
        String[] array = StringUtils.arraySplit(data, '|', false);
        name = get(array, 0);
        driver = get(array, 1);
        url = get(array, 2);
        user = get(array, 3);
    }

    private static String get(String[] array, int i) {
        return array != null && array.length > i ? array[i] : "";
    }

    String getString() {
        return StringUtils.arrayCombine(new String[] { name, driver, url, user }, '|');
    }

    @Override
    public int compareTo(ConnectionInfo o) {
        return Integer.compare(o.lastAccess, lastAccess);
    }

}
