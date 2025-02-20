package com.thundax.analysis.core.client.protocol;

import java.util.Map;

public interface Jsonable {

    /**
     * Converts the object to a map
     *
     * @return map
     */
    default Map<String, ?> toMap() {
        return null;
    }

    /**
     * Converts the map to the object
     *
     * @param map map
     */
    default void fromMap(Map<String, ?> map) {

    }
}
