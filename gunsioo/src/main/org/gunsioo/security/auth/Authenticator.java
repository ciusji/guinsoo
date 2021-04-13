/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://h2database.com/html/license.html).
 * Initial Developer: Alessandro Ventura
 */
package org.gunsioo.security.auth;

import org.gunsioo.engine.Database;
import org.gunsioo.engine.User;

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
