package me.darknet.oop;

import java.util.HashMap;
import java.util.Map;

public class OopCache {

    private static final Map<Long, Oop> cache = new HashMap<>();

    public static void put(long base, Oop oop) {
        cache.putIfAbsent(base, oop);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Oop> T get(long base) {
        return (T) cache.get(base);
    }

}
