/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Alessandro Ventura
 */
package org.guinsoo.security.auth;

import java.util.List;

/**
 * Interface for objects with configuration properties.
 */
public interface HasConfigProperties {
    List<PropertyConfig> getProperties();
}
