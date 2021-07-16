/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.mvstore.db;

import java.util.Arrays;

import org.guinsoo.mvstore.rtree.Spatial;
import org.guinsoo.engine.CastDataProvider;
import org.guinsoo.value.CompareMode;
import org.guinsoo.value.TypeInfo;
import org.guinsoo.value.Value;

/**
 * A unique spatial key.
 */
public class SpatialKey extends Value implements Spatial {

    private final long id;
    private final float[] minMax;

    /**
     * Create a new key.
     *
     * @param id the id
     * @param minMax min x, max x, min y, max y, and so on
     */
    public SpatialKey(long id, float... minMax) {
        this.id = id;
        this.minMax = minMax;
    }

    public SpatialKey(long id, SpatialKey other) {
        this.id = id;
        this.minMax = other.minMax.clone();
    }

    @Override
    public float min(int dim) {
        return minMax[dim + dim];
    }

    @Override
    public void setMin(int dim, float x) {
        minMax[dim + dim] = x;
    }

    @Override
    public float max(int dim) {
        return minMax[dim + dim + 1];
    }

    @Override
    public void setMax(int dim, float x) {
        minMax[dim + dim + 1] = x;
    }

    @Override
    public Spatial clone(long id) {
        return new SpatialKey(id, this);
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public boolean isNull() {
        return minMax.length == 0;
    }

    @Override
    public String toString() {
        return getString();
    }

    @Override
    public int hashCode() {
        return (int) ((id >>> 32) ^ id);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        } else if (!(other instanceof SpatialKey)) {
            return false;
        }
        SpatialKey o = (SpatialKey) other;
        if (id != o.id) {
            return false;
        }
        return equalsIgnoringId(o);
    }

    @Override
    public int compareTypeSafe(Value v, CompareMode mode, CastDataProvider provider) {
        throw new UnsupportedOperationException();
//        return 0;
    }

    /**
     * Check whether two objects are equals, but do not compare the id fields.
     *
     * @param o the other key
     * @return true if the contents are the same
     */
    @Override
    public boolean equalsIgnoringId(Spatial o) {
        return Arrays.equals(minMax, ((SpatialKey)o).minMax);
    }

    @Override
    public StringBuilder getSQL(StringBuilder builder, int sqlFlags) {
        builder.append(id).append(": (");
        for (int i = 0; i < minMax.length; i += 2) {
            if (i > 0) {
                builder.append(", ");
            }
            builder.append(minMax[i]).append('/').append(minMax[i + 1]);
        }
        builder.append(")");
        return builder;
    }

    @Override
    public TypeInfo getType() {
        return TypeInfo.TYPE_GEOMETRY;
    }

    @Override
    public int getValueType() {
        return Value.GEOMETRY;
    }

    @Override
    public String getString() {
        return getTraceSQL();
    }

}
