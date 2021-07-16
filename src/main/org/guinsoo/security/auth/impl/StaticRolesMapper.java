/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Alessandro Ventura
 */
package org.guinsoo.security.auth.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import org.guinsoo.api.UserToRolesMapper;
import org.guinsoo.security.auth.AuthenticationException;
import org.guinsoo.security.auth.AuthenticationInfo;
import org.guinsoo.security.auth.ConfigProperties;

/**
 * Assign static roles to authenticated users
 * <p>
 * Configuration parameters:
 * </p>
 * <ul>
 *   <li>roles role list separated by comma</li>
 * </ul>
 *
 */
public class StaticRolesMapper implements UserToRolesMapper {

    private Collection<String> roles;

    public StaticRolesMapper() {
    }

    public StaticRolesMapper(String... roles) {
        this.roles=Arrays.asList(roles);
    }

    @Override
    public void configure(ConfigProperties configProperties) {
        String rolesString=configProperties.getStringValue("roles", "");
        if (rolesString!=null) {
            roles = new HashSet<>(Arrays.asList(rolesString.split(",")));
        }
    }

    @Override
    public Collection<String> mapUserToRoles(AuthenticationInfo authenticationInfo) throws AuthenticationException {
        return roles;
    }

}
