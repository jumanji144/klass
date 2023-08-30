package me.darknet.oop.jvm;

import me.darknet.oop.data.Memory;

public class NarrowOop {

    private final long address;

    public NarrowOop(long address) {
        this.address = address;
    }

    private long decodeNarrowOop() {
        long oopAddress = Universe.getNarrowOopBase() + (address << Universe.getNarrowOopShift());
        // check valid oop address
        if(oopAddress % 8 != 0) {
            throw new IllegalStateException("Oop address unaligned, p=" + Long.toHexString(oopAddress));
        }
        return oopAddress;
    }

    public OopHandle getOopHandle() {
        return new NativeOopHandle(decodeNarrowOop());
    }

}
