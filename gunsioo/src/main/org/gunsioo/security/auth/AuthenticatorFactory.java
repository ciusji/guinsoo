/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://h2database.com/html/license.html).
 * Initial Developer: Alessandro Ventura
 */
package org.gunsioo.security.auth;

/**
 * Authenticator factory
 */
public class AuthenticatorFactory {

    /**
     * Factory method.
     * @return authenticator instance.
     */
    public static Authenticator createAuthenticator() {
        return DefaultAuthenticator.getInstance();
    }
}
