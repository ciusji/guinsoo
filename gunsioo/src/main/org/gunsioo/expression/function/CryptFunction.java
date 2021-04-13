/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://h2database.com/html/license.html).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.expression.function;

import org.gunsioo.engine.SessionLocal;
import org.gunsioo.expression.Expression;
import org.gunsioo.expression.TypedValueExpression;
import org.gunsioo.message.DbException;
import org.gunsioo.security.BlockCipher;
import org.gunsioo.security.CipherFactory;
import org.gunsioo.util.MathUtils;
import org.gunsioo.util.Utils;
import org.gunsioo.value.DataType;
import org.gunsioo.value.TypeInfo;
import org.gunsioo.value.Value;
import org.gunsioo.value.ValueVarbinary;

/**
 * An ENCRYPT or DECRYPT function.
 */
public final class CryptFunction extends FunctionN {

    /**
     * ENCRYPT() (non-standard).
     */
    public static final int ENCRYPT = 0;

    /**
     * DECRYPT() (non-standard).
     */
    public static final int DECRYPT = ENCRYPT + 1;

    private static final String[] NAMES = { //
            "ENCRYPT", "DECRYPT" //
    };

    private final int function;

    public CryptFunction(Expression arg1, Expression arg2, Expression arg3, int function) {
        super(new Expression[] { arg1, arg2, arg3 });
        this.function = function;
    }

    @Override
    public Value getValue(SessionLocal session, Value v1, Value v2, Value v3) {
        BlockCipher cipher = CipherFactory.getBlockCipher(v1.getString());
        cipher.setKey(getPaddedArrayCopy(v2.getBytesNoCopy(), cipher.getKeyLength()));
        byte[] newData = getPaddedArrayCopy(v3.getBytesNoCopy(), BlockCipher.ALIGN);
        switch (function) {
        case ENCRYPT:
            cipher.encrypt(newData, 0, newData.length);
            break;
        case DECRYPT:
            cipher.decrypt(newData, 0, newData.length);
            break;
        default:
            throw DbException.getInternalError("function=" + function);
        }
        return ValueVarbinary.getNoCopy(newData);
    }

    private static byte[] getPaddedArrayCopy(byte[] data, int blockSize) {
        return Utils.copyBytes(data, MathUtils.roundUpInt(data.length, blockSize));
    }

    @Override
    public Expression optimize(SessionLocal session) {
        boolean allConst = optimizeArguments(session, true);
        TypeInfo t = args[2].getType();
        type = DataType.isBinaryStringType(t.getValueType())
                ? TypeInfo.getTypeInfo(Value.VARBINARY, t.getPrecision(), 0, null)
                : TypeInfo.TYPE_VARBINARY;
        if (allConst) {
            return TypedValueExpression.getTypedIfNull(getValue(session), type);
        }
        return this;
    }

    @Override
    public String getName() {
        return NAMES[function];
    }

}
