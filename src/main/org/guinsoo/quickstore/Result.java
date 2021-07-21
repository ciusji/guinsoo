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
 * StoreResult
 *
 * <p>
 * A sum type for holding either a generic type value or a bool flag.
 *
 * @author cius.ji
 * @since 1.8+
 */
public class Result {

    ValueUtils.ValueResult operationResult;

    Object value;

    Result() {
        invalidate();
    }

    void invalidate() {
        this.operationResult = ValueUtils.ValueResult.FALSE;
        this.value = null;
    }

    Result withValue(Object value) {
        this.operationResult = ValueUtils.ValueResult.TRUE;
        this.value = value;
        return this;
    }

    Result withFlag(ValueUtils.ValueResult flag) {
        this.operationResult = flag;
        this.value = null;
        return this;
    }

    Result withFlag(boolean flag) {
        this.operationResult = flag ? ValueUtils.ValueResult.TRUE : ValueUtils.ValueResult.FALSE;
        this.value = null;
        return this;
    }
}
