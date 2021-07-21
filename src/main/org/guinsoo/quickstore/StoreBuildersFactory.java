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

import org.guinsoo.quickstore.common.StoreComparator;
import org.guinsoo.quickstore.common.integer.StoreIntComparator;
import org.guinsoo.quickstore.common.integer.StoreIntSerializer;

/**
 * StoreBuildersFactory
 *
 * @author cius.ji
 * @since 1.8+
 */
public class StoreBuildersFactory {

    // Integer factories
    public static final StoreComparator<Integer> DEFAULT_INT_COMPARATOR = new StoreIntComparator();
    public static final StoreSerializer<Integer> DEFAULT_INT_SERIALIZER = new StoreIntSerializer();

}
