package me.darknet.oop.library;

public class Symbol {

    private final SymbolType type;
    private final String name;
    private final Section section;
    private final long size;
    private final long offset;

    public Symbol(SymbolType type, String name, Section section, long size, long offset) {
        this.type = type;
        this.name = name;
        this.section = section;
        this.size = size;
        this.offset = offset;
    }

    public SymbolType type() {
        return type;
    }

    public String name() {
        return name;
    }

    public Section section() {
        return section;
    }

    public long size() {
        return size;
    }

    public long offset() {
        return offset;
    }

    @Override
    public String toString() {
        return "Symbol{" +
                "type=" + type +
                ", name='" + name + '\'' +
                ", section=" + section +
                ", size=" + size +
                ", offset=" + offset +
                '}';
    }
}
