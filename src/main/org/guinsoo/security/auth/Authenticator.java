/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Alessandro Ventura
 */
package org.guinsoo.security.auth;

import org.guinsoo.engine.Database;
import org.guinsoo.engine.User;

/**
 * Low level interface to implement full authentication process.
 */
public interface Authenticator {

    /**
     * Perform user authentication.
     *
     * @param authenticationInfo authentication info.
     * @param database target database instance.
     * @return valid database user or null if user doesn't exists in the
     *         database
     */
    User authenticate(AuthenticationInfo authenticationInfo, Database database) throws AuthenticationException;

    /**
     * Initialize the authenticator. This method is invoked by databases when
     * the authenticator is set when the authenticator is set.
     *
     * @param database target database
     */
    void init(Database database) throws AuthConfigException;
}
