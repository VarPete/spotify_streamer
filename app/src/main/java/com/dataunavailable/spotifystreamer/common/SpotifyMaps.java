package com.dataunavailable.spotifystreamer.common;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Panayiotis on 7/11/2015.
 */
public class SpotifyMaps {

    private SpotifyMaps() {
        throw new RuntimeException("Class is static only.");
    }

    public static final Map<String, Object> COUNTRY_MAP;

    static {
        Map<String, Object> tempMap = new HashMap<>();
        tempMap.put("country", "US");
        COUNTRY_MAP = Collections.unmodifiableMap(tempMap);
    }


}
