package me.darknet.oop.util;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.security.ProtectionDomain;

public class Unsafe {

    public static Object theUnsafe;
    private static final MethodHandle getLong, getInt, getByte, getShort, getChar, getFloat, getDouble,
                                putLong, putInt, putByte, putShort, putChar, putFloat, putDouble,
                                getAddress, putAddress,
                                getLongObject, getIntObject, getByteObject, getShortObject, getCharObject,
                                putLongObject, putIntObject, putByteObject, putShortObject, putCharObject,
                                getFloatObject, getDoubleObject, putFloatObject, putDoubleObject,

                                getObject;
    private static final MethodHandle arrayBaseOffset, arrayIndexScale, staticFieldBase, staticFieldOffset;
    private static final MethodHandle objectFieldOffset;
    private static final MethodHandle copyMemory, copyMemoryObject;
    private static final MethodHandle addressSize;
    private static final MethodHandle allocateMemory, reallocateMemory, freeMemory;
    private static final MethodHandle pageSize;
    private static final MethodHandle defineAnonymousClass, ensureClassInitialized;

    public long getLong(long address) {
        try {
            return (long) getLong.invoke(theUnsafe, address);
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    public int getInt(long address) {
        try {
            return (int) getInt.invoke(theUnsafe, address);
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    public byte getByte(long address) {
        try {
            return (byte) getByte.invoke(theUnsafe, address);
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    public short getShort(long address) {
        try {
            return (short) getShort.invoke(theUnsafe, address);
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    public char getChar(long address) {
        try {
            return (char) getChar.invoke(theUnsafe, address);
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    public float getFloat(long address) {
        try {
            return (float) getFloat.invoke(theUnsafe, address);
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    public double getDouble(long address) {
        try {
            return (double) getDouble.invoke(theUnsafe, address);
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    public Object getObject(Object object, long offset) {
        try {
            return getObject.invoke(theUnsafe, object, offset);
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    public void putLong(long address, long value) {
        try {
            putLong.invoke(theUnsafe, address, value);
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    public void putInt(long address, int value) {
        try {
            putInt.invoke(theUnsafe, address, value);
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    public void putByte(long address, byte value) {
        try {
            putByte.invoke(theUnsafe, address, value);
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    public void putShort(long address, short value) {
        try {
            putShort.invoke(theUnsafe, address, value);
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    public void putChar(long address, char value) {
        try {
            putChar.invoke(theUnsafe, address, value);
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    public void putFloat(long address, float value) {
        try {
            putFloat.invoke(theUnsafe, address, value);
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    public void putDouble(long address, double value) {
        try {
            putDouble.invoke(theUnsafe, address, value);
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    public long getAddress(long address) {
        try {
            return (long) getAddress.invoke(theUnsafe, address);
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    public void putAddress(long address, long value) {
        try {
            putAddress.invoke(theUnsafe, address, value);
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    public long getLong(Object object, long offset) {
        try {
            return (long) getLongObject.invoke(theUnsafe, object, offset);
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    public int getInt(Object object, long offset) {
        try {
            return (int) getIntObject.invoke(theUnsafe, object, offset);
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    public byte getByte(Object object, long offset) {
        try {
            return (byte) getByteObject.invoke(theUnsafe, object, offset);
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    public short getShort(Object object, long offset) {
        try {
            return (short) getShortObject.invoke(theUnsafe, object, offset);
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    public char getChar(Object object, long offset) {
        try {
            return (char) getCharObject.invoke(theUnsafe, object, offset);
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    public float getFloat(Object object, long offset) {
        try {
            return (float) getFloatObject.invoke(theUnsafe, object, offset);
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    public double getDouble(Object object, long offset) {
        try {
            return (double) getDoubleObject.invoke(theUnsafe, object, offset);
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    public void putLong(Object object, long offset, long value) {
        try {
            putLongObject.invoke(theUnsafe, object, offset, value);
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    public void putInt(Object object, long offset, int value) {
        try {
            putIntObject.invoke(theUnsafe, object, offset, value);
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    public void putByte(Object object, long offset, byte value) {
        try {
            putByteObject.invoke(theUnsafe, object, offset, value);
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    public void putShort(Object object, long offset, short value) {
        try {
            putShortObject.invoke(theUnsafe, object, offset, value);
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    public void putChar(Object object, long offset, char value) {
        try {
            putCharObject.invoke(theUnsafe, object, offset, value);
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    public void putFloat(Object object, long offset, float value) {
        try {
            putFloatObject.invoke(theUnsafe, object, offset, value);
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    public void putDouble(Object object, long offset, double value) {
        try {
            putDoubleObject.invoke(theUnsafe, object, offset, value);
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    public long arrayBaseOffset(Class<?> clazz) {
        try {
            return (long) arrayBaseOffset.invoke(theUnsafe, clazz);
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    public long arrayIndexScale(Class<?> clazz) {
        try {
            return (long) arrayIndexScale.invoke(theUnsafe, clazz);
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    public long staticFieldOffset(Field field) {
        try {
            return (long) staticFieldOffset.invoke(theUnsafe, field);
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    public long objectFieldOffset(Field field) {
        try {
            return (long) objectFieldOffset.invoke(theUnsafe, field);
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    public Object staticFieldBase(Field field) {
        try {
            return staticFieldBase.invoke(theUnsafe, field);
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    public void copyMemory(long srcAddress, long destAddress, long length) {
        try {
            copyMemory.invoke(theUnsafe, srcAddress, destAddress, length);
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    public void copyMemory(Object src, long srcOffset, Object dest, long destOffset, long length) {
        try {
            copyMemoryObject.invoke(theUnsafe, src, srcOffset, dest, destOffset, length);
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    public long addressSize() {
        try {
            return (long) addressSize.invoke(theUnsafe);
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    public long allocateMemory(long bytes) {
        try {
            return (long) allocateMemory.invoke(theUnsafe, bytes);
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    public long reallocateMemory(long address, long bytes) {
        try {
            return (long) reallocateMemory.invoke(theUnsafe, address, bytes);
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    public void freeMemory(long address) {
        try {
            freeMemory.invoke(theUnsafe, address);
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    public void writeBytes(byte[] bytes, long address) {
        for (int i = 0; i < bytes.length; i++) {
            putByte(address + i, bytes[i]);
        }
    }

    public void clear(long base, long size) {
        for (long i = 0; i < size; i++) {
            putByte(base + i, (byte) 0);
        }
    }

    public long pageSize() {
        try {
            return (long) pageSize.invoke(theUnsafe);
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    public Class<?> defineAnonymousClass(Class<?> hostClass, byte[] bytes, Object[] cpPatches) {
        try {
            return (Class<?>) defineAnonymousClass.invoke(theUnsafe, hostClass, bytes, cpPatches);
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    public void ensureClassInitialized(Class<?> clazz) {
        try {
            ensureClassInitialized.invoke(theUnsafe, clazz);
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    static {
        try {
            Class<?> unsafeClass;
            try {
                unsafeClass = Class.forName("sun.misc.Unsafe");
            } catch (ClassNotFoundException e) {
                unsafeClass = Class.forName("jdk.internal.misc.Unsafe");
            }
            // obtain lookup instance
            Field field = unsafeClass.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            theUnsafe = field.get(null);
            MethodHandles.Lookup lookup = MethodHandles.lookup();

            // obtain method handles
            getLong = lookup.unreflect(unsafeClass.getMethod("getLong", long.class));
            getInt = lookup.unreflect(unsafeClass.getMethod("getInt", long.class));
            getByte = lookup.unreflect(unsafeClass.getMethod("getByte", long.class));
            getShort = lookup.unreflect(unsafeClass.getMethod("getShort", long.class));
            getChar = lookup.unreflect(unsafeClass.getMethod("getChar", long.class));
            getFloat = lookup.unreflect(unsafeClass.getMethod("getFloat", long.class));
            getDouble = lookup.unreflect(unsafeClass.getMethod("getDouble", long.class));

            putLong = lookup.unreflect(unsafeClass.getMethod("putLong", long.class, long.class));
            putInt = lookup.unreflect(unsafeClass.getMethod("putInt", long.class, int.class));
            putByte = lookup.unreflect(unsafeClass.getMethod("putByte", long.class, byte.class));
            putShort = lookup.unreflect(unsafeClass.getMethod("putShort", long.class, short.class));
            putChar = lookup.unreflect(unsafeClass.getMethod("putChar", long.class, char.class));
            putFloat = lookup.unreflect(unsafeClass.getMethod("putFloat", long.class, float.class));
            putDouble = lookup.unreflect(unsafeClass.getMethod("putDouble", long.class, double.class));

            getAddress = lookup.unreflect(unsafeClass.getMethod("getAddress", long.class));
            putAddress = lookup.unreflect(unsafeClass.getMethod("putAddress", long.class, long.class));

            getLongObject = lookup.unreflect(unsafeClass.getMethod("getLong", Object.class, long.class));
            getIntObject = lookup.unreflect(unsafeClass.getMethod("getInt", Object.class, long.class));
            getByteObject = lookup.unreflect(unsafeClass.getMethod("getByte", Object.class, long.class));
            getShortObject = lookup.unreflect(unsafeClass.getMethod("getShort", Object.class, long.class));
            getCharObject = lookup.unreflect(unsafeClass.getMethod("getChar", Object.class, long.class));
            getFloatObject = lookup.unreflect(unsafeClass.getMethod("getFloat", Object.class, long.class));
            getDoubleObject = lookup.unreflect(unsafeClass.getMethod("getDouble", Object.class, long.class));

            putLongObject = lookup.unreflect(unsafeClass.getMethod("putLong", Object.class, long.class, long.class));
            putIntObject = lookup.unreflect(unsafeClass.getMethod("putInt", Object.class, long.class, int.class));
            putByteObject = lookup.unreflect(unsafeClass.getMethod("putByte", Object.class, long.class, byte.class));
            putShortObject = lookup.unreflect(unsafeClass.getMethod("putShort", Object.class, long.class, short.class));
            putCharObject = lookup.unreflect(unsafeClass.getMethod("putChar", Object.class, long.class, char.class));
            putFloatObject = lookup.unreflect(unsafeClass.getMethod("putFloat", Object.class, long.class, float.class));
            putDoubleObject = lookup.unreflect(unsafeClass.getMethod("putDouble", Object.class, long.class, double.class));

            arrayBaseOffset = lookup.unreflect(unsafeClass.getMethod("arrayBaseOffset", Class.class));
            arrayIndexScale = lookup.unreflect(unsafeClass.getMethod("arrayIndexScale", Class.class));

            copyMemoryObject = lookup.unreflect(unsafeClass.getMethod("copyMemory", Object.class, long.class, Object.class, long.class, long.class));
            copyMemory = lookup.unreflect(unsafeClass.getMethod("copyMemory", long.class, long.class, long.class));

            addressSize = lookup.unreflect(unsafeClass.getMethod("addressSize"));

            staticFieldBase = lookup.unreflect(unsafeClass.getMethod("staticFieldBase", Field.class));
            staticFieldOffset = lookup.unreflect(unsafeClass.getMethod("staticFieldOffset", Field.class));

            objectFieldOffset = lookup.unreflect(unsafeClass.getMethod("objectFieldOffset", Field.class));

            getObject = lookup.unreflect(unsafeClass.getMethod("getObject", Object.class, long.class));

            allocateMemory = lookup.unreflect(unsafeClass.getMethod("allocateMemory", long.class));
            reallocateMemory = lookup.unreflect(unsafeClass.getMethod("reallocateMemory", long.class, long.class));
            freeMemory = lookup.unreflect(unsafeClass.getMethod("freeMemory", long.class));

            pageSize = lookup.unreflect(unsafeClass.getMethod("pageSize"));

            defineAnonymousClass = lookup.unreflect(unsafeClass.getMethod("defineAnonymousClass", Class.class, byte[].class, Object[].class));

            ensureClassInitialized = lookup.unreflect(unsafeClass.getMethod("ensureClassInitialized", Class.class));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Unsafe(Object theUnsafe) {
        Unsafe.theUnsafe = theUnsafe;
    }
}
