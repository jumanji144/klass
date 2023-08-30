package me.darknet.oop.library;

public class Symbol {

    private final SymbolType type;
    private final String name;
    private final long offset;

    public Symbol(SymbolType type, String name, long offset) {
        this.type = type;
        this.name = name;
        this.offset = offset;
    }

    public SymbolType type() {
        return type;
    }

    public String name() {
        return name;
    }

    public long offset() {
        return offset;
    }

    @Override
    public String toString() {
        return "Symbol{" +
                "type=" + type +
                ", name='" + name + '\'' +
                ", offset=" + Long.toHexString(offset) +
                '}';
    }
}
