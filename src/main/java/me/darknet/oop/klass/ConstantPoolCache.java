package me.darknet.oop.klass;

import me.darknet.oop.DataOop;
import me.darknet.oop.Structs;
import me.darknet.oop.data.Struct;
import me.darknet.oop.types.Types;

public class ConstantPoolCache extends DataOop<ConstantPoolCacheEntry> {

    public ConstantPoolCache(long base) {
        super(base, base + Types.getSize("ConstantPoolCache"), Structs.constantPoolCache);
    }

    @Override
    public int getLength() {
        return struct.getInt(base, "_length");
    }

    @Override
    public ConstantPoolCacheEntry get(int index) {
        return new ConstantPoolCacheEntry(dataBase + index * unsafe.addressSize());
    }

    @Override
    public void set(int index, ConstantPoolCacheEntry value) {

    }
}
