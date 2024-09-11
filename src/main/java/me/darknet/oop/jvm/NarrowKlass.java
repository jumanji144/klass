package me.darknet.oop.jvm;

import me.darknet.oop.klass.Klass;

public class NarrowKlass {

    private final long address;

    public NarrowKlass(long address) {
        this.address = address;
    }

    private long decodeNarrowKlass() {
        long klassAddress = Universe.getNarrowKlassBase() + (address << Universe.getNarrowKlassShift());
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
