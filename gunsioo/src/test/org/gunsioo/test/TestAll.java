/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://h2database.com/html/license.html).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.test;

import java.lang.management.ManagementFactory;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import org.gunsioo.Driver;
import org.gunsioo.engine.Constants;
import org.gunsioo.store.fs.FileUtils;
import org.gunsioo.store.fs.rec.FilePathRec;
import org.gunsioo.test.auth.TestAuthentication;
import org.gunsioo.test.bench.TestPerformance;
import org.gunsioo.test.db.TestAlter;
import org.gunsioo.test.db.TestAlterSchemaRename;
import org.gunsioo.test.db.TestAlterTableNotFound;
import org.gunsioo.test.db.TestAnalyzeTableTx;
import org.gunsioo.test.db.TestAutoRecompile;
import org.gunsioo.test.db.TestBackup;
import org.gunsioo.test.db.TestBigDb;
import org.gunsioo.test.db.TestBigResult;
import org.gunsioo.test.db.TestCases;
import org.gunsioo.test.db.TestCheckpoint;
import org.gunsioo.test.db.TestCluster;
import org.gunsioo.test.db.TestCompatibility;
import org.gunsioo.test.db.TestCompatibilityOracle;
import org.gunsioo.test.db.TestCompatibilitySQLServer;
import org.gunsioo.test.db.TestCsv;
import org.gunsioo.test.db.TestDateStorage;
import org.gunsioo.test.db.TestDeadlock;
import org.gunsioo.test.db.TestDuplicateKeyUpdate;
import org.gunsioo.test.db.TestEncryptedDb;
import org.gunsioo.test.db.TestExclusive;
import org.gunsioo.test.db.TestFullText;
import org.gunsioo.test.db.TestFunctionOverload;
import org.gunsioo.test.db.TestFunctions;
import org.gunsioo.test.db.TestGeneralCommonTableQueries;
import org.gunsioo.test.db.TestIgnoreCatalogs;
import org.gunsioo.test.db.TestIndex;
import org.gunsioo.test.db.TestIndexHints;
import org.gunsioo.test.db.TestLargeBlob;
import org.gunsioo.test.db.TestLinkedTable;
import org.gunsioo.test.db.TestListener;
import org.gunsioo.test.db.TestLob;
import org.gunsioo.test.db.TestMemoryUsage;
import org.gunsioo.test.db.TestMergeUsing;
import org.gunsioo.test.db.TestMultiConn;
import org.gunsioo.test.db.TestMultiDimension;
import org.gunsioo.test.db.TestMultiThread;
import org.gunsioo.test.db.TestMultiThreadedKernel;
import org.gunsioo.test.db.TestOpenClose;
import org.gunsioo.test.db.TestOptimizations;
import org.gunsioo.test.db.TestOptimizerHints;
import org.gunsioo.test.db.TestOutOfMemory;
import org.gunsioo.test.db.TestPersistentCommonTableExpressions;
import org.gunsioo.test.db.TestPowerOff;
import org.gunsioo.test.db.TestQueryCache;
import org.gunsioo.test.db.TestReadOnly;
import org.gunsioo.test.db.TestRecursiveQueries;
import org.gunsioo.test.db.TestRights;
import org.gunsioo.test.db.TestRunscript;
import org.gunsioo.test.db.TestSQLInjection;
import org.gunsioo.test.db.TestSelectCountNonNullColumn;
import org.gunsioo.test.db.TestSelectTableNotFound;
import org.gunsioo.test.db.TestSequence;
import org.gunsioo.test.db.TestSessionsLocks;
import org.gunsioo.test.db.TestSetCollation;
import org.gunsioo.test.db.TestSpaceReuse;
import org.gunsioo.test.db.TestSpatial;
import org.gunsioo.test.db.TestSpeed;
import org.gunsioo.test.db.TestSubqueryPerformanceOnLazyExecutionMode;
import org.gunsioo.test.db.TestSynonymForTable;
import org.gunsioo.test.db.TestTableEngines;
import org.gunsioo.test.db.TestTempTables;
import org.gunsioo.test.db.TestTransaction;
import org.gunsioo.test.db.TestTriggersConstraints;
import org.gunsioo.test.db.TestTwoPhaseCommit;
import org.gunsioo.test.db.TestView;
import org.gunsioo.test.db.TestViewAlterTable;
import org.gunsioo.test.db.TestViewDropView;
import org.gunsioo.test.jdbc.TestBatchUpdates;
import org.gunsioo.test.jdbc.TestCallableStatement;
import org.gunsioo.test.jdbc.TestCancel;
import org.gunsioo.test.jdbc.TestConcurrentConnectionUsage;
import org.gunsioo.test.jdbc.TestConnection;
import org.gunsioo.test.jdbc.TestDatabaseEventListener;
import org.gunsioo.test.jdbc.TestDriver;
import org.gunsioo.test.jdbc.TestGetGeneratedKeys;
import org.gunsioo.test.jdbc.TestJavaObjectSerializer;
import org.gunsioo.test.jdbc.TestLimitUpdates;
import org.gunsioo.test.jdbc.TestLobApi;
import org.gunsioo.test.jdbc.TestManyJdbcObjects;
import org.gunsioo.test.jdbc.TestMetaData;
import org.gunsioo.test.jdbc.TestNativeSQL;
import org.gunsioo.test.jdbc.TestPreparedStatement;
import org.gunsioo.test.jdbc.TestResultSet;
import org.gunsioo.test.jdbc.TestSQLXML;
import org.gunsioo.test.jdbc.TestStatement;
import org.gunsioo.test.jdbc.TestTransactionIsolation;
import org.gunsioo.test.jdbc.TestUpdatableResultSet;
import org.gunsioo.test.jdbc.TestUrlJavaObjectSerializer;
import org.gunsioo.test.jdbc.TestZloty;
import org.gunsioo.test.jdbcx.TestConnectionPool;
import org.gunsioo.test.jdbcx.TestDataSource;
import org.gunsioo.test.jdbcx.TestXA;
import org.gunsioo.test.jdbcx.TestXASimple;
import org.gunsioo.test.mvcc.TestMvcc1;
import org.gunsioo.test.mvcc.TestMvcc2;
import org.gunsioo.test.mvcc.TestMvcc3;
import org.gunsioo.test.mvcc.TestMvcc4;
import org.gunsioo.test.mvcc.TestMvccMultiThreaded;
import org.gunsioo.test.mvcc.TestMvccMultiThreaded2;
import org.gunsioo.test.poweroff.TestReorderWrites;
import org.gunsioo.test.recover.RecoverLobTest;
import org.gunsioo.test.rowlock.TestRowLocks;
import org.gunsioo.test.scripts.TestScript;
import org.gunsioo.test.server.TestAutoServer;
import org.gunsioo.test.server.TestInit;
import org.gunsioo.test.server.TestNestedLoop;
import org.gunsioo.test.server.TestWeb;
import org.gunsioo.test.store.TestCacheConcurrentLIRS;
import org.gunsioo.test.store.TestCacheLIRS;
import org.gunsioo.test.store.TestCacheLongKeyLIRS;
import org.gunsioo.test.store.TestDataUtils;
import org.gunsioo.test.store.TestDefrag;
import org.gunsioo.test.store.TestFreeSpace;
import org.gunsioo.test.store.TestKillProcessWhileWriting;
import org.gunsioo.test.store.TestMVRTree;
import org.gunsioo.test.store.TestMVStore;
import org.gunsioo.test.store.TestMVStoreBenchmark;
import org.gunsioo.test.store.TestMVStoreConcurrent;
import org.gunsioo.test.store.TestMVStoreStopCompact;
import org.gunsioo.test.store.TestMVStoreTool;
import org.gunsioo.test.store.TestMVTableEngine;
import org.gunsioo.test.store.TestObjectDataType;
import org.gunsioo.test.store.TestRandomMapOps;
import org.gunsioo.test.store.TestSpinLock;
import org.gunsioo.test.store.TestStreamStore;
import org.gunsioo.test.store.TestTransactionStore;
import org.gunsioo.test.synth.TestBtreeIndex;
import org.gunsioo.test.synth.TestConcurrentUpdate;
import org.gunsioo.test.synth.TestCrashAPI;
import org.gunsioo.test.synth.TestDiskFull;
import org.gunsioo.test.synth.TestFuzzOptimizations;
import org.gunsioo.test.synth.TestHaltApp;
import org.gunsioo.test.synth.TestJoin;
import org.gunsioo.test.synth.TestKill;
import org.gunsioo.test.synth.TestKillRestart;
import org.gunsioo.test.synth.TestKillRestartMulti;
import org.gunsioo.test.synth.TestLimit;
import org.gunsioo.test.synth.TestMultiThreaded;
import org.gunsioo.test.synth.TestNestedJoins;
import org.gunsioo.test.synth.TestOuterJoins;
import org.gunsioo.test.synth.TestRandomCompare;
import org.gunsioo.test.synth.TestRandomSQL;
import org.gunsioo.test.synth.TestTimer;
import org.gunsioo.test.synth.sql.TestSynth;
import org.gunsioo.test.synth.thread.TestMulti;
import org.gunsioo.test.unit.TestAnsCompression;
import org.gunsioo.test.unit.TestAutoReconnect;
import org.gunsioo.test.unit.TestBinaryArithmeticStream;
import org.gunsioo.test.unit.TestBinaryOperation;
import org.gunsioo.test.unit.TestBitStream;
import org.gunsioo.test.unit.TestBnf;
import org.gunsioo.test.unit.TestCache;
import org.gunsioo.test.unit.TestCharsetCollator;
import org.gunsioo.test.unit.TestCollation;
import org.gunsioo.test.unit.TestCompress;
import org.gunsioo.test.unit.TestConcurrentJdbc;
import org.gunsioo.test.unit.TestConnectionInfo;
import org.gunsioo.test.unit.TestDataPage;
import org.gunsioo.test.unit.TestDate;
import org.gunsioo.test.unit.TestDateIso8601;
import org.gunsioo.test.unit.TestDateTimeUtils;
import org.gunsioo.test.unit.TestDbException;
import org.gunsioo.test.unit.TestExit;
import org.gunsioo.test.unit.TestFile;
import org.gunsioo.test.unit.TestFileLock;
import org.gunsioo.test.unit.TestFileLockProcess;
import org.gunsioo.test.unit.TestFileSystem;
import org.gunsioo.test.unit.TestFtp;
import org.gunsioo.test.unit.TestGeometryUtils;
import org.gunsioo.test.unit.TestIntArray;
import org.gunsioo.test.unit.TestIntIntHashMap;
import org.gunsioo.test.unit.TestIntPerfectHash;
import org.gunsioo.test.unit.TestInterval;
import org.gunsioo.test.unit.TestJmx;
import org.gunsioo.test.unit.TestJsonUtils;
import org.gunsioo.test.unit.TestKeywords;
import org.gunsioo.test.unit.TestLocale;
import org.gunsioo.test.unit.TestMVTempResult;
import org.gunsioo.test.unit.TestMathUtils;
import org.gunsioo.test.unit.TestMemoryUnmapper;
import org.gunsioo.test.unit.TestMode;
import org.gunsioo.test.unit.TestNetUtils;
import org.gunsioo.test.unit.TestObjectDeserialization;
import org.gunsioo.test.unit.TestOldVersion;
import org.gunsioo.test.unit.TestOverflow;
import org.gunsioo.test.unit.TestPageStore;
import org.gunsioo.test.unit.TestPageStoreCoverage;
import org.gunsioo.test.unit.TestPattern;
import org.gunsioo.test.unit.TestPerfectHash;
import org.gunsioo.test.unit.TestPgServer;
import org.gunsioo.test.unit.TestReader;
import org.gunsioo.test.unit.TestRecovery;
import org.gunsioo.test.unit.TestReopen;
import org.gunsioo.test.unit.TestSampleApps;
import org.gunsioo.test.unit.TestScriptReader;
import org.gunsioo.test.unit.TestSecurity;
import org.gunsioo.test.unit.TestShell;
import org.gunsioo.test.unit.TestSort;
import org.gunsioo.test.unit.TestStreams;
import org.gunsioo.test.unit.TestStringCache;
import org.gunsioo.test.unit.TestStringUtils;
import org.gunsioo.test.unit.TestTimeStampWithTimeZone;
import org.gunsioo.test.unit.TestTools;
import org.gunsioo.test.unit.TestTraceSystem;
import org.gunsioo.test.unit.TestUtils;
import org.gunsioo.test.unit.TestValue;
import org.gunsioo.test.unit.TestValueMemory;
import org.gunsioo.test.utils.OutputCatcher;
import org.gunsioo.test.utils.SelfDestructor;
import org.gunsioo.tools.DeleteDbFiles;
import org.gunsioo.tools.Server;
import org.gunsioo.util.AbbaLockingDetector;
import org.gunsioo.util.Profiler;
import org.gunsioo.util.StringUtils;
import org.gunsioo.util.Task;
import org.gunsioo.util.ThreadDeadlockDetector;
import org.gunsioo.util.Utils;

/**
 * The main test application. JUnit is not used because loops are easier to
 * write in regular java applications (most tests are ran multiple times using
 * different settings).
 */
public class TestAll {

    static {
        // Locale.setDefault(new Locale("ru", "ru"));
    }

/*

PIT test:
java org.pitest.mutationtest.MutationCoverageReport
--reportDir data --targetClasses org.h2.dev.store.btree.StreamStore*
--targetTests org.h2.test.store.TestStreamStore
--sourceDirs src/test,src/tools

Dump heap on out of memory:
-XX:+HeapDumpOnOutOfMemoryError

Random test:
java15
cd h2database/h2/bin
del *.db
start cmd /k "java -cp .;%H2DRIVERS% org.h2.test.TestAll join >testJoin.txt"
start cmd /k "java -cp . org.h2.test.TestAll synth >testSynth.txt"
start cmd /k "java -cp . org.h2.test.TestAll all >testAll.txt"
start cmd /k "java -cp . org.h2.test.TestAll random >testRandom.txt"
start cmd /k "java -cp . org.h2.test.TestAll btree >testBtree.txt"
start cmd /k "java -cp . org.h2.test.TestAll halt >testHalt.txt"
java -cp . org.h2.test.TestAll crash >testCrash.txt

java org.h2.test.TestAll timer

*/

    /**
     * Set to true if any of the tests fail. Used to return an error code from
     * the whole program.
     */
    static boolean atLeastOneTestFailed;

    /**
     * Whether the MVStoreUsage storage is used.
     */
    public boolean mvStore = true;

    /**
     * If the test should run with many rows.
     */
    public boolean big;

    /**
     * If remote database connections should be used.
     */
    public boolean networked;

    /**
     * If in-memory databases should be used.
     */
    public boolean memory;

    /**
     * If code coverage is enabled.
     */
    public boolean codeCoverage;

    /**
     * If lazy queries should be used.
     */
    public boolean lazy;

    /**
     * The cipher to use (null for unencrypted).
     */
    public String cipher;

    /**
     * The file trace level value to use.
     */
    public int traceLevelFile;

    /**
     * If test trace information should be written (for debugging only).
     */
    public boolean traceTest;

    /**
     * If testing on Google App Engine.
     */
    public boolean googleAppEngine;

    /**
     * If a small cache and a low number for MAX_MEMORY_ROWS should be used.
     */
    public boolean diskResult;

    /**
     * Test using the recording file system.
     */
    public boolean reopen;

    /**
     * Test the split file system.
     */
    public boolean splitFileSystem;

    /**
     * If only fast/CI/Jenkins/Travis tests should be run.
     */
    public boolean travis;

    /**
     * the vmlens.com race condition tool
     */
    public boolean vmlens;

    /**
     * The lock timeout to use
     */
    public int lockTimeout = 50;

    /**
     * If the transaction log should be kept small (that is, the log should be
     * switched early).
     */
    boolean smallLog;

    /**
     * If SSL should be used for remote connections.
     */
    boolean ssl;

    /**
     * If MAX_MEMORY_UNDO=3 should be used.
     */
    boolean diskUndo;

    /**
     * If TRACE_LEVEL_SYSTEM_OUT should be set to 2 (for debugging only).
     */
    boolean traceSystemOut;

    /**
     * If the tests should run forever.
     */
    boolean endless;

    /**
     * The THROTTLE value to use.
     */
    public int throttle;

    /**
     * The THROTTLE value to use by default.
     */
    int throttleDefault = Integer.parseInt(System.getProperty("throttle", "0"));

    /**
     * If the test should stop when the first error occurs.
     */
    boolean stopOnError;

    /**
     * The cache type.
     */
    String cacheType;

    /** If not null the database should be opened with the collation parameter */
    public String collation;


    /**
     * The AB-BA locking detector.
     */
    AbbaLockingDetector abbaLockingDetector;

    /**
     * The list of tests.
     */
    ArrayList<TestBase> tests = new ArrayList<>();

    private Server server;

    HashSet<String> excludedTests = new HashSet<>();

    /**
     * The map of executed tests to detect not executed tests.
     * Boolean value is 'false' for a disabled test.
     */
    HashMap<Class<? extends TestBase>, Boolean> executedTests = new HashMap<>();

    /**
     * Run all tests.
     *
     * @param args the command line arguments
     */
    public static void main(String... args) throws Exception {
        OutputCatcher catcher = OutputCatcher.start();
        run(args);
        catcher.stop();
        catcher.writeTo("Test Output", "docs/html/testOutput.html");
        if (atLeastOneTestFailed) {
            System.exit(1);
        }
    }

    private static void run(String... args) throws Exception {
        SelfDestructor.startCountdown(4 * 60);
        long time = System.nanoTime();
        printSystemInfo();

        // use lower values, to better test those cases,
        // and (for delays) to speed up the tests

        System.setProperty("h2.maxMemoryRows", "100");

        System.setProperty("h2.delayWrongPasswordMin", "0");
        System.setProperty("h2.delayWrongPasswordMax", "0");
        System.setProperty("h2.useThreadContextClassLoader", "true");

        // System.setProperty("h2.modifyOnWrite", "true");

        // speedup
        // System.setProperty("h2.syncMethod", "");

/*

recovery tests with small freeList pages, page size 64

reopen org.h2.test.unit.TestPageStore
-Xmx1500m -D reopenOffset=3 -D reopenShift=1

power failure test
power failure test: larger binaries and additional index.
power failure test with randomly generating / dropping indexes and tables.

drop table test;
create table test(id identity, name varchar(100) default space(100));
@LOOP 10 insert into test select null, null from system_range(1, 100000);
delete from test;

documentation: review package and class level javadocs
documentation: rolling review at main.html

-------------

kill a test:
kill -9 `jps -l | grep "org.h2.test." | cut -d " " -f 1`

*/
        TestAll test = new TestAll();
        if (args.length > 0) {
            if ("travis".equals(args[0])) {
                test.travis = true;
                test.testAll(args, 1);
            } else if ("vmlens".equals(args[0])) {
                test.vmlens = true;
                test.testAll(args, 1);
            } else if ("reopen".equals(args[0])) {
                System.setProperty("h2.delayWrongPasswordMin", "0");
                System.setProperty("h2.analyzeAuto", "100");
                System.setProperty("h2.pageSize", "64");
                System.setProperty("h2.reopenShift", "5");
                FilePathRec.register();
                test.reopen = true;
                TestReopen reopen = new TestReopen();
                reopen.init();
                FilePathRec.setRecorder(reopen);
                test.runTests();
            } else if ("crash".equals(args[0])) {
                test.endless = true;
                new TestCrashAPI().runTest(test);
            } else if ("synth".equals(args[0])) {
                new TestSynth().runTest(test);
            } else if ("kill".equals(args[0])) {
                new TestKill().runTest(test);
            } else if ("random".equals(args[0])) {
                test.endless = true;
                new TestRandomSQL().runTest(test);
            } else if ("join".equals(args[0])) {
                new TestJoin().runTest(test);
                test.endless = true;
            } else if ("btree".equals(args[0])) {
                new TestBtreeIndex().runTest(test);
            } else if ("all".equals(args[0])) {
                test.testEverything();
            } else if ("codeCoverage".equals(args[0])) {
                test.codeCoverage = true;
                test.runCoverage();
            } else if ("multiThread".equals(args[0])) {
                new TestMulti().runTest(test);
            } else if ("halt".equals(args[0])) {
                new TestHaltApp().runTest(test);
            } else if ("timer".equals(args[0])) {
                new TestTimer().runTest(test);
            }
        } else {
            test.testAll(args, 0);
        }
        System.out.println(TestBase.formatTime(new StringBuilder(),
                TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - time)).append(" total").toString());
    }

    private void testAll(String[] args, int offset) throws Exception {
        int l = args.length;
        while (l > offset + 1) {
            if ("-exclude".equals(args[offset])) {
                excludedTests.add(args[offset + 1]);
                offset += 2;
            } else {
                break;
            }
        }
        runTests();
        if (!travis && !vmlens) {
            Profiler prof = new Profiler();
            prof.depth = 16;
            prof.interval = 1;
            prof.startCollecting();
            TestPerformance.main("-init", "-db", "1", "-size", "1000");
            prof.stopCollecting();
            System.out.println(prof.getTop(5));
            TestPerformance.main("-init", "-db", "1", "-size", "1000");
        }
    }

    /**
     * Run all tests in all possible combinations.
     */
    private void testEverything() throws SQLException {
        for (int c = 0; c < 2; c++) {
            if (c == 0) {
                cipher = null;
            } else {
                cipher = "AES";
            }
            for (int a = 0; a < 64; a++) {
                smallLog = (a & 1) != 0;
                big = (a & 2) != 0;
                networked = (a & 4) != 0;
                memory = (a & 8) != 0;
                ssl = (a & 16) != 0;
                diskResult = (a & 32) != 0;
                for (int trace = 0; trace < 3; trace++) {
                    traceLevelFile = trace;
                    test();
                }
            }
        }
    }

    /**
     * Run the tests with a number of different settings.
     */
    private void runTests() throws SQLException {

        if (Boolean.getBoolean("abba")) {
            abbaLockingDetector = new AbbaLockingDetector().startCollecting();
        }

        mvStore = true;
        smallLog = big = networked = memory = lazy = ssl = false;
        diskResult = traceSystemOut = diskUndo = false;
        traceTest = stopOnError = false;
        traceLevelFile = throttle = 0;
        cipher = null;

        // memory is a good match for multi-threaded, makes things happen
        // faster, more chance of exposing race conditions
        memory = true;
        test();
        if (vmlens) {
            return;
        }
        testAdditional();

        // test utilities
        big = !travis;
        testUtils();
        big = false;

        // lazy
        lazy = true;
        memory = true;
        test();
        lazy = false;

        // but sometimes race conditions need bigger windows
        memory = false;
        test();
        testAdditional();

        // basic pagestore testing
        memory = false;
        mvStore = false;
        test();
        testAdditional();
        mvStore = true;

        networked = true;

        memory = true;
        test();
        memory = false;

        lazy = true;
        test();
        lazy = false;

        networked = false;

        diskUndo = true;
        diskResult = true;
        traceLevelFile = 3;
        throttle = 1;
        cacheType = "SOFT_LRU";
        cipher = "AES";
        test();

        diskUndo = false;
        diskResult = false;
        traceLevelFile = 1;
        throttle = 0;
        cacheType = null;
        cipher = null;

        if (!travis) {
            traceLevelFile = 0;
            smallLog = true;
            networked = true;
            ssl = true;
            test();

            big = true;
            smallLog = false;
            networked = false;
            ssl = false;
            traceLevelFile = 0;
            test();
            testAdditional();

            big = false;
            cipher = "AES";
            test();
            cipher = null;
            test();
        }

        for (Entry<Class<? extends TestBase>, Boolean> entry : executedTests.entrySet()) {
            if (!entry.getValue()) {
                System.out.println("Warning: test " + entry.getKey().getName() + " was not executed.");
            }
        }
    }

    private void runCoverage() throws SQLException {
        smallLog = big = networked = memory = ssl = false;
        diskResult = traceSystemOut = diskUndo = false;
        traceTest = stopOnError = false;
        traceLevelFile = throttle = 0;
        cipher = null;

        memory = true;
        test();
        testAdditional();
        testUtils();

        mvStore = false;
        test();
        // testUnit();
    }

    /**
     * Run all tests with the current settings.
     */
    private void test() throws SQLException {
        System.out.println();
        System.out.println("Test " + toString() +
                " (" + Utils.getMemoryUsed() + " KB used)");
        beforeTest();
        try {
            // db
            addTest(new TestScript());
            addTest(new TestAlter());
            addTest(new TestAlterSchemaRename());
            addTest(new TestAutoRecompile());
            addTest(new TestBackup());
            addTest(new TestBigDb());
            addTest(new TestBigResult());
            addTest(new TestCases());
            addTest(new TestCheckpoint());
            addTest(new TestCompatibility());
            addTest(new TestCompatibilityOracle());
            addTest(new TestCompatibilitySQLServer());
            addTest(new TestCsv());
            addTest(new TestDeadlock());
            if (vmlens) {
                return;
            }
            addTest(new TestDuplicateKeyUpdate());
            addTest(new TestEncryptedDb());
            addTest(new TestExclusive());
            addTest(new TestFullText());
            addTest(new TestFunctionOverload());
            addTest(new TestFunctions());
            addTest(new TestInit());
            addTest(new TestIndex());
            addTest(new TestIndexHints());
            addTest(new TestLargeBlob());
            addTest(new TestLinkedTable());
            addTest(new TestListener());
            addTest(new TestLob());
            addTest(new TestMergeUsing());
            addTest(new TestMultiConn());
            addTest(new TestMultiDimension());
            addTest(new TestMultiThreadedKernel());
            addTest(new TestOpenClose());
            addTest(new TestOptimizerHints());
            addTest(new TestReadOnly());
            addTest(new TestRecursiveQueries());
            addTest(new TestGeneralCommonTableQueries());
            addTest(new TestAlterTableNotFound());
            addTest(new TestSelectTableNotFound());
            if (!memory) {
                // requires persistent store for reconnection tests
                addTest(new TestPersistentCommonTableExpressions());
            }
            addTest(new TestRights());
            addTest(new TestRunscript());
            addTest(new TestSQLInjection());
            addTest(new TestSessionsLocks());
            addTest(new TestSelectCountNonNullColumn());
            addTest(new TestSequence());
            addTest(new TestSpaceReuse());
            addTest(new TestSpatial());
            addTest(new TestSpeed());
            addTest(new TestTableEngines());
            addTest(new TestTempTables());
            addTest(new TestTransaction());
            addTest(new TestTriggersConstraints());
            addTest(new TestTwoPhaseCommit());
            addTest(new TestView());
            addTest(new TestViewAlterTable());
            addTest(new TestViewDropView());
            addTest(new TestSynonymForTable());

            // jdbc
            addTest(new TestBatchUpdates());
            addTest(new TestCallableStatement());
            addTest(new TestCancel());
            addTest(new TestConcurrentConnectionUsage());
            addTest(new TestConnection());
            addTest(new TestDatabaseEventListener());
            addTest(new TestLimitUpdates());
            addTest(new TestLobApi());
            addTest(new TestSQLXML());
            addTest(new TestManyJdbcObjects());
            addTest(new TestMetaData());
            addTest(new TestNativeSQL());
            addTest(new TestPreparedStatement());
            addTest(new TestResultSet());
            addTest(new TestStatement());
            addTest(new TestGetGeneratedKeys());
            addTest(new TestTransactionIsolation());
            addTest(new TestUpdatableResultSet());
            addTest(new TestZloty());
            addTest(new TestSetCollation());

            // jdbcx
            addTest(new TestConnectionPool());
            addTest(new TestDataSource());
            addTest(new TestXA());
            addTest(new TestXASimple());

            // server
            addTest(new TestAutoServer());
            addTest(new TestNestedLoop());

            // mvcc & row level locking
            addTest(new TestMvcc1());
            addTest(new TestMvcc2());
            addTest(new TestMvcc3());
            addTest(new TestMvcc4());
            addTest(new TestMvccMultiThreaded());
            addTest(new TestMvccMultiThreaded2());
            addTest(new TestRowLocks());
            addTest(new TestAnalyzeTableTx());

            // synth
            addTest(new TestBtreeIndex());
            addTest(new TestConcurrentUpdate());
            addTest(new TestDiskFull());
            addTest(new TestCrashAPI());
            addTest(new TestFuzzOptimizations());
            addTest(new TestLimit());
            addTest(new TestRandomCompare());
            addTest(new TestKillRestart());
            addTest(new TestKillRestartMulti());
            addTest(new TestMultiThreaded());
            addTest(new TestOuterJoins());
            addTest(new TestNestedJoins());

            runAddedTests();

            // serial
            addTest(new TestDateStorage());
            addTest(new TestDriver());
            addTest(new TestJavaObjectSerializer());
            addTest(new TestLocale());
            addTest(new TestMemoryUsage());
            addTest(new TestMultiThread());
            addTest(new TestPowerOff());
            addTest(new TestReorderWrites());
            addTest(new TestRandomSQL());
            addTest(new TestQueryCache());
            addTest(new TestUrlJavaObjectSerializer());
            addTest(new TestWeb());

            // other unsafe
            addTest(new TestOptimizations());
            addTest(new TestOutOfMemory());
            addTest(new TestIgnoreCatalogs());


            runAddedTests(1);
        } finally {
            afterTest();
        }
    }

    /**
     * Run additional tests.
     */
    private void testAdditional() {
        if (networked) {
            throw new RuntimeException("testAdditional() is not allowed in networked mode");
        }

        addTest(new TestMVTableEngine());
        addTest(new TestAutoReconnect());
        addTest(new TestBnf());
        addTest(new TestCache());
        addTest(new TestCollation());
        addTest(new TestCompress());
        addTest(new TestConnectionInfo());
        addTest(new TestExit());
        addTest(new TestFileLock());
        addTest(new TestJmx());
        addTest(new TestOldVersion());
        addTest(new TestMultiThreadedKernel());
        addTest(new TestPageStore());
        addTest(new TestPageStoreCoverage());
        addTest(new TestPgServer());
        addTest(new TestRecovery());
        addTest(new RecoverLobTest());
        addTest(createTest("org.h2.test.unit.TestServlet"));
        addTest(new TestTimeStampWithTimeZone());
        addTest(new TestValue());

        runAddedTests();

        addTest(new TestCluster());
        addTest(new TestFileLockProcess());
        addTest(new TestDefrag());
        addTest(new TestTools());
        addTest(new TestSampleApps());
        addTest(new TestSubqueryPerformanceOnLazyExecutionMode());

        runAddedTests(1);
    }

    /**
     * Run tests for utilities.
     */
    private void testUtils() {
        System.out.println();
        System.out.println("Test utilities (" + Utils.getMemoryUsed() + " KB used)");

        // mv store
        addTest(new TestCacheConcurrentLIRS());
        addTest(new TestCacheLIRS());
        addTest(new TestCacheLongKeyLIRS());
        addTest(new TestDataUtils());
        addTest(new TestFreeSpace());
        addTest(new TestKillProcessWhileWriting());
        addTest(new TestMVRTree());
        addTest(new TestMVStore());
        addTest(new TestMVStoreBenchmark());
        addTest(new TestMVStoreStopCompact());
        addTest(new TestMVStoreTool());
        addTest(new TestObjectDataType());
        addTest(new TestRandomMapOps());
        addTest(new TestSpinLock());
        addTest(new TestStreamStore());
        addTest(new TestTransactionStore());
        addTest(new TestMVTempResult());

        // unit
        addTest(new TestConcurrentJdbc());
        addTest(new TestAnsCompression());
        addTest(new TestBinaryArithmeticStream());
        addTest(new TestBinaryOperation());
        addTest(new TestBitStream());
        addTest(new TestCharsetCollator());
        addTest(new TestDataPage());
        addTest(new TestDateIso8601());
        addTest(new TestDbException());
        addTest(new TestFile());
        addTest(new TestFileSystem());
        addTest(new TestFtp());
        addTest(new TestGeometryUtils());
        addTest(new TestInterval());
        addTest(new TestIntArray());
        addTest(new TestIntIntHashMap());
        addTest(new TestIntPerfectHash());
        addTest(new TestJsonUtils());
        addTest(new TestKeywords());
        addTest(new TestMathUtils());
        addTest(new TestMemoryUnmapper());
        addTest(new TestMode());
        addTest(new TestObjectDeserialization());
        addTest(new TestOverflow());
        addTest(new TestPerfectHash());
        addTest(new TestReader());
        addTest(new TestScriptReader());
        addTest(new TestSecurity());
        addTest(new TestShell());
        addTest(new TestSort());
        addTest(new TestStreams());
        addTest(new TestStringUtils());
        addTest(new TestTraceSystem());
        addTest(new TestUtils());

        runAddedTests();

        // serial
        addTest(new TestDate());
        addTest(new TestDateTimeUtils());
        addTest(new TestMVStoreConcurrent());
        addTest(new TestNetUtils());
        addTest(new TestPattern());
        addTest(new TestStringCache());
        addTest(new TestValueMemory());
        addTest(new TestAuthentication());

        runAddedTests(1);
    }

    private void addTest(TestBase test) {
        if (excludedTests.contains(test.getClass().getName())) {
            return;
        }
        // tests.add(test);
        // run directly for now, because concurrently running tests
        // fails on Raspberry Pi quite often (seems to be a JVM problem)

        // event queue watchdog for tests that get stuck when running in Jenkins
        final java.util.Timer watchdog = new java.util.Timer();
        // 5 minutes
        watchdog.schedule(new TimerTask() {
            @Override
            public void run() {
                ThreadDeadlockDetector.dumpAllThreadsAndLocks("test watchdog timed out");
            }
        }, 5 * 60 * 1000);
        try {
            test.runTest(this);
        } finally {
            watchdog.cancel();
        }
    }

    private void runAddedTests() {
        int threadCount = ManagementFactory.getOperatingSystemMXBean().getAvailableProcessors();
        // threadCount = 2;
        runAddedTests(threadCount);
    }

    private void runAddedTests(int threadCount) {
        Task[] tasks = new Task[threadCount];
        for (int i = 0; i < threadCount; i++) {
            Task t = new Task() {
                @Override
                public void call() throws Exception {
                    while (true) {
                        TestBase test;
                        synchronized (tests) {
                            if (tests.isEmpty()) {
                                break;
                            }
                            test = tests.remove(0);
                        }
                        if (!excludedTests.contains(test.getClass().getName())) {
                            test.runTest(TestAll.this);
                        }
                    }
                }
            };
            t.execute();
            tasks[i] = t;
        }
        for (Task t : tasks) {
            t.get();
        }
    }

    private static TestBase createTest(String className) {
        try {
            Class<?> clazz = Class.forName(className);
            return (TestBase) clazz.getDeclaredConstructor().newInstance();
        } catch (Exception | NoClassDefFoundError e) {
            // ignore
            TestBase.printlnWithTime(0, className + " class not found");
        }
        return new TestBase() {

            @Override
            public void test() throws Exception {
                // ignore
            }

        };
    }

    /**
     * This method is called before a complete set of tests is run. It deletes
     * old database files in the test directory and trace files. It also starts
     * a TCP server if the test uses remote connections.
     */
    public void beforeTest() throws SQLException {
        Driver.load();
        FileUtils.deleteRecursive(TestBase.BASE_TEST_DIR, true);
        DeleteDbFiles.execute(TestBase.BASE_TEST_DIR, null, true);
        FileUtils.deleteRecursive("trace.db", false);
        if (networked) {
            String[] args = ssl ? new String[] { "-ifNotExists", "-tcpSSL" } : new String[] { "-ifNotExists" };
            server = Server.createTcpServer(args);
            try {
                server.start();
            } catch (SQLException e) {
                System.out.println("FAIL: can not start server (may already be running)");
                server = null;
            }
        }
    }

    /**
     * Stop the server if it was started.
     */
    public void afterTest() {
        if (networked && server != null) {
            server.stop();
        }
        FileUtils.deleteRecursive("trace.db", true);
        FileUtils.deleteRecursive(TestBase.BASE_TEST_DIR, true);
    }

    public int getPort() {
        return server == null ? 9192 : server.getPort();
    }

    /**
     * Print system information.
     */
    public static void printSystemInfo() {
        Properties prop = System.getProperties();
        System.out.println("H2 " + Constants.FULL_VERSION +
                " @ " + new java.sql.Timestamp(System.currentTimeMillis()).toString());
        System.out.println("Java " +
                prop.getProperty("java.runtime.version") + ", " +
                prop.getProperty("java.vm.name")+", " +
                prop.getProperty("java.vendor") + ", " +
                prop.getProperty("sun.arch.data.model"));
        System.out.println(
                prop.getProperty("os.name") + ", " +
                prop.getProperty("os.arch")+", "+
                prop.getProperty("os.version")+", "+
                prop.getProperty("sun.os.patch.level")+", "+
                prop.getProperty("file.separator")+" "+
                prop.getProperty("path.separator")+" "+
                StringUtils.javaEncode(prop.getProperty("line.separator")) + " " +
                prop.getProperty("user.country") + " " +
                prop.getProperty("user.language") + " " +
                prop.getProperty("user.timezone") + " " +
                prop.getProperty("user.variant")+" "+
                prop.getProperty("file.encoding"));
    }

    @Override
    public String toString() {
        StringBuilder buff = new StringBuilder();
        appendIf(buff, lazy, "lazy");
        if (mvStore) {
            buff.append("mvStore ");
        } else {
            buff.append("pageStore ");
        }
        appendIf(buff, big, "big");
        appendIf(buff, networked, "net");
        appendIf(buff, memory, "memory");
        appendIf(buff, codeCoverage, "codeCoverage");
        appendIf(buff, cipher != null, cipher);
        appendIf(buff, cacheType != null, cacheType);
        appendIf(buff, smallLog, "smallLog");
        appendIf(buff, ssl, "ssl");
        appendIf(buff, diskUndo, "diskUndo");
        appendIf(buff, diskResult, "diskResult");
        appendIf(buff, traceSystemOut, "traceSystemOut");
        appendIf(buff, endless, "endless");
        appendIf(buff, traceLevelFile > 0, "traceLevelFile");
        appendIf(buff, throttle > 0, "throttle:" + throttle);
        appendIf(buff, traceTest, "traceTest");
        appendIf(buff, stopOnError, "stopOnError");
        appendIf(buff, splitFileSystem, "split");
        appendIf(buff, collation != null, collation);
        return buff.toString();
    }

    private static void appendIf(StringBuilder buff, boolean flag, String text) {
        if (flag) {
            buff.append(text);
            buff.append(' ');
        }
    }

}
