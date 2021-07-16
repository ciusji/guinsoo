/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.compress;

/**
 * This class implements a data compression algorithm that does in fact not
 * compress. This is useful if the data can not be compressed because it is
 * encrypted, already compressed, or random.
 */
public class CompressNo implements Compressor {

    @Override
    public int getAlgorithm() {
        return NO;
    }

    @Override
    public void setOptions(String options) {
        // nothing to do
    }

    @Override
    public int compress(byte[] in, int inPos, int inLen, byte[] out, int outPos) {
        System.arraycopy(in, inPos, out, outPos, inLen);
        return outPos + inLen;
    }

    @Override
    public void expand(byte[] in, int inPos, int inLen, byte[] out, int outPos,
            int outLen) {
        System.arraycopy(in, inPos, out, outPos, outLen);
    }

}
