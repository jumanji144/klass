package me.darknet.oop.jvm;

import me.darknet.oop.util.Unsafe;
import me.darknet.oop.util.UnsafeAccessor;

public class JNIEnv {

    private static final Unsafe unsafe = UnsafeAccessor.getUnsafe();
    private final long base;

    public JNIEnv(long base) {
        this.base = base;
    }

    public long getStringUtfChars() {
        return unsafe.getAddress(base + 0x548);
    }

    public long releaseStringUtfChars() {
        return unsafe.getAddress(base + 1360);
    }

    public long getBase() {
        return base;
    }

}
