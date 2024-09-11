package me.darknet.oop.data;

import me.darknet.oop.jvm.BaseObtainStrategy;
import me.darknet.oop.types.Type;
import me.darknet.oop.types.Types;
import me.darknet.oop.util.Unsafe;
import me.darknet.oop.util.UnsafeAccessor;

public class Struct {

    private static final Unsafe unsafe = UnsafeAccessor.getUnsafe();
    private final Type type;

    public Struct(Type type) {
        this.type = type;
    }

    public Struct(String type) {
        this.type = Types.getType(type);
    }

    public long getOffset(Type type, String field) {
        long offset = Types.getOffset(type.name + "::" + field);
        if(offset == -1L) {
            if(type.superType != null) return getOffset(type.superType, field);
            else return -1L;
        }
        return offset;
    }

    public long getOffset(String field) {
        return getOffset(type, field);
    }

    public long getLong(long base, String field) {
        return unsafe.getLong(base + getOffset(field));
    }

    public long getAddress(long base, String field) {
        return unsafe.getAddress(base + getOffset(field));
    }

    public int getInt(long base, String field) {
        return unsafe.getInt(base + getOffset(field));
    }

    public short getShort(long base, String field) {
        return unsafe.getShort(base + getOffset(field));
    }

    public byte getByte(long base, String field) {
        return unsafe.getByte(base + getOffset(field));
    }

    public char getChar(long base, String field) {
        return unsafe.getChar(base + getOffset(field));
    }

    public void putLong(long base, String field, long value) {
        unsafe.putLong(base + getOffset(field), value);
    }

    public void putAddress(long base, String field, long value) {
        unsafe.putAddress(base + getOffset(field), value);
    }

    public void putInt(long base, String field, int value) {
        unsafe.putInt(base + getOffset(field), value);
    }

    public void putShort(long base, String field, short value) {
        unsafe.putShort(base + getOffset(field), value);
    }

    public void putByte(long base, String field, byte value) {
        unsafe.putByte(base + getOffset(field), value);
    }

    public void putChar(long base, String field, char value) {
        unsafe.putChar(base + getOffset(field), value);
    }

    public Field getStaticField(String field) {
        me.darknet.oop.types.Field f = Types.getField(type.name + "::" + field);
        if(!f.isStatic) return null;
        return new Field(BaseObtainStrategy.getStrategy().getBase() + f.offset, field);
    }

    public Field getField(long base, String field) {
        long offset = getOffset(field);
        if(offset == -1L) return null;
        return new Field(base + offset, field);
    }
}
