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

/**
 * MVStoreUsage
 *
 * @author cius.ji
 * @blame guinsoo Group
 * @since 1.8+
 */
public class MVStoreUsage {

    public static void main(String[] args) {
        // open the store (in-memory if fileName is null)
        MVStore s = MVStore.open(null);

        // create/get the map named "data"
        MVMap<Integer, String> map = s.openMap("data");

        // add and read some data
        for (int i=0; i<8; i++) {
            map.put(i, "Hello World-" + i);
        }
        long oldVersion = s.getCurrentVersion();

        // from now on, the old version is read-only
        s.commit();

        // changes can be rolled back if required
        for (int i=0; i<4; i++) {
            map.put(i, "Hello World-????-" + i);
        }
        map.remove(5);
        map.remove(6);

        System.out.println(map.get(1));
        System.out.println(map.get(5));

        MVMap<Integer, String> oldMap = map.openVersion(oldVersion);

        // print the old version
        System.out.println(oldMap.get(1));
        System.out.println(oldMap.get(5));

        // close the store (this will persist changes)
        s.close();
    }
}
