/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.engine;

import org.guinsoo.api.JavaObjectSerializer;
import org.guinsoo.util.TimeZoneProvider;
import org.guinsoo.value.ValueTimestampTimeZone;

/**
 * Provides information for type casts and comparison operations.
 */
public interface CastDataProvider {

    /**
     * Returns the current timestamp with maximum resolution. The value must be
     * the same within a transaction or within execution of a command.
     *
     * @return the current timestamp for CURRENT_TIMESTAMP(9)
     */
    ValueTimestampTimeZone currentTimestamp();

    /**
     * Returns the current time zone.
     *
     * @return the current time zone
     */
    TimeZoneProvider currentTimeZone();

    /**
     * Returns the database mode.
     *
     * @return the database mode
     */
    Mode getMode();

    /**
     * Returns the custom Java object serializer, or {@code null}.
     *
     * @return the custom Java object serializer, or {@code null}
     */
    JavaObjectSerializer getJavaObjectSerializer();

    /**
     * Returns are ENUM values 0-based.
     *
     * @return are ENUM values 0-based
     */
    boolean zeroBasedEnums();

}
