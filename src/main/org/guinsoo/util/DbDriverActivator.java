/*
 * Copyright 2004-2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.util;

import org.guinsoo.Driver;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * The driver activator loads the Guinsoo driver when starting the bundle. The driver
 * is unloaded when stopping the bundle.
 */
public class DbDriverActivator implements BundleActivator {

    private static final String DATASOURCE_FACTORY_CLASS =
            "org.osgi.service.jdbc.DataSourceFactory";

    /**
     * Start the bundle. If the 'org.osgi.service.jdbc.DataSourceFactory' class
     * is available in the class path, this will load the database driver and
     * register the DataSourceFactory service.
     *
     * @param bundleContext the bundle context
     */
    @Override
    public void start(BundleContext bundleContext) {
        Driver driver = Driver.load();
        try {
            JdbcUtils.loadUserClass(DATASOURCE_FACTORY_CLASS);
        } catch (Exception e) {
            // class not found - don't register
            return;
        }
        // but don't ignore exceptions in this call
        OsgiDataSourceFactory.registerService(bundleContext, driver);
    }

    /**
     * Stop the bundle. This will unload the database driver. The
     * DataSourceFactory service is implicitly un-registered by the OSGi
     * framework.
     *
     * @param bundleContext the bundle context
     */
    @Override
    public void stop(BundleContext bundleContext) {
        Driver.unload();
    }

}
