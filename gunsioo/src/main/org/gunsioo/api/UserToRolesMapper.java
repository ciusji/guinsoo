/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Alessandro Ventura
 */
package org.gunsioo.api;

import java.util.Collection;

import org.gunsioo.security.auth.AuthenticationException;
import org.gunsioo.security.auth.AuthenticationInfo;
import org.gunsioo.security.auth.Configurable;

/**
 * A class that implement this interface can be used during authentication to
 * map external users to database roles.
 * <p>
 * <b>This feature is experimental and subject to change</b>
 * </p>
 */
public interface UserToRolesMapper extends Configurable {

    /**
     * Map user identified by authentication info to a set of granted roles.
     *
     * @param authenticationInfo
     *            authentication information
     * @return list of roles to be assigned to the user temporary
     * @throws AuthenticationException
     *             on authentication exception
     */
    Collection<String> mapUserToRoles(AuthenticationInfo authenticationInfo) throws AuthenticationException;
}
