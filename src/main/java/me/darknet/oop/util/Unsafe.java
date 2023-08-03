package me.darknet.oop.util;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class Unsafe {

    public static Object theUnsafe;
    private static final MethodHandle getLong, getInt, getByte, getShort, getChar, getFloat, getDouble,
                                putLong, putInt, putByte, putShort, putChar, putFloat, putDouble,
                                getAddress, putAddress,
                                getLongObject, getIntObject;
    private static final MethodHandle arrayBaseOffset, arrayIndexScale;
    private static final MethodHandle copyMemory, copyMemoryObject;
    private static final MethodHandle addressSize;

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

    static {
        try {
            Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
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

            arrayBaseOffset = lookup.unreflect(unsafeClass.getMethod("arrayBaseOffset", Class.class));
            arrayIndexScale = lookup.unreflect(unsafeClass.getMethod("arrayIndexScale", Class.class));

            copyMemoryObject = lookup.unreflect(unsafeClass.getMethod("copyMemory", Object.class, long.class, Object.class, long.class, long.class));
            copyMemory = lookup.unreflect(unsafeClass.getMethod("copyMemory", long.class, long.class, long.class));

            addressSize = lookup.unreflect(unsafeClass.getMethod("addressSize"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Unsafe(Object theUnsafe) {
        Unsafe.theUnsafe = theUnsafe;
    }
}
