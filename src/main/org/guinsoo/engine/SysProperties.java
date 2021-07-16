/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.engine;

import org.guinsoo.security.auth.DefaultAuthenticator;
import org.guinsoo.util.MathUtils;
import org.guinsoo.util.Utils;

/**
 * The constants defined in this class are initialized from system properties.
 * Some system properties are per machine settings, and others are as a last
 * resort and temporary solution to work around a problem in the application or
 * database engine. Also, there are system properties to enable features that
 * are not yet fully tested or that are not backward compatible.
 * <p>
 * System properties can be set when starting the virtual machine:
 * </p>
 *
 * <pre>
 * java -Dguinsoo.baseDir=/temp
 * </pre>
 *
 * They can be set within the application, but this must be done before loading
 * any classes of this database (before loading the JDBC driver):
 *
 * <pre>
 * System.setProperty(&quot;guinsoo.baseDir&quot;, &quot;/temp&quot;);
 * </pre>
 */
public class SysProperties {

    /**
     * INTERNAL
     */
    public static final String H2_SCRIPT_DIRECTORY = "guinsoo.scriptDirectory";

    /**
     * INTERNAL
     */
    public static final String H2_BROWSER = "guinsoo.browser";

    /**
     * System property <code>user.home</code> (empty string if not set).<br />
     * It is usually set by the system, and used as a replacement for ~ in file
     * names.
     */
    public static final String USER_HOME =
            Utils.getProperty("user.home", "");

    /**
     * System property <code>guinsoo.allowedClasses</code> (default: *).<br />
     * Comma separated list of class names or prefixes.
     */
    public static final String ALLOWED_CLASSES =
            Utils.getProperty("guinsoo.allowedClasses", "*");

    /**
     * System property <code>guinsoo.enableAnonymousTLS</code> (default: true).<br />
     * When using TLS connection, the anonymous cipher suites should be enabled.
     */
    public static final boolean ENABLE_ANONYMOUS_TLS =
            Utils.getProperty("guinsoo.enableAnonymousTLS", true);

    /**
     * System property <code>guinsoo.bindAddress</code> (default: null).<br />
     * The bind address to use.
     */
    public static final String BIND_ADDRESS =
            Utils.getProperty("guinsoo.bindAddress", null);

    /**
     * System property <code>guinsoo.check</code>
     * (default: true for JDK/JRE, false for Android).<br />
     * Optional additional checks in the database engine.
     */
    public static final boolean CHECK =
            Utils.getProperty("guinsoo.check", !"0.9".equals(Utils.getProperty("java.specification.version", null)));

    /**
     * System property <code>guinsoo.clientTraceDirectory</code> (default:
     * trace.db/).<br />
     * Directory where the trace files of the JDBC client are stored (only for
     * client / server).
     */
    public static final String CLIENT_TRACE_DIRECTORY =
            Utils.getProperty("guinsoo.clientTraceDirectory", "trace.db/");

    /**
     * System property <code>guinsoo.collatorCacheSize</code> (default: 32000).<br />
     * The cache size for collation keys (in elements). Used when a collator has
     * been set for the database.
     */
    public static final int COLLATOR_CACHE_SIZE =
            Utils.getProperty("guinsoo.collatorCacheSize", 32_000);

    /**
     * System property <code>guinsoo.consoleTableIndexes</code>
     * (default: 100).<br />
     * Up to this many tables, the column type and indexes are listed.
     */
    public static final int CONSOLE_MAX_TABLES_LIST_INDEXES =
            Utils.getProperty("guinsoo.consoleTableIndexes", 100);

    /**
     * System property <code>guinsoo.consoleTableColumns</code>
     * (default: 500).<br />
     * Up to this many tables, the column names are listed.
     */
    public static final int CONSOLE_MAX_TABLES_LIST_COLUMNS =
            Utils.getProperty("guinsoo.consoleTableColumns", 500);

    /**
     * System property <code>guinsoo.consoleProcedureColumns</code>
     * (default: 500).<br />
     * Up to this many procedures, the column names are listed.
     */
    public static final int CONSOLE_MAX_PROCEDURES_LIST_COLUMNS =
            Utils.getProperty("guinsoo.consoleProcedureColumns", 300);

    /**
     * System property <code>guinsoo.consoleStream</code> (default: true).<br />
     * Guinsoo Console: stream query results.
     */
    public static final boolean CONSOLE_STREAM =
            Utils.getProperty("guinsoo.consoleStream", true);

    /**
     * System property <code>guinsoo.consoleTimeout</code> (default: 1800000).<br />
     * Guinsoo Console: session timeout in milliseconds. The default is 30 minutes.
     */
    public static final int CONSOLE_TIMEOUT =
            Utils.getProperty("guinsoo.consoleTimeout", 30 * 60 * 1000);

    /**
     * System property <code>guinsoo.dataSourceTraceLevel</code> (default: 1).<br />
     * The trace level of the data source implementation. Default is 1 for
     * error.
     */
    public static final int DATASOURCE_TRACE_LEVEL =
            Utils.getProperty("guinsoo.dataSourceTraceLevel", 1);

    /**
     * System property <code>guinsoo.delayWrongPasswordMin</code>
     * (default: 250).<br />
     * The minimum delay in milliseconds before an exception is thrown for using
     * the wrong user name or password. This slows down brute force attacks. The
     * delay is reset to this value after a successful login. Unsuccessful
     * logins will double the time until DELAY_WRONG_PASSWORD_MAX.
     * To disable the delay, set this system property to 0.
     */
    public static final int DELAY_WRONG_PASSWORD_MIN =
            Utils.getProperty("guinsoo.delayWrongPasswordMin", 250);

    /**
     * System property <code>guinsoo.delayWrongPasswordMax</code>
     * (default: 4000).<br />
     * The maximum delay in milliseconds before an exception is thrown for using
     * the wrong user name or password. This slows down brute force attacks. The
     * delay is reset after a successful login. The value 0 means there is no
     * maximum delay.
     */
    public static final int DELAY_WRONG_PASSWORD_MAX =
            Utils.getProperty("guinsoo.delayWrongPasswordMax", 4000);

    /**
     * System property <code>guinsoo.javaSystemCompiler</code> (default: true).<br />
     * Whether to use the Java system compiler
     * (ToolProvider.getSystemJavaCompiler()) if it is available to compile user
     * defined functions. If disabled or if the system compiler is not
     * available, the com.sun.tools.javac compiler is used if available, and
     * "javac" (as an external process) is used if not.
     */
    public static final boolean JAVA_SYSTEM_COMPILER =
            Utils.getProperty("guinsoo.javaSystemCompiler", true);

    /**
     * System property <code>guinsoo.lobCloseBetweenReads</code>
     * (default: false).<br />
     * Close LOB files between read operations.
     */
    public static boolean lobCloseBetweenReads =
            Utils.getProperty("guinsoo.lobCloseBetweenReads", false);

    /**
     * System property <code>guinsoo.lobClientMaxSizeMemory</code> (default:
     * 1048576).<br />
     * The maximum size of a LOB object to keep in memory on the client side
     * when using the server mode.
     */
    public static final int LOB_CLIENT_MAX_SIZE_MEMORY =
            Utils.getProperty("guinsoo.lobClientMaxSizeMemory", 1024 * 1024);

    /**
     * System property <code>guinsoo.maxFileRetry</code> (default: 16).<br />
     * Number of times to retry file delete and rename. in Windows, files can't
     * be deleted if they are open. Waiting a bit can help (sometimes the
     * Windows Explorer opens the files for a short time) may help. Sometimes,
     * running garbage collection may close files if the user forgot to call
     * Connection.close() or InputStream.close().
     */
    public static final int MAX_FILE_RETRY =
            Math.max(1, Utils.getProperty("guinsoo.maxFileRetry", 16));

    /**
     * System property <code>guinsoo.maxReconnect</code> (default: 3).<br />
     * The maximum number of tries to reconnect in a row.
     */
    public static final int MAX_RECONNECT =
            Utils.getProperty("guinsoo.maxReconnect", 3);

    /**
     * System property <code>guinsoo.maxMemoryRows</code>
     * (default: 40000 per GB of available RAM).<br />
     * The default maximum number of rows to be kept in memory in a result set.
     */
    public static final int MAX_MEMORY_ROWS =
            getAutoScaledForMemoryProperty("guinsoo.maxMemoryRows", 40_000);

    /**
     * System property <code>guinsoo.maxTraceDataLength</code>
     * (default: 65535).<br />
     * The maximum size of a LOB value that is written as data to the trace
     * system.
     */
    public static final long MAX_TRACE_DATA_LENGTH =
            Utils.getProperty("guinsoo.maxTraceDataLength", 65535);

    /**
     * System property <code>guinsoo.nioLoadMapped</code> (default: false).<br />
     * If the mapped buffer should be loaded when the file is opened.
     * This can improve performance.
     */
    public static final boolean NIO_LOAD_MAPPED =
            Utils.getProperty("guinsoo.nioLoadMapped", false);

    /**
     * System property <code>guinsoo.nioCleanerHack</code> (default: false).<br />
     * If enabled, use the reflection hack to un-map the mapped file if
     * possible. If disabled, System.gc() is called in a loop until the object
     * is garbage collected. See also
     * https://bugs.openjdk.java.net/browse/JDK-4724038
     */
    public static final boolean NIO_CLEANER_HACK =
            Utils.getProperty("guinsoo.nioCleanerHack", false);

    /**
     * System property <code>guinsoo.objectCache</code> (default: true).<br />
     * Cache commonly used values (numbers, strings). There is a shared cache
     * for all values.
     */
    public static final boolean OBJECT_CACHE =
            Utils.getProperty("guinsoo.objectCache", true);

    /**
     * System property <code>guinsoo.objectCacheMaxPerElementSize</code> (default:
     * 4096).<br />
     * The maximum size (precision) of an object in the cache.
     */
    public static final int OBJECT_CACHE_MAX_PER_ELEMENT_SIZE =
            Utils.getProperty("guinsoo.objectCacheMaxPerElementSize", 4096);

    /**
     * System property <code>guinsoo.objectCacheSize</code> (default: 1024).<br />
     * The maximum number of objects in the cache.
     * This value must be a power of 2.
     */
    public static final int OBJECT_CACHE_SIZE;
    static {
        try {
            OBJECT_CACHE_SIZE = MathUtils.nextPowerOf2(
                    Utils.getProperty("guinsoo.objectCacheSize", 1024));
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("Invalid guinsoo.objectCacheSize", e);
        }
    }

    /**
     * System property <code>guinsoo.pgClientEncoding</code> (default: UTF-8).<br />
     * Default client encoding for PG server. It is used if the client does not
     * sends his encoding.
     */
    public static final String PG_DEFAULT_CLIENT_ENCODING =
            Utils.getProperty("guinsoo.pgClientEncoding", "UTF-8");

    /**
     * System property <code>guinsoo.prefixTempFile</code> (default: guinsoo.temp).<br />
     * The prefix for temporary files in the temp directory.
     */
    public static final String PREFIX_TEMP_FILE =
            Utils.getProperty("guinsoo.prefixTempFile", "guinsoo.temp");

    /**
     * System property <code>guinsoo.forceAutoCommitOffOnCommit</code> (default: false).<br />
     * Throw error if transaction's auto-commit property is true when a commit is executed.
     */
    public static boolean FORCE_AUTOCOMMIT_OFF_ON_COMMIT =
            Utils.getProperty("guinsoo.forceAutoCommitOffOnCommit", false);

    /**
     * System property <code>guinsoo.serverCachedObjects</code> (default: 64).<br />
     * TCP Server: number of cached objects per session.
     */
    public static final int SERVER_CACHED_OBJECTS =
            Utils.getProperty("guinsoo.serverCachedObjects", 64);

    /**
     * System property <code>guinsoo.serverResultSetFetchSize</code>
     * (default: 100).<br />
     * The default result set fetch size when using the server mode.
     */
    public static final int SERVER_RESULT_SET_FETCH_SIZE =
            Utils.getProperty("guinsoo.serverResultSetFetchSize", 100);

    /**
     * System property <code>guinsoo.socketConnectRetry</code> (default: 16).<br />
     * The number of times to retry opening a socket. Windows sometimes fails
     * to open a socket, see bug
     * https://bugs.openjdk.java.net/browse/JDK-6213296
     */
    public static final int SOCKET_CONNECT_RETRY =
            Utils.getProperty("guinsoo.socketConnectRetry", 16);

    /**
     * System property <code>guinsoo.socketConnectTimeout</code>
     * (default: 2000).<br />
     * The timeout in milliseconds to connect to a server.
     */
    public static final int SOCKET_CONNECT_TIMEOUT =
            Utils.getProperty("guinsoo.socketConnectTimeout", 2000);

    /**
     * System property <code>guinsoo.splitFileSizeShift</code> (default: 30).<br />
     * The maximum file size of a split file is 1L &lt;&lt; x.
     */
    public static final long SPLIT_FILE_SIZE_SHIFT =
            Utils.getProperty("guinsoo.splitFileSizeShift", 30);

    /**
     * System property <code>guinsoo.traceIO</code> (default: false).<br />
     * Trace all I/O operations.
     */
    public static final boolean TRACE_IO =
            Utils.getProperty("guinsoo.traceIO", false);

    /**
     * System property <code>guinsoo.threadDeadlockDetector</code>
     * (default: false).<br />
     * Detect thread deadlocks in a background thread.
     */
    public static final boolean THREAD_DEADLOCK_DETECTOR =
            Utils.getProperty("guinsoo.threadDeadlockDetector", false);

    /**
     * System property <code>guinsoo.urlMap</code> (default: null).<br />
     * A properties file that contains a mapping between database URLs. New
     * connections are written into the file. An empty value in the map means no
     * redirection is used for the given URL.
     */
    public static final String URL_MAP =
            Utils.getProperty("guinsoo.urlMap", null);

    /**
     * System property <code>guinsoo.useThreadContextClassLoader</code>
     * (default: false).<br />
     * Instead of using the default class loader when deserializing objects, the
     * current thread-context class loader will be used.
     */
    public static final boolean USE_THREAD_CONTEXT_CLASS_LOADER =
        Utils.getProperty("guinsoo.useThreadContextClassLoader", false);

    /**
     * System property <code>guinsoo.javaObjectSerializer</code>
     * (default: null).<br />
     * The JavaObjectSerializer class name for java objects being stored in
     * column of type OTHER. It must be the same on client and server to work
     * correctly.
     */
    public static final String JAVA_OBJECT_SERIALIZER =
            Utils.getProperty("guinsoo.javaObjectSerializer", null);

    /**
     * System property <code>guinsoo.authConfigFile</code>
     * (default: null).<br />
     * authConfigFile define the URL of configuration file
     * of {@link DefaultAuthenticator}
     *
     */
    public static final String AUTH_CONFIG_FILE =
            Utils.getProperty("guinsoo.authConfigFile", null);

    private static final String H2_BASE_DIR = "guinsoo.baseDir";

    private SysProperties() {
        // utility class
    }

    /**
     * INTERNAL
     */
    public static void setBaseDir(String dir) {
        if (!dir.endsWith("/")) {
            dir += "/";
        }
        System.setProperty(H2_BASE_DIR, dir);
    }

    /**
     * INTERNAL
     */
    public static String getBaseDir() {
        return Utils.getProperty(H2_BASE_DIR, null);
    }

    /**
     * System property <code>guinsoo.scriptDirectory</code> (default: empty
     * string).<br />
     * Relative or absolute directory where the script files are stored to or
     * read from.
     *
     * @return the current value
     */
    public static String getScriptDirectory() {
        return Utils.getProperty(H2_SCRIPT_DIRECTORY, "");
    }

    /**
     * This method attempts to auto-scale some of our properties to take
     * advantage of more powerful machines out of the box. We assume that our
     * default properties are set correctly for approx. 1G of memory, and scale
     * them up if we have more.
     */
    private static int getAutoScaledForMemoryProperty(String key, int defaultValue) {
        String s = Utils.getProperty(key, null);
        if (s != null) {
            try {
                return Integer.decode(s);
            } catch (NumberFormatException e) {
                // ignore
            }
        }
        return Utils.scaleForAvailableMemory(defaultValue);
    }

}
