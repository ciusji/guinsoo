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

import java.util.WeakHashMap;

/**
 * WeakMap
 *
 * @author cius.ji
 * @blame h2 Group
 * @since 1.8+
 */
public class WeakMap {
    public static void main(String[] args) {
        WeakHashMap<String, Integer> wm = new WeakHashMap<>();
        wm.put(new String("1"), 1);
        wm.put(new String("2"), 2);
        wm.put("3", 3);
        System.out.println("GC before: " + wm);

        System.gc();

        System.out.println("GC after: " + wm);
    }
}