package me.darknet.oop;

import me.darknet.oop.data.Struct;
import me.darknet.oop.util.Unsafe;
import me.darknet.oop.util.UnsafeAccessor;

public class Oop {

    protected static final Unsafe unsafe = UnsafeAccessor.getUnsafe();
    protected long base;
    protected final Struct struct;

    public Oop(long base, Struct struct) {
        this.base = base;
        this.struct = struct;
        OopCache.put(base, this);
    }

    public long getBase() {
        return base;
    }

    public void setBase(long base) {
        this.base = base;
    }

}
