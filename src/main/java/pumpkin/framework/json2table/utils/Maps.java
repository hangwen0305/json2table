package pumpkin.framework.json2table.utils;

import java.util.Map;

public class Maps {

    public static <K, V> boolean isNullOrEmpty(final Map<K, V> map) {
        return map == null || map.isEmpty();
    }
}
