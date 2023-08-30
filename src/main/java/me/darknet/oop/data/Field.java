package me.darknet.oop.data;

import me.darknet.oop.util.Unsafe;
import me.darknet.oop.util.UnsafeAccessor;

public class Field {

    static Unsafe unsafe = UnsafeAccessor.getUnsafe();

    private final long address;
    private final String name;

    public Field(long address, String name) {
        this.address = address;
        this.name = name;
    }

    public long address() {
        return address;
    }

    public String name() {
        return name;
    }

    public void putLong(long value) {
        unsafe.putLong(address, value);
    }

    public void putAddress(long value) {
        unsafe.putAddress(address, value);
    }

    public void putInt(int value) {
        unsafe.putInt(address, value);
    }

    public void putShort(short value) {
        unsafe.putShort(address, value);
    }

    public void putByte(byte value) {
        unsafe.putByte(address, value);
    }

    public void putChar(char value) {
        unsafe.putChar(address, value);
    }

    public long getLong() {
        return unsafe.getLong(address);
    }

    public long getAddress() {
        return unsafe.getAddress(address);
    }

    public int getInt() {
        return unsafe.getInt(address);
    }

    public short getShort() {
        return unsafe.getShort(address);
    }

    public byte getByte() {
        return unsafe.getByte(address);
    }

    public char getChar() {
        return unsafe.getChar(address);
    }

    public long getLongAt(long offset) {
        return unsafe.getLong(address + offset);
    }

    public long getAddressAt(long offset) {
        return unsafe.getAddress(address + offset);
    }

    public int getIntAt(long offset) {
        return unsafe.getInt(address + offset);
    }

    public short getShortAt(long offset) {
        return unsafe.getShort(address + offset);
    }

    public byte getByteAt(long offset) {
        return unsafe.getByte(address + offset);
    }

    public char getCharAt(long offset) {
        return unsafe.getChar(address + offset);
    }

    public void putLongAt(long offset, long value) {
        unsafe.putLong(address + offset, value);
    }

    public static Field of(long address, String name) {
        return new Field(address, name);
    }

}
