package me.darknet.oop.util;

import me.darknet.oop.types.Types;

public class OopUtil {

    private static final long narrow_klass_base = 0x000000000L;
    private static final long narrow_klass_shift = 3;

    public static long readCompKlassAddress(long address) {
        long value = 0;
        int size = Types.getSize("narrowKlass");
        if(size == 4) {
            value = UnsafeAccessor.getUnsafe().getInt(address);
        } else if(size == 8) {
            value = UnsafeAccessor.getUnsafe().getLong(address);
        }
        if(value != 0) {
            return narrow_klass_base + (value << narrow_klass_shift);
        }
        return value;
    }

}
