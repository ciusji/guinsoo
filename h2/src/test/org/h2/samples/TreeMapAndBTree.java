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

package org.h2.samples;

import org.h2.mvstore.MVMap;
import org.h2.mvstore.MVStore;
import org.h2.mvstore.OffHeapStore;

import java.util.TreeMap;

/**
 * TreeMapAndBTree
 *
 * @author cius.ji
 * @blame guinsoo Group
 * @since 1.8+
 */
public class TreeMapAndBTree {

    private int limit = 30_000_000;

    public void treeMapUsage() {
        long startTime = System.currentTimeMillis();
        TreeMap<Integer, String> rbTree = new TreeMap<>();
        for (int i=0; i<limit; i++) {
            rbTree.put(i, "Hello World-" + i);
        }

        // java.lang.OutOfMemoryError: Java heap space
        System.out.println("Duration: " + (System.currentTimeMillis() - startTime));
    }

    public void btreeMapUsage() {
        long startTime = System.currentTimeMillis();
        OffHeapStore offHeapStore = new OffHeapStore();
        MVStore store = new MVStore.Builder()
                .fileStore(offHeapStore)
                .open();
        /// MVMap<Integer, String> bTree = MVStore.open(null).openMap("data");
        MVMap<Integer, String> bTree = store.openMap("data");
        for (int i=0; i<limit; i++) {
            bTree.put(i, "Hello World-" + i);
        }
        // (heap memory) Duration: ~ 51903
        // (off-heap memory) Duration: ~ 14470
        System.out.println("Duration: ~ " + (System.currentTimeMillis() - startTime));
    }

    public static void main(String[] args) {
        TreeMapAndBTree tab = new TreeMapAndBTree();
        tab.btreeMapUsage();

    }
}
