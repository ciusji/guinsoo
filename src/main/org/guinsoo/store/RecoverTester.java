/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.store;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.HashSet;

import org.guinsoo.engine.ConnectionInfo;
import org.guinsoo.engine.Constants;
import org.guinsoo.engine.Database;
import org.guinsoo.engine.SessionLocal;
import org.guinsoo.message.DbException;
import org.guinsoo.store.fs.FileUtils;
import org.guinsoo.store.fs.Recorder;
import org.guinsoo.tools.Recover;
import org.guinsoo.api.ErrorCode;
import org.guinsoo.store.fs.rec.FilePathRec;
import org.guinsoo.util.IOUtils;
import org.guinsoo.util.StringUtils;
import org.guinsoo.util.Utils;

/**
 * A tool that simulates a crash while writing to the database, and then
 * verifies the database doesn't get corrupt.
 */
public class RecoverTester implements Recorder {

    private static final RecoverTester instance = new RecoverTester();

    private String testDatabase = "memFS:reopen";
    private int writeCount = Utils.getProperty("guinsoo.recoverTestOffset", 0);
    private int testEvery = Utils.getProperty("guinsoo.recoverTest", 64);
    private final long maxFileSize = Utils.getProperty(
            "guinsoo.recoverTestMaxFileSize", Integer.MAX_VALUE) * 1024L * 1024;
    private int verifyCount;
    private final HashSet<String> knownErrors = new HashSet<>();
    private volatile boolean testing;

    /**
     * Initialize the recover test.
     *
     * @param recoverTest the value of the recover test parameter
     */
    public static synchronized void init(String recoverTest) {
        if (StringUtils.isNumber(recoverTest)) {
            instance.setTestEvery(Integer.parseInt(recoverTest));
        }
        FilePathRec.setRecorder(instance);
    }

    @Override
    public void log(int op, String fileName, byte[] data, long x) {
        if (op != Recorder.WRITE && op != Recorder.TRUNCATE) {
            return;
        }
        if (!fileName.endsWith(Constants.SUFFIX_PAGE_FILE) &&
                !fileName.endsWith(Constants.SUFFIX_MV_FILE)) {
            return;
        }
        writeCount++;
        if ((writeCount % testEvery) != 0) {
            return;
        }
        if (FileUtils.size(fileName) > maxFileSize) {
            // System.out.println(fileName + " " + IOUtils.length(fileName));
            return;
        }
        if (testing) {
            // avoid deadlocks
            return;
        }
        testing = true;
        PrintWriter out = null;
        try {
            out = new PrintWriter(
                    new OutputStreamWriter(
                    FileUtils.newOutputStream(fileName + ".log", true)));
            testDatabase(fileName, out);
        } catch (IOException e) {
            throw DbException.convertIOException(e, null);
        } finally {
            IOUtils.closeSilently(out);
            testing = false;
        }
    }

    private synchronized void testDatabase(String fileName, PrintWriter out) {
        out.println("+ write #" + writeCount + " verify #" + verifyCount);
        try {
            IOUtils.copyFiles(fileName, testDatabase + Constants.SUFFIX_PAGE_FILE);
            String mvFileName = fileName.substring(0, fileName.length() -
                    Constants.SUFFIX_PAGE_FILE.length()) +
                    Constants.SUFFIX_MV_FILE;
            if (FileUtils.exists(mvFileName)) {
                IOUtils.copyFiles(mvFileName, testDatabase + Constants.SUFFIX_MV_FILE);
            }
            verifyCount++;
            // avoid using the Engine class to avoid deadlocks
            ConnectionInfo ci = new ConnectionInfo("jdbc:guinsoo:" + testDatabase +
                    ";FILE_LOCK=NO;TRACE_LEVEL_FILE=0", null, "", "");
            Database database = new Database(ci, null);
            // close the database
            SessionLocal sysSession = database.getSystemSession();
            sysSession.prepare("script to '" + testDatabase + ".sql'").query(0);
            sysSession.prepare("shutdown immediately").update();
            database.removeSession(null);
            // everything OK - return
            return;
        } catch (DbException e) {
            SQLException e2 = DbException.toSQLException(e);
            int errorCode = e2.getErrorCode();
            if (errorCode == ErrorCode.WRONG_USER_OR_PASSWORD) {
                return;
            } else if (errorCode == ErrorCode.FILE_ENCRYPTION_ERROR_1) {
                return;
            }
            e.printStackTrace(System.out);
        } catch (Exception e) {
            // failed
            int errorCode = 0;
            if (e instanceof SQLException) {
                errorCode = ((SQLException) e).getErrorCode();
            }
            if (errorCode == ErrorCode.WRONG_USER_OR_PASSWORD) {
                return;
            } else if (errorCode == ErrorCode.FILE_ENCRYPTION_ERROR_1) {
                return;
            }
            e.printStackTrace(System.out);
        }
        out.println("begin ------------------------------ " + writeCount);
        try {
            Recover.execute(fileName.substring(0, fileName.lastIndexOf('/')), null);
        } catch (SQLException e) {
            // ignore
        }
        testDatabase += "X";
        try {
            IOUtils.copyFiles(fileName, testDatabase + Constants.SUFFIX_PAGE_FILE);
            // avoid using the Engine class to avoid deadlocks
            ConnectionInfo ci = new ConnectionInfo("jdbc:guinsoo:" +
                        testDatabase + ";FILE_LOCK=NO", null, null, null);
            Database database = new Database(ci, null);
            // close the database
            database.removeSession(null);
        } catch (Exception e) {
            int errorCode = 0;
            if (e instanceof DbException) {
                e = ((DbException) e).getSQLException();
                errorCode = ((SQLException) e).getErrorCode();
            }
            if (errorCode == ErrorCode.WRONG_USER_OR_PASSWORD) {
                return;
            } else if (errorCode == ErrorCode.FILE_ENCRYPTION_ERROR_1) {
                return;
            }
            StringBuilder buff = new StringBuilder();
            StackTraceElement[] list = e.getStackTrace();
            for (int i = 0; i < 10 && i < list.length; i++) {
                buff.append(list[i].toString()).append('\n');
            }
            String s = buff.toString();
            if (!knownErrors.contains(s)) {
                out.println(writeCount + " code: " + errorCode + " " + e.toString());
                e.printStackTrace(System.out);
                knownErrors.add(s);
            } else {
                out.println(writeCount + " code: " + errorCode);
            }
        }
    }

    public void setTestEvery(int testEvery) {
        this.testEvery = testEvery;
    }

}