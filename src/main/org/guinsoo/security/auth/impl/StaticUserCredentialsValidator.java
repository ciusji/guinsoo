/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Alessandro Ventura
 */
package org.guinsoo.security.auth.impl;

import java.util.regex.Pattern;

import org.guinsoo.api.CredentialsValidator;
import org.guinsoo.security.SHA256;
import org.guinsoo.security.auth.AuthenticationException;
import org.guinsoo.security.auth.AuthenticationInfo;
import org.guinsoo.security.auth.ConfigProperties;
import org.guinsoo.util.MathUtils;
import org.guinsoo.util.StringUtils;
import org.guinsoo.util.Utils;

/**
 * This credentials validator matches the user and password with the configured
 * Usage should be limited to test purposes
 *
 */
public class StaticUserCredentialsValidator implements CredentialsValidator {

    private Pattern userNamePattern;
    private String password;
    private byte[] salt;
    private byte[] hashWithSalt;

    public StaticUserCredentialsValidator() {
    }

    public StaticUserCredentialsValidator(String userNamePattern,String password) {
        if (userNamePattern!=null) {
            this.userNamePattern=Pattern.compile(userNamePattern.toUpperCase());
        }
        salt= MathUtils.secureRandomBytes(256);
        hashWithSalt= SHA256.getHashWithSalt(password.getBytes(), salt);
    }

    @Override
    public boolean validateCredentials(AuthenticationInfo authenticationInfo) throws AuthenticationException {
        if (userNamePattern!=null) {
            if (!userNamePattern.matcher(authenticationInfo.getUserName()).matches()) {
                return false;
            }
        }
        if (password!=null) {
            return password.equals(authenticationInfo.getPassword());
        }
        return Utils.compareSecure(hashWithSalt,
                SHA256.getHashWithSalt(authenticationInfo.getPassword().getBytes(), salt));
    }

    @Override
    public void configure(ConfigProperties configProperties) {
        String userNamePatternString=configProperties.getStringValue("userNamePattern",null);
        if (userNamePatternString!=null) {
            userNamePattern = Pattern.compile(userNamePatternString);
        }
        password=configProperties.getStringValue("password",password);
        String saltString =configProperties.getStringValue("salt",null);
        if (saltString!=null) {
            salt= StringUtils.convertHexToBytes(saltString);
        }
        String hashString=configProperties.getStringValue("hash", null);
        if (hashString!=null) {
            hashWithSalt = SHA256.getHashWithSalt(StringUtils.convertHexToBytes(hashString), salt);
        }
    }

}
