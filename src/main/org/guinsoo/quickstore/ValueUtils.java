/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.guinsoo.quickstore;

/**
 * ValueUtil
 *
 * @author cius.ji
 * @since 1.8+
 */
public class ValueUtils {

    enum ValueResult {
        TRUE, FALSE, RETRY
    }

    /**
     * Used to try and read a value off-heap
     *
     * @param result      The result object
     * @param value       the value's off-heap Slice object
     * @param transformer value deserializer
     * @param <T>         the type of {@code transformer}'s output
     * @return {@code TRUE} if the value was read successfully
     * {@code FALSE} if the value is deleted
     * {@code RETRY} if the value was moved, or the version of the off-heap value does not match {@code version}.
     * In case of {@code TRUE}, the read value is stored in the returned Result, otherwise, the value is {@code null}.
     */
    <T> Result transform(Result result, ValueBuffer value, StoreTransformer<T> transformer) {
        // TODO: ???
        return null;
    }


}
