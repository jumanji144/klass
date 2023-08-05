package me.darknet.oop.jvm;

import me.darknet.oop.Structs;
import me.darknet.oop.klass.Klass;
import me.darknet.oop.klass.NarrowKlass;
import me.darknet.oop.util.Unsafe;
import me.darknet.oop.util.UnsafeAccessor;

public class ProxyOopHandle implements OopHandle {

    private static final Unsafe unsafe = UnsafeAccessor.getUnsafe();
    private final Object oop;

    public ProxyOopHandle(Object oop) {
        this.oop = oop;
    }

    @Override
    public boolean isCompressedOop() {
        return NativeOopHandle.compressedOops;
    }

    @Override
    public boolean isCompressedKlass() {
        return NativeOopHandle.compressedKlass;
    }

    @Override
    public Klass getKlassPointer() {
        if(NativeOopHandle.compressedKlass) throw new IllegalStateException("Compressed oops is enabled");
        return Klass.of(unsafe.getLong(oop, Structs.oopDesc.getOffset("_metadata._klass")));
    }

    @Override
    public NarrowKlass getNarrowKlass() {
        if(!NativeOopHandle.compressedKlass) throw new IllegalStateException("Compressed oops is disabled");
        return new NarrowKlass(
                unsafe.getInt(oop, Structs.oopDesc.getOffset("_metadata._compressed_klass")) & 0xFFFF_FFFFL
        );
    }

    @Override
    public long getOopAddress() {
        throw new UnsupportedOperationException("ProxyOopHandle does not support getOopAddress()");
    }

    @Override
    public long getMark() {
        return unsafe.getLong(oop, Structs.oopDesc.getOffset("_mark"));
    }

    public long getLong(long offset) {
        return unsafe.getLong(oop, offset);
    }

    @Override
    public void putLong(long offset, long value) {
        unsafe.putLong(oop, offset, value);
    }

    @Override
    public int getInt(long offset) {
        return unsafe.getInt(oop, offset);
    }

    @Override
    public void putInt(long offset, int value) {
        unsafe.putInt(oop, offset, value);
    }

    @Override
    public short getShort(long offset) {
        return unsafe.getShort(oop, offset);
    }

    @Override
    public void putShort(long offset, short value) {
        unsafe.putShort(oop, offset, value);
    }

    @Override
    public byte getByte(long offset) {
        return unsafe.getByte(oop, offset);
    }

    @Override
    public void putByte(long offset, byte value) {
        unsafe.putByte(oop, offset, value);
    }

    @Override
    public char getChar(long offset) {
        return unsafe.getChar(oop, offset);
    }

    @Override
    public void putChar(long offset, char value) {
        unsafe.putChar(oop, offset, value);
    }

    @Override
    public float getFloat(long offset) {
        return unsafe.getFloat(oop, offset);
    }

    @Override
    public void putFloat(long offset, float value) {
        unsafe.putFloat(oop, offset, value);
    }

    @Override
    public double getDouble(long offset) {
        return unsafe.getDouble(oop, offset);
    }

    @Override
    public void putDouble(long offset, double value) {
        unsafe.putDouble(oop, offset, value);
    }

    @Override
    public OopHandle getOop(long offset) {
        if(NativeOopHandle.compressedOops) {
            return new NarrowOop(unsafe.getInt(oop, offset) & 0xFFFF_FFFFL).getOopHandle();
        } else {
            return new NativeOopHandle(unsafe.getLong(oop, offset));
        }
    }

    @Override
    public void putOop(long offset, OopHandle value) {
        unsafe.putLong(oop, offset, value.getOopAddress());
    }

}
