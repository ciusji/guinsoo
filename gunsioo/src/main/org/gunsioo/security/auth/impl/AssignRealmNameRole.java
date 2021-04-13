/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://h2database.com/html/license.html).
 * Initial Developer: Alessandro Ventura
 */
package org.gunsioo.security.auth.impl;

import java.util.Arrays;
import java.util.Collection;

import org.gunsioo.api.UserToRolesMapper;
import org.gunsioo.security.auth.AuthenticationException;
import org.gunsioo.security.auth.AuthenticationInfo;
import org.gunsioo.security.auth.ConfigProperties;

/**
 * Assign to user a role based on realm name
 *
 *  * <p>
 * Configuration parameters:
 * </p>
 * <ul>
 * <li> roleNameFormat, optional by default is @{realm}</li>
 * </ul>
 */
public class AssignRealmNameRole implements UserToRolesMapper{

    private String roleNameFormat;

    public AssignRealmNameRole() {
        this("@%s");
    }

    public AssignRealmNameRole(String roleNameFormat) {
        this.roleNameFormat = roleNameFormat;
    }

    @Override
    public void configure(ConfigProperties configProperties) {
        roleNameFormat=configProperties.getStringValue("roleNameFormat",roleNameFormat);
    }

    @Override
    public Collection<String> mapUserToRoles(AuthenticationInfo authenticationInfo) throws AuthenticationException {
        return Arrays.asList(String.format(roleNameFormat, authenticationInfo.getRealm()));
    }

}
