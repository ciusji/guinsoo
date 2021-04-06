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

package org.h2.ext;

import org.h2.util.FastList;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * FastArray
 *
 * @author cius.ji
 * @blame guinsoo Group
 * @since 1.8+
 */
public class FastArray {
    private int limit = 30_000_000;

    // Duration: 13987
    // Duration with capacity: 7163
    public void putNumByArrayList() {
        ArrayList<Integer> arrayList = new ArrayList<>(limit);
        for (int i=0; i<limit; i++) {
            arrayList.add(i);
        }
        System.out.println(arrayList.size());
    }

    // Duration: 5753
    public void putNumByFastList() {
        FastList<Integer> fastList = new FastList<>(Integer.class);
        for (int i=0; i<limit; i++) {
            fastList.add(i);
        }
        System.out.println(fastList.size());
    }

    // Duration: 22029
    public void putNumByLinkedList() {
        LinkedList<String> linkedList = new LinkedList<>();
        for (int i=0; i<limit; i++) {
            linkedList.add("Hello world " + i);
        }
        System.out.println(linkedList.size());
    }

    public static void main(String[] args) {
        FastArray fa = new FastArray();
        long start = System.currentTimeMillis();
        fa.putNumByArrayList();
        // fa.putNumByFastList();
        // fa.putNumByLinkedList();
        System.out.println("Duration: " + (System.currentTimeMillis() - start));
    }

}
