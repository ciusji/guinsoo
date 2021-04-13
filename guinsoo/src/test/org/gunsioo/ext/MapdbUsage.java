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

package org.gunsioo.ext;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import net.openhft.chronicle.map.ChronicleMap;
import org.gunsioo.mvstore.MVMap;
import org.gunsioo.mvstore.MVStore;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.mapdb.Serializer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

/**
 * MapdbUsage
 *
 * @author cius.ji
 * @blame guinsoo Group
 * @since 1.8+
 */
public class MapdbUsage {

    private String path = "/Users/admin/Desktop/relations2.csv";
    private String name = "relations";

    public void testStoreConcurrency() throws IOException {
        DB db = DBMaker.memoryDB().make();

        HTreeMap<Long, String> map = db.hashMap(name, Serializer.LONG, Serializer.STRING).createOrOpen();

        long startTime = System.currentTimeMillis();
        AtomicLong longKey = new AtomicLong(0);
        // add and read some data
        int bufferSize = 1024;
        try (BufferedReader br = new BufferedReader(new FileReader(path), bufferSize)) {
            br.lines().parallel().forEach(it -> map.put(longKey.getAndIncrement(), it));
            // br.lines().parallel().forEach(it -> map.putIfAbsent(longKey.getAndIncrement(), it));
        }
        System.out.println("Duration: " + (System.currentTimeMillis() - startTime));

        db.close();
    }

    public void testStoreConcurrency2() throws IOException {

        ChronicleMap<Long, String> map = ChronicleMap
                .of(Long.class, String.class)
                .name(name)
                .entries(10_000_000)
                .averageValue("340098736478169")
                .create();

        long startTime = System.currentTimeMillis();
        AtomicLong longKey = new AtomicLong(0);
        // add and read some data
        int bufferSize = 1024 * 2;
        try (BufferedReader br = new BufferedReader(new FileReader(path), bufferSize)) {
            br.lines().parallel().forEach(it -> map.put(longKey.getAndIncrement(), it));
            // br.lines().parallel().forEach(it -> map.putIfAbsent(longKey.getAndIncrement(), it));
        }
        System.out.println("Duration: " + (System.currentTimeMillis() - startTime));
        // System.out.println(map.size());
        map.close();
    }

    public void testStoreConcurrency3() throws IOException {
        HazelcastInstance hz = HazelcastClient.newHazelcastClient();

        IMap<Long, String> map = hz.getMap(name);

        long startTime = System.currentTimeMillis();
        AtomicLong longKey = new AtomicLong(0);
        // add and read some data
        int bufferSize = 1024;
        try (BufferedReader br = new BufferedReader(new FileReader(path), bufferSize)) {
            br.lines().parallel().forEach(it -> map.put(longKey.getAndIncrement(), it));
            // br.lines().parallel().forEach(it -> map.putIfAbsent(longKey.getAndIncrement(), it));
        }
        System.out.println("Duration: " + (System.currentTimeMillis() - startTime));

    }

    public void testStoreConcurrency4() throws IOException {
        // open the store (in-memory if fileName is null)
        // MVStore s = MVStore.open(null);
        MVStore s = MVStore.open("~/test");
//        OffHeapStore offHeapStore = new OffHeapStore();
//        MVStore s = new MVStore.Builder()
//                .fileStore(offHeapStore)
//                .open();

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

    public static void main(String[] args) throws IOException {
        // System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "4");
        // System.out.println(ForkJoinPool.getCommonPoolParallelism());
        MapdbUsage usage = new MapdbUsage();
        // usage.testStoreConcurrency();
        // usage.testStoreConcurrency2();
        // usage.testStoreConcurrency3();
        usage.testStoreConcurrency4();
    }
}
