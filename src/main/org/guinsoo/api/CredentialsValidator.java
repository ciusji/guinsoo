/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Alessandro Ventura
 */
package org.guinsoo.api;

import org.guinsoo.security.auth.AuthenticationInfo;
import org.guinsoo.security.auth.Configurable;

/**
 * A class that implement this interface can be used to validate credentials
 * provided by client.
 * <p>
 * <b>This feature is experimental and subject to change</b>
 * </p>
 */
public interface CredentialsValidator extends Configurable {

    /**
     * Validate user credential.
     *
     * @param authenticationInfo
     *            = authentication info
     * @return true if credentials are valid, otherwise false
     * @throws Exception
     *             any exception occurred (invalid credentials or internal
     *             issue) prevent user login
     */
    boolean validateCredentials(AuthenticationInfo authenticationInfo) throws Exception;

}
