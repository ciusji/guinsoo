/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.index;

import org.guinsoo.engine.SessionLocal;
import org.guinsoo.result.SearchRow;

/**
 * A spatial index. Spatial indexes are used to speed up searching
 * spatial/geometric data.
 */
public interface SpatialIndex {

    /**
     * Find a row or a list of rows and create a cursor to iterate over the
     * result.
     *
     * @param session the session
     * @param first the lower bound
     * @param last the upper bound
     * @param intersection the geometry which values should intersect with, or
     *            null for anything
     * @return the cursor to iterate over the results
     */
    Cursor findByGeometry(SessionLocal session, SearchRow first, SearchRow last, SearchRow intersection);

}
