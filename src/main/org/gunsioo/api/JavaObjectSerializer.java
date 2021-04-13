/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.api;

/**
 * Custom serialization mechanism for java objects being stored in column of
 * type OTHER.
 *
 * @author Sergi Vladykin
 */
public interface JavaObjectSerializer {

    /**
     * Serialize object to byte array.
     *
     * @param obj the object to serialize
     * @return the byte array of the serialized object
     */
    byte[] serialize(Object obj) throws Exception;

    /**
     * Deserialize object from byte array.
     *
     * @param bytes the byte array of the serialized object
     * @return the object
     */
    Object deserialize(byte[] bytes) throws Exception;

}
