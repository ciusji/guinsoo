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

/**
 * StoreBuffer
 *
 * <p>
 * This buffer may represent either a key or a value for read access.
 * It mimic the standard interface of Java's ByteBuffer, for example, int getInt(int index), capacity(), etc.
 *
 * @author cius.ji
 * @since 1.8+
 */
public interface StoreBuffer {

    /**
     * Returns the buffer's capacity.
     *
     * @return the capacity of the buffer.
     */
    int capacity();

    /**
     * Reads the byte at the given index.
     *
     * @param index read index
     * @return the byte at the given index.
     */
    byte get(int index);

    /**
     * Reading a char value.
     *
     * @param index read index.
     * @return the char at the give index.
     */
    char getChar(int index);

    /**
     * Reading an int value.
     * @param index reading index.
     * @return the int value at the given index.
     */
    int getInt(int index);

    // TODO: add other types of get* method.
}
