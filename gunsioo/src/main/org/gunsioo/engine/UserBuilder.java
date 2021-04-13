/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://h2database.com/html/license.html).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.engine;

import org.gunsioo.security.auth.AuthenticationInfo;
import org.gunsioo.util.MathUtils;

public class UserBuilder {

    /**
     * Build the database user starting from authentication informations.
     *
     * @param authenticationInfo
     *            authentication info
     * @param database
     *            target database
     * @param persistent
     *            true if the user will be persisted in the database
     * @return user bean
     */
    public static User buildUser(AuthenticationInfo authenticationInfo, Database database, boolean persistent) {
        User user = new User(database, persistent ? database.allocateObjectId() : -1,
                authenticationInfo.getFullyQualifiedName(), false);
        // In case of external authentication fill the password hash with random
        // data
        user.setUserPasswordHash(
                authenticationInfo.getRealm() == null ? authenticationInfo.getConnectionInfo().getUserPasswordHash()
                        : MathUtils.secureRandomBytes(64));
        user.setTemporary(!persistent);
        return user;
    }

}
