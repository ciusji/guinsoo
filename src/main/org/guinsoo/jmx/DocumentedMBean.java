/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.jmx;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Properties;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.NotCompliantMBeanException;
import javax.management.StandardMBean;

import org.guinsoo.util.Utils;

/**
 * An MBean that reads the documentation from a resource file.
 */
public class DocumentedMBean extends StandardMBean {

    private final String interfaceName;
    private Properties resources;

    public <T> DocumentedMBean(T impl, Class<T> mbeanInterface)
            throws NotCompliantMBeanException {
        super(impl, mbeanInterface);
        this.interfaceName = impl.getClass().getName() + "MBean";
    }

    private Properties getResources() {
        if (resources == null) {
            resources = new Properties();
            String resourceName = "/org/guinsoo/res/javadoc.properties";
            try {
                byte[] buff = Utils.getResource(resourceName);
                if (buff != null) {
                    resources.load(new ByteArrayInputStream(buff));
                }
            } catch (IOException e) {
                // ignore
            }
        }
        return resources;
    }

    @Override
    protected String getDescription(MBeanInfo info) {
        String s = getResources().getProperty(interfaceName);
        return s == null ? super.getDescription(info) : s;
    }

    @Override
    protected String getDescription(MBeanOperationInfo op) {
        String s = getResources().getProperty(interfaceName + "." + op.getName());
        return s == null ? super.getDescription(op) : s;
    }

    @Override
    protected String getDescription(MBeanAttributeInfo info) {
        String prefix = info.isIs() ? "is" : "get";
        String s = getResources().getProperty(
                interfaceName + "." + prefix + info.getName());
        return s == null ? super.getDescription(info) : s;
    }

    @Override
    protected int getImpact(MBeanOperationInfo info) {
        if (info.getName().startsWith("list")) {
            return MBeanOperationInfo.INFO;
        }
        return MBeanOperationInfo.ACTION;
    }

}
