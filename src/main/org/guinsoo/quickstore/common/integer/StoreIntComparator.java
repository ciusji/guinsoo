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

package org.guinsoo.quickstore.common.integer;

import org.guinsoo.quickstore.StoreScopedReadBuffer;
import org.guinsoo.quickstore.common.StoreComparator;

/**
 * StoreIntComparator
 *
 * @author cius.ji
 * @since 1.8+
 */
public class StoreIntComparator implements StoreComparator<Integer> {

    @Override
    public int compareKeys(Integer key1, Integer key2) {
        return Integer.compare(key1, key2);
    }

    @Override
    public int compareSerializedKeys(StoreScopedReadBuffer serializedKey1, StoreScopedReadBuffer serializedKey2) {
        return Integer.compare(serializedKey1.getInt(0), serializedKey2.getInt(0));
    }

    @Override
    public int compareKeyAndSerializedKey(Integer key, StoreScopedReadBuffer serializedKey) {
        return Integer.compare(key, serializedKey.getInt(0));
    }
}
