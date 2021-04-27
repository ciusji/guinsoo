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

package org.gunsioo.quickstore;

import net.openhft.chronicle.map.ChronicleMap;
import org.gunsioo.engine.ConnectionInfo;
import org.gunsioo.engine.Constants;

/**
 * QuickStore
 *
 * @author cius.ji
 * @blame guinsoo Group
 * @since 1.8+
 */
public class QuickStore {

    private ChronicleMap<Long, String> xxMap;

    public QuickStore() {
    }

    public QuickStore(ConnectionInfo info) {
        ChronicleMap<Long, String> map = ChronicleMap
                .of(Long.class, String.class)
                .name(info.getName())
                .entries(Constants.SMALL_STORE_KEYS)
                .averageValue(Constants.EXT_KEYS)
                .create();
        setXxMap(map);
    }

    public void setXxMap(ChronicleMap<Long, String> xxMap) {
        this.xxMap = xxMap;
    }

    public ChronicleMap<Long, String> getXxMap() {
        return xxMap;
    }
}
