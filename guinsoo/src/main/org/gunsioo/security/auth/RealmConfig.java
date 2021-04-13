/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Alessandro Ventura
 */
package org.gunsioo.security.auth;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuration for authentication realm.
 */
public class RealmConfig implements HasConfigProperties {

    private String name;
    private String validatorClass;
    private List<PropertyConfig> properties;

    /**
     * Gets realm's name.
     *
     * @return realm's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets realm's name.
     *
     * @param name realm's name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets validator class name.
     *
     * @return validator class name.
     */
    public String getValidatorClass() {
        return validatorClass;
    }

    /**
     * Sets validator class name.
     *
     * @param  validatorClass validator class name.
     */
    public void setValidatorClass(String validatorClass) {
        this.validatorClass = validatorClass;
    }

    @Override
    public List<PropertyConfig> getProperties() {
        if (properties == null) {
            properties = new ArrayList<>();
        }
        return properties;
    }

}
