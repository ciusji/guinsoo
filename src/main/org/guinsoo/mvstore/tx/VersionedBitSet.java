/*
 * Copyright 2004-2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.mvstore.tx;

import java.util.BitSet;

/**
 * Class VersionedBitSet extends standard BitSet to add a version field.
 * This will allow bit set and version to be changed atomically.
 */
final class VersionedBitSet extends BitSet {
    private static final long serialVersionUID = 1L;

    private long version;

    public VersionedBitSet() {}

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    @Override
    public VersionedBitSet clone() {
        return (VersionedBitSet)super.clone();
    }
}
