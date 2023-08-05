package me.darknet.oop.klass;

import me.darknet.oop.data.Memory;

public class NarrowKlass {

    private final long address;

    public NarrowKlass(long address) {
        this.address = address;
    }

    private long decodeNarrowKlass() {
        long klassAddress = Memory.narrowKlassBase + (address << Memory.narrowKlassShift);
        // check valid klass address
        if(klassAddress % 8 != 0) {
            throw new IllegalStateException("Klass address unaligned, p=" + Long.toHexString(klassAddress));
        }
        return klassAddress;
    }

    public Klass getKlass() {
        return Klass.of(decodeNarrowKlass());
    }

}
