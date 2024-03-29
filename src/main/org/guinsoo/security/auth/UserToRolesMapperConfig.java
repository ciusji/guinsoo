/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Alessandro Ventura
 */
package org.guinsoo.security.auth;

import org.guinsoo.api.UserToRolesMapper;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuration for class that maps users to their roles.
 *
 * @see UserToRolesMapper
 */
public class UserToRolesMapperConfig implements HasConfigProperties {

    private String className;
    private List<PropertyConfig> properties;

    /**
     * @return Mapper class name.
     */
    public String getClassName() {
        return className;
    }

    /**
     * @param className mapper class name.
     */
    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * @return Mapper properties.
     */
    @Override
    public List<PropertyConfig> getProperties() {
        if (properties == null) {
            properties = new ArrayList<>();
        }
        return properties;
    }

}
