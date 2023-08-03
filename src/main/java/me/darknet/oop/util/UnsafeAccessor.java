package me.darknet.oop.util;

import java.lang.reflect.Field;

public class UnsafeAccessor {

    private static final Unsafe unsafe;

    public static Unsafe getUnsafe() {
        return unsafe;
    }

    static {
        unsafe = new Unsafe(Unsafe.theUnsafe);
    }

}
