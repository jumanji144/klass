package me.darknet.oop.jvm;

import com.sun.management.HotSpotDiagnosticMXBean;
import me.darknet.oop.Oop;
import me.darknet.oop.Structs;
import me.darknet.oop.klass.Klass;

import java.lang.management.ManagementFactory;

public class NativeOopHandle extends Oop implements OopHandle {

    static final boolean compressedOops;
    static final boolean compressedKlass;

    public NativeOopHandle(long base) {
        super(base, Structs.oopDesc);
    }

    @Override
    public boolean isCompressedOop() {
        return compressedOops;
    }

    @Override
    public boolean isCompressedKlass() {
        return compressedKlass;
    }

    public Klass getKlassPointer() {
        if(compressedKlass) throw new IllegalStateException("Compressed oops is enabled");
        return Klass.of(struct.getAddress(base, "_metadata._klass"));
    }

    public NarrowKlass getNarrowKlass() {
        if(!compressedKlass) throw new IllegalStateException("Compressed oops is disabled");
        return new NarrowKlass(struct.getInt(base, "_metadata._compressed_klass") & 0xFFFF_FFFFL);
    }

    @Override
    public long getOopAddress() {
        return base;
    }

    @Override
    public long getMark() {
        return struct.getAddress(base, "_mark");
    }

    @Override
    public long getLong(long offset) {
        return unsafe.getLong(base + offset);
    }

    @Override
    public void putLong(long offset, long value) {
        unsafe.putLong(base + offset, value);
    }

    @Override
    public int getInt(long offset) {
        return unsafe.getInt(base + offset);
    }

    @Override
    public void putInt(long offset, int value) {
        unsafe.putInt(base + offset, value);
    }

    @Override
    public short getShort(long offset) {
        return unsafe.getShort(base + offset);
    }

    @Override
    public void putShort(long offset, short value) {
        unsafe.putShort(base + offset, value);
    }

    @Override
    public byte getByte(long offset) {
        return unsafe.getByte(base + offset);
    }

    @Override
    public void putByte(long offset, byte value) {
        unsafe.putByte(base + offset, value);
    }

    @Override
    public char getChar(long offset) {
        return unsafe.getChar(base + offset);
    }

    @Override
    public void putChar(long offset, char value) {
        unsafe.putChar(base + offset, value);
    }

    @Override
    public float getFloat(long offset) {
        return unsafe.getFloat(base + offset);
    }

    @Override
    public void putFloat(long offset, float value) {
        unsafe.putFloat(base + offset, value);
    }

    @Override
    public double getDouble(long offset) {
        return unsafe.getDouble(base + offset);
    }

    @Override
    public void putDouble(long offset, double value) {
        unsafe.putDouble(base + offset, value);
    }

    @Override
    public OopHandle getOop(long offset) {
        if(compressedOops) {
            return new NarrowOop(unsafe.getInt(base + offset) & 0xFFFF_FFFFL).getOopHandle();
        } else {
            return new NativeOopHandle(unsafe.getAddress(base + offset));
        }
    }

    @Override
    public void putOop(long offset, OopHandle value) {
        unsafe.putAddress(base + offset, value.getOopAddress());
    }

    static {
        compressedOops = Flag.getFlag("UseCompressedOops").getValue().getByte() == 1;
        compressedKlass = Flag.getFlag("UseCompressedClassPointers").getValue().getByte() == 1;
    }

}
