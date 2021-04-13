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

package org.gunsioo.samples;

import org.gunsioo.mvstore.MVMap;
import org.gunsioo.mvstore.MVStore;
import org.gunsioo.mvstore.OffHeapStore;
import org.gunsioo.mvstore.tx.Transaction;
import org.gunsioo.mvstore.tx.TransactionMap;
import org.gunsioo.mvstore.tx.TransactionStore;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

/**
 * MVStoreUsage
 *
 * @author cius.ji
 * @blame guinsoo Group
 * @since 1.8+
 */
public class MVStoreUsage {

    private String path = "/Users/admin/Desktop/relations4.csv";
    private String name = "relations";

    public void testStoreConcurrency() throws IOException {
        // open the store (in-memory if fileName is null)
        // MVStore s = MVStore.open(null);
        OffHeapStore offHeapStore = new OffHeapStore();
        MVStore s = new MVStore.Builder()
                .fileStore(offHeapStore)
                .open();

        MVMap<Long, String> map = s.openMap(name);

        long startTime = System.currentTimeMillis();
        AtomicLong longKey = new AtomicLong(0);
        // add and read some data
        int bufferSize = 1024;
        try (BufferedReader br = new BufferedReader(new FileReader(path), bufferSize)) {
            br.lines().parallel().forEach(it -> map.put(longKey.getAndIncrement(), it));
            // br.lines().parallel().forEach(it -> map.putIfAbsent(longKey.getAndIncrement(), it));
        }
        System.out.println("Duration: " + (System.currentTimeMillis() - startTime));

        s.commit();
        s.close();
    }

    public void testTransaction() {
        MVStore s = MVStore.open(null);
        TransactionStore ts = new TransactionStore(s);
        ts.init();
        Transaction tx;
        TransactionMap<Long, String> map;

        tx = ts.begin();

        map = tx.openMap("test");

        long startTime = System.currentTimeMillis();
        AtomicLong longKey = new AtomicLong(0);
        // add and read some data
        int bufferSize = 1024;
        try (BufferedReader br = new BufferedReader(new FileReader(path), bufferSize)) {
            br.lines().parallel().forEach(it -> map.put(longKey.getAndIncrement(), it));
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Duration: " + (System.currentTimeMillis() - startTime));

        s.close();
    }

    public static void main(String[] args) throws IOException {
        MVStoreUsage usage = new MVStoreUsage();
        usage.testStoreConcurrency();
        // usage.testTransaction();
    }
}
