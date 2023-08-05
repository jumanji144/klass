package me.darknet.oop.klass;

import me.darknet.oop.Oop;
import me.darknet.oop.Structs;
import me.darknet.oop.data.Struct;

public class ConstantPoolCacheEntry extends Oop {

    //  right_n_bits(cp_index_bits)
    private static final int CP_INDEX_BITS = 2 * 8;
    private static final int CP_INDEX_MASK = (1 << CP_INDEX_BITS) - 1;

    public ConstantPoolCacheEntry(long base) {
        super(base, Structs.constantPoolCacheEntry);
    }

    public long getIndecies() {
        return struct.getLong(base, "_indices");
    }

    public int getCpIndex() {
        return (int) (getIndecies() & CP_INDEX_MASK);
    }

}
