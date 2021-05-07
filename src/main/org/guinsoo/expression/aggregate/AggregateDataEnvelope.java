/*
 * Copyright 2004-2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.expression.aggregate;

import java.util.ArrayList;

import org.guinsoo.engine.SessionLocal;
import org.guinsoo.expression.Expression;
import org.guinsoo.expression.ExpressionColumn;
import org.guinsoo.index.Index;
import org.guinsoo.mvstore.db.MVSpatialIndex;
import org.guinsoo.table.Column;
import org.guinsoo.table.TableFilter;
import org.guinsoo.util.geometry.GeometryUtils;
import org.guinsoo.value.Value;
import org.guinsoo.value.ValueGeometry;
import org.guinsoo.value.ValueNull;

/**
 * Data stored while calculating an aggregate.
 */
class AggregateDataEnvelope extends AggregateData {

    private double[] envelope;

    /**
     * Get the index (if any) for the column specified in the geometry
     * aggregate.
     *
     * @param on
     *            the expression (usually a column expression)
     * @return the index, or null
     */
    static Index getGeometryColumnIndex(Expression on) {
        if (on instanceof ExpressionColumn) {
            ExpressionColumn col = (ExpressionColumn) on;
            Column column = col.getColumn();
            if (column.getType().getValueType() == Value.GEOMETRY) {
                TableFilter filter = col.getTableFilter();
                if (filter != null) {
                    ArrayList<Index> indexes = filter.getTable().getIndexes();
                    if (indexes != null) {
                        for (int i = 1, size = indexes.size(); i < size; i++) {
                            Index index = indexes.get(i);
                            if (index instanceof MVSpatialIndex && index.isFirstColumn(column)) {
                                return index;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    @Override
    void add(SessionLocal session, Value v) {
        if (v == ValueNull.INSTANCE) {
            return;
        }
        envelope = GeometryUtils.union(envelope, v.convertToGeometry(null).getEnvelopeNoCopy());
    }

    @Override
    Value getValue(SessionLocal session) {
        return ValueGeometry.fromEnvelope(envelope);
    }

}
