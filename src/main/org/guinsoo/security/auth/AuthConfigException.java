/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Alessandro Ventura
 */
package org.guinsoo.security.auth;

/**
 * Exception thrown when an issue occurs during the authentication configuration
 *
 */
public class AuthConfigException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public AuthConfigException() {
        super();
    }

    public AuthConfigException(String message) {
        super(message);
    }

    public AuthConfigException(Throwable cause) {
        super(cause);
    }

    public AuthConfigException(String message, Throwable cause) {
        super(message, cause);
    }
}
