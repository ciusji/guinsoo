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

import java.util.concurrent.atomic.AtomicLong;

/**
 * Block
 *
 * @author cius.ji
 * @since 1.8+
 */
public class Block {

    private final long blockMemAddress;

    private final int capacity;
    private final AtomicLong allocated = new AtomicLong(0);
    private int id;

    public Block(long capacity) {
        assert capacity > 0;
        assert capacity <= Integer.MAX_VALUE;
        this.capacity = (int) capacity;
        this.id = NativeMemoryAllocator.INVALID_BLOCK_ID;
        this.blockMemAddress = UnsafeUtils.allocateMemory(capacity);
    }
}
