package me.darknet.oop;

import me.darknet.oop.klass.Symbol;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class OopCache {

    private static final Map<Long, Oop> cache = new HashMap<>();

    public static void put(long base, Oop oop) {
        cache.putIfAbsent(base, oop);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Oop> T get(long base) {
        return (T) cache.get(base);
    }

    public static <T extends Oop> T getOrPut(long base, Function<Long, T> function) {
        cache.putIfAbsent(base, function.apply(base));
        return get(base);
    }
}
