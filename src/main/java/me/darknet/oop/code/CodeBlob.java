package me.darknet.oop.code;

public class CodeBlob {

    private final String name;
    private final int size;
    private final int type;
    private final long address;

    public CodeBlob(String name, int size, int type, long address) {
        this.name = name;
        this.size = size;
        this.type = type;
        this.address = address;
    }

    public String name() {
        return name;
    }

    public int size() {
        return size;
    }

    public int type() {
        return type;
    }

    public long address() {
        return address;
    }

}
