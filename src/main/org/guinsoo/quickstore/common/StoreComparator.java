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

package org.guinsoo.quickstore.common;

import org.guinsoo.quickstore.StoreScopedReadBuffer;

import java.util.Comparator;

/**
 * StoreComparator
 *
 * @author cius.ji
 * @since 1.8+
 */
public interface StoreComparator<K> extends Comparator<K> {

    @Override
    default int compare(K key1, K key2) {
        return compareKeys(key1, key2);
    }

    int compareKeys(K key1, K key2);

    int compareSerializedKeys(StoreScopedReadBuffer serializedKey1, StoreScopedReadBuffer serializedKey2);

    int compareKeyAndSerializedKey(K key, StoreScopedReadBuffer serializedKey);
}
