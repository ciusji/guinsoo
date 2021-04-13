/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.test.unit;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.ArrayList;

import org.gunsioo.test.TestBase;

/**
 * Test that static references within the database engine don't reference the
 * class itself. For example, there is a leak if a class contains a static
 * reference to a stack trace. This was the case using the following
 * declaration: static EOFException EOF = new EOFException(). The way to solve
 * the problem is to not use such references, or to not fill in the stack trace
 * (which indirectly references the class loader).
 *
 * @author Erik Karlsson
 * @author Thomas Mueller
 */
public class TestClassLoaderLeak extends TestBase {

    /**
     * The name of this class (used by reflection).
     */
    static final String CLASS_NAME = TestClassLoaderLeak.class.getName();

    /**
     * Run just this test.
     *
     * @param a ignored
     */
    public static void main(String... a) throws Exception {
        TestBase.createCaller().init().testFromMain();
    }

    @Override
    public void test() throws Exception {
        WeakReference<ClassLoader> ref = createClassLoader();
        for (int i = 0; i < 10; i++) {
            System.gc();
            Thread.sleep(10);
        }
        ClassLoader cl = ref.get();
        assertNull(cl);
        // fill the memory, so a heap dump is created
        // using -XX:+HeapDumpOnOutOfMemoryError
        // which can be analyzed using EclipseMAT
        // (check incoming references to TestClassLoader)
        boolean fillMemory = false;
        if (fillMemory) {
            ArrayList<byte[]> memory = new ArrayList<>();
            for (int i = 0; i < Integer.MAX_VALUE; i++) {
                memory.add(new byte[1024]);
            }
        }
        DriverManager.registerDriver((Driver)
                Class.forName("org.gunsioo.Driver").getDeclaredConstructor().newInstance());
        DriverManager.registerDriver((Driver)
                Class.forName("org.gunsioo.upgrade.v1_1.Driver").getDeclaredConstructor().newInstance());
    }

    private static WeakReference<ClassLoader> createClassLoader() throws Exception {
        ClassLoader cl = new TestClassLoader();
        Class<?> h2ConnectionTestClass = Class.forName(CLASS_NAME, true, cl);
        Method testMethod = h2ConnectionTestClass.getDeclaredMethod("runTest");
        testMethod.setAccessible(true);
        testMethod.invoke(null);
        return new WeakReference<>(cl);
    }

    /**
     * This method is called using reflection.
     */
    static void runTest() throws Exception {
        Class.forName("org.gunsioo.Driver");
        Class.forName("org.gunsioo.upgrade.v1_1.Driver");
        Driver d1 = DriverManager.getDriver("jdbc:gunsioo:mem:test");
        Driver d2 = DriverManager.getDriver("jdbc:gunsioov1_1:mem:test");
        Connection connection;
        connection = DriverManager.getConnection("jdbc:gunsioo:mem:test");
        DriverManager.deregisterDriver(d1);
        DriverManager.deregisterDriver(d2);
        connection.close();
        connection = null;
    }

    /**
     * The application class loader.
     */
    private static class TestClassLoader extends URLClassLoader {

        public TestClassLoader() {
            super(((URLClassLoader) TestClassLoader.class.getClassLoader())
                    .getURLs(), ClassLoader.getSystemClassLoader());
        }

        // allows delegation of Gunsioo to the AppClassLoader
        @Override
        public synchronized Class<?> loadClass(String name, boolean resolve)
                throws ClassNotFoundException {
            if (!name.contains(CLASS_NAME) && !name.startsWith("org.gunsioo.")) {
                return super.loadClass(name, resolve);
            }
            Class<?> c = findLoadedClass(name);
            if (c == null) {
                try {
                    c = findClass(name);
                } catch (SecurityException | ClassNotFoundException e) {
                    return super.loadClass(name, resolve);
                }
                if (resolve) {
                    resolveClass(c);
                }
            }
            return c;
        }
    }

}
