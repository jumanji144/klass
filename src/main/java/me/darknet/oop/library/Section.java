package me.darknet.oop.library;

public class Section {

    private final String name;
    private final long address;
    private final long size;
    private final long offset;
    private final byte[] data;

    public Section(String name, long address, long size, long offset, byte[] data) {
        this.name = name;
        this.address = address;
        this.size = size;
        this.offset = offset;
        this.data = data;
    }

    public String name() {
        return name;
    }

    public long address() {
        return address;
    }

    public long size() {
        return size;
    }

    public long offset() {
        return offset;
    }

    public byte[] data() {
        return data;
    }

    public boolean contains(long address) {
        return address >= this.address && address < this.address + size;
    }

}
