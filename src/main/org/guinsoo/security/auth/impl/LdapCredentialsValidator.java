/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Alessandro Ventura
 */
package org.guinsoo.security.auth.impl;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import org.guinsoo.api.CredentialsValidator;
import org.guinsoo.security.auth.AuthenticationInfo;
import org.guinsoo.security.auth.ConfigProperties;

/**
 * Validate credentials by performing an LDAP bind
 * <p>
 * Configuration parameters:
 * </p>
 * <ul>
 *    <li>bindDnPattern bind dn pattern with %u instead of username
 *    (example: uid=%u,ou=users,dc=example,dc=com)</li>
 *    <li>host ldap server</li>
 *    <li>port of ldap service; optional, by default 389 for insecure, 636 for secure</li>
 *    <li>secure, optional by default is true (use SSL)</li>
 * </ul>
 */
public class LdapCredentialsValidator implements CredentialsValidator {

    private String bindDnPattern;
    private String host;
    private int port;
    private boolean secure;
    private String url;

    @Override
    public void configure(ConfigProperties configProperties) {
        bindDnPattern = configProperties.getStringValue("bindDnPattern");
        host = configProperties.getStringValue("host");
        secure = configProperties.getBooleanValue("secure", true);
        port = configProperties.getIntValue("port", secure ? 636 : 389);
        url = "ldap" + (secure ? "s" : "") + "://" + host + ":" + port;
    }

    @Override
    public boolean validateCredentials(AuthenticationInfo authenticationInfo) throws Exception {
        DirContext dirContext = null;
        try {
            String dn=bindDnPattern.replace("%u", authenticationInfo.getUserName());
            Hashtable<String, String> env = new Hashtable<>();
            env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            env.put(Context.PROVIDER_URL, url);
            env.put(Context.SECURITY_AUTHENTICATION, "simple");
            env.put(Context.SECURITY_PRINCIPAL, dn);
            env.put(Context.SECURITY_CREDENTIALS, authenticationInfo.getPassword());
            if (secure) {
                env.put(Context.SECURITY_PROTOCOL,"ssl");
            }
            dirContext = new InitialDirContext(env);
            authenticationInfo.setNestedIdentity(dn);
            return true;
        } finally {
            if (dirContext != null) {
                dirContext.close();
            }
        }

    }

}
