package me.darknet.oop.jvm;

import me.darknet.oop.klass.Klass;
import me.darknet.oop.klass.NarrowKlass;

public interface OopHandle {

    boolean isCompressedOop();
    boolean isCompressedKlass();

    Klass getKlassPointer();

    NarrowKlass getNarrowKlass();

    long getOopAddress();

    default Klass getKlass() {
        return isCompressedKlass() ? getNarrowKlass().getKlass() : getKlassPointer();
    }

    long getMark();

    long getLong(long offset);
    void putLong(long offset, long value);

    int getInt(long offset);
    void putInt(long offset, int value);

    short getShort(long offset);
    void putShort(long offset, short value);

    byte getByte(long offset);
    void putByte(long offset, byte value);

    char getChar(long offset);
    void putChar(long offset, char value);

    float getFloat(long offset);
    void putFloat(long offset, float value);

    double getDouble(long offset);
    void putDouble(long offset, double value);

    OopHandle getOop(long offset);
    void putOop(long offset, OopHandle value);

}
