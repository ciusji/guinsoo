/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.test.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import org.guinsoo.engine.Constants;
import org.guinsoo.store.fs.FileUtils;
import org.guinsoo.test.TestBase;
import org.guinsoo.test.TestDb;

/**
 * Tests if disk space is reused after deleting many rows.
 */
public class TestSpaceReuse extends TestDb {

    /**
     * Run just this test.
     *
     * @param a ignored
     */
    public static void main(String... a) throws Exception {
        TestBase.createCaller().init().testFromMain();
    }

    @Override
    public boolean isEnabled() {
        if (config.memory) {
            return false;
        }
        return true;
    }

    @Override
    public void test() throws SQLException {
        deleteDb("spaceReuse");
        long max = 0, now = 0, min = Long.MAX_VALUE;
        for (int i = 0; i < 20; i++) {
            Connection conn = getConnection("spaceReuse");
            Statement stat = conn.createStatement();
            stat.execute("set retention_time 0");
            stat.execute("set write_delay 0"); // disable auto-commit so that free-unused runs on commit
            stat.execute("create table if not exists t(i int)");
            stat.execute("insert into t select x from system_range(1, 500)");
            conn.close();
            conn = getConnection("spaceReuse");
            conn.createStatement().execute("delete from t");
            conn.close();
            String fileName = getBaseDir() + "/spaceReuse";
            if (config.mvStore) {
                fileName += Constants.SUFFIX_MV_FILE;
            } else {
                fileName += Constants.SUFFIX_PAGE_FILE;
            }
            now = FileUtils.size(fileName);
            assertTrue(now > 0);
            if (i < 10) {
                max = Math.max(max, now);
            } else {
                min = Math.min(min, now);
            }
        }
        assertTrue("min: " + min + " max: " + max, min <= max);
        deleteDb("spaceReuse");
    }

}
