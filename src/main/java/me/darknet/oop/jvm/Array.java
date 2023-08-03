package me.darknet.oop.jvm;

import me.darknet.oop.Offsets;
import me.darknet.oop.Structs;
import me.darknet.oop.data.Struct;
import me.darknet.oop.types.Type;
import me.darknet.oop.types.Types;
import me.darknet.oop.util.Unsafe;
import me.darknet.oop.util.UnsafeAccessor;

public class Array {

    private static final Unsafe unsafe = UnsafeAccessor.getUnsafe();
    private final long elementBase;
    private final int elementSize;
    private final int length;

    public Array(long base, Type type) {
        this.elementSize = type.getSize();
        this.length = unsafe.getInt(base);
        this.elementBase = base + Types.getOffset("Array<" + type.name + ">::_data");
    }

    public long getLong(int index) {
        return unsafe.getLong(elementBase + (long) index * elementSize);
    }

    public long getAddress(int index) {
        return unsafe.getAddress(elementBase + (long) index * elementSize);
    }

    public int getInt(int index) {
        return unsafe.getInt(elementBase + (long) index * elementSize);
    }

    public short getShort(int index) {
        return unsafe.getShort(elementBase + (long) index * elementSize);
    }

    public byte getByte(int index) {
        return unsafe.getByte(elementBase + (long) index * elementSize);
    }

    public char getChar(int index) {
        return unsafe.getChar(elementBase + (long) index * elementSize);
    }

    public void putLong(int index, long value) {
        unsafe.putLong(elementBase + (long) index * elementSize, value);
    }

    public void putAddress(int index, long value) {
        unsafe.putAddress(elementBase + (long) index * elementSize, value);
    }

    public void putInt(int index, int value) {
        unsafe.putInt(elementBase + (long) index * elementSize, value);
    }

    public void putShort(int index, short value) {
        unsafe.putShort(elementBase + (long) index * elementSize, value);
    }

    public void putByte(int index, byte value) {
        unsafe.putByte(elementBase + (long) index * elementSize, value);
    }

    public void putChar(int index, char value) {
        unsafe.putChar(elementBase + (long) index * elementSize, value);
    }

    public long[] getLongs() {
        long[] array = new long[length];
        for (int i = 0; i < length; i++) {
            array[i] = getLong(i);
        }
        return array;
    }

    public int[] getInts() {
        int[] array = new int[length];
        for (int i = 0; i < length; i++) {
            array[i] = getInt(i);
        }
        return array;
    }

    public short[] getShorts() {
        short[] array = new short[length];
        for (int i = 0; i < length; i++) {
            array[i] = getShort(i);
        }
        return array;
    }

    public byte[] getBytes() {
        byte[] array = new byte[length];
        for (int i = 0; i < length; i++) {
            array[i] = getByte(i);
        }
        return array;
    }

    public int length() {
        return length;
    }
}
