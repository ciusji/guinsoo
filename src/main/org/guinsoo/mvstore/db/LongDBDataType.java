/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.mvstore.db;

import java.nio.ByteBuffer;

import org.guinsoo.mvstore.type.LongDataType;
import org.guinsoo.mvstore.WriteBuffer;

/**
 * Class LongDBDataType provides version of LongDataType which is backward compatible
 * with the way ValueDataType serializes Long values.
 * Backward compatibility aside, LongDataType could have been used instead.
 *
 * @author <a href='mailto:andrei.tokar@gmail.com'>Andrei Tokar</a>
 */
public class LongDBDataType extends LongDataType {

    public static final LongDBDataType INSTANCE = new LongDBDataType();
    private static final ValueDataType DUMMY = new ValueDataType();

    public LongDBDataType() {}

    @Override
    public void write(WriteBuffer buff, Long data) {
        ValueDataType.writeLong(buff, data);
    }

    @Override
    public Long read(ByteBuffer buff) {
        return DUMMY.read(buff).getLong();
    }
}
