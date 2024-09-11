package me.darknet.oop.library;

import org.intellij.lang.annotations.MagicConstant;

public class Relocation {

    public static final int REL8 = 0;
    public static final int REL16 = 1;
    public static final int REL32 = 2;
    public static final int REL64 = 3;

    private final RelocationType type;
    @MagicConstant(intValues = {REL8, REL16, REL32, REL64})
    private final int size;
    private final Symbol symbol;
    private final String target;
    private final long address;
    private final long addend;

    public Relocation(RelocationType type, int size, Symbol symbol, String target, long address, long addend) {
        this.type = type;
        this.symbol = symbol;
        this.target = target;
        this.address = address;
        this.addend = addend;
        this.size = size;
    }

    public RelocationType type() {
        return type;
    }

    public int size() {
        return size;
    }
    public Symbol symbol() {
        return symbol;
    }
    public String target() {
        return target;
    }
    public long address() {
        return address;
    }
    public long addend() {
        return addend;
    }
}
