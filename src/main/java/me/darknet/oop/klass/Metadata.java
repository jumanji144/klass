package me.darknet.oop.klass;

import me.darknet.oop.util.Unsafe;
import me.darknet.oop.util.UnsafeAccessor;

public class Metadata {

    private Unsafe unsafe = UnsafeAccessor.getUnsafe();
    private long base;

    public Metadata(long base) {
        this.base = base;
    }

    public boolean isKlass() {
        // for klass the first field is always a _layout_helper
        long lh = unsafe.getInt(base) & 0xffffffffL;
        // the highest this value can ever be is ~0x1
        return lh <= 0x80000000L;
    }

    public boolean isMethod() {
        return !isKlass();
    }

    public Klass getKlass() {
        if(!isKlass()) throw new IllegalStateException("Metadata is not a klass");
        return Klass.of(base);
    }

    public Method getMethod() {
        if(isKlass()) throw new IllegalStateException("Metadata is not a method");
        return Method.of(base);
    }

}
