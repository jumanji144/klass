package me.darknet.oop;

import me.darknet.oop.util.Unsafe;
import me.darknet.oop.util.UnsafeAccessor;
import me.darknet.oop.util.Venuzdonoa;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class VMStructs {
    private static final MethodHandle findNative;

    private static long getOffset(String field) {
        try {
            return (long) findNative.invoke(null, field);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private static String getString(long address) {
        Unsafe unsafe = UnsafeAccessor.getUnsafe();
        StringBuilder sb = new StringBuilder();
        while(true) {
            byte b = unsafe.getByte(address++);
            if(b == 0) {
                break;
            }
            sb.append((char) b);
        }
        return sb.toString();
    }

    public static class Field {
        public final String name;
        public final String parent;
        public final String type;
        public final boolean isStatic;
        public final long offset;
        public final long address;

        public Field(String name, String parent, String type, boolean isStatic, long offset, long address) {
            this.name = name;
            this.parent = parent;
            this.type = type;
            this.isStatic = isStatic;
            this.offset = offset;
            this.address = address;
        }
    }

    public static Map<String, Field> readVMTypes() {
        Unsafe unsafe = UnsafeAccessor.getUnsafe();

        long structEntryTypeNameOffset;
        long structEntryFieldNameOffset;
        long structEntryTypeStringOffset;
        long structEntryIsStaticOffset;
        long structEntryOffsetOffset;
        long structEntryAddressOffset;
        long structEntryArrayStride;

        structEntryTypeNameOffset     = unsafe.getLong(getOffset("gHotSpotVMStructEntryTypeNameOffset"));
        structEntryFieldNameOffset    = unsafe.getLong(getOffset("gHotSpotVMStructEntryFieldNameOffset"));
        structEntryTypeStringOffset   = unsafe.getLong(getOffset("gHotSpotVMStructEntryTypeStringOffset"));
        structEntryIsStaticOffset     = unsafe.getLong(getOffset("gHotSpotVMStructEntryIsStaticOffset"));
        structEntryOffsetOffset       = unsafe.getLong(getOffset("gHotSpotVMStructEntryOffsetOffset"));
        structEntryAddressOffset      = unsafe.getLong(getOffset("gHotSpotVMStructEntryAddressOffset"));
        structEntryArrayStride        = unsafe.getLong(getOffset("gHotSpotVMStructEntryArrayStride"));

        long entryAddress = getOffset("gHotSpotVMStructs");

        entryAddress = unsafe.getAddress(entryAddress);

        Map<String, Field> gHotSpotVMTypes = new HashMap<>();

        long fieldNamesAddress = 0;
        do {
            fieldNamesAddress = unsafe.getAddress(entryAddress + structEntryFieldNameOffset);
            if(fieldNamesAddress != 0) {
                String name = getString(fieldNamesAddress);
                String type = getString(unsafe.getAddress(entryAddress + structEntryTypeNameOffset));
                String typeString = "<opaque>";

                long typeStringAddress = unsafe.getAddress(entryAddress + structEntryTypeStringOffset);
                if(typeStringAddress != 0) {
                    typeString = getString(typeStringAddress);
                }

                boolean isStatic = unsafe.getInt(entryAddress + structEntryIsStaticOffset) != 0;
                long offset = 0;
                long staticFieldAddress = 0;
                if(isStatic) {
                    staticFieldAddress = unsafe.getAddress(entryAddress + structEntryAddressOffset);
                } else {
                    offset = unsafe.getLong(entryAddress + structEntryOffsetOffset);
                }
                gHotSpotVMTypes.put(type + "::" + name, new Field(name, type, typeString, isStatic, offset, staticFieldAddress));
            }
            entryAddress += structEntryArrayStride;
        } while (fieldNamesAddress != 0);

        return gHotSpotVMTypes;
    }

    public static Map<String, Long> readVMLongConstants() {
        Map<String, Long> gHotSpotVMLongConstants = new HashMap<>();

        Unsafe unsafe = UnsafeAccessor.getUnsafe();

        long longConstantEntryNameOffset;
        long longConstantEntryValueOffset;
        long longConstantEntryArrayStride;

        longConstantEntryNameOffset = unsafe.getLong(getOffset("gHotSpotVMLongConstantEntryNameOffset"));
        longConstantEntryValueOffset = unsafe.getLong(getOffset("gHotSpotVMLongConstantEntryValueOffset"));
        longConstantEntryArrayStride = unsafe.getAddress(getOffset("gHotSpotVMLongConstantEntryArrayStride"));

        long entryAddress = getOffset("gHotSpotVMLongConstants");

        entryAddress = unsafe.getAddress(entryAddress);

        long nameAddress = 0;
        do {
            nameAddress = unsafe.getAddress(entryAddress + longConstantEntryNameOffset);
            if(nameAddress != 0) {
                String name = getString(nameAddress);
                long value = unsafe.getLong(entryAddress + longConstantEntryValueOffset);
                gHotSpotVMLongConstants.put(name, value);
            }
            entryAddress += longConstantEntryArrayStride;
        } while (nameAddress != 0);

        return gHotSpotVMLongConstants;
    }

    static {
        try {
            Method findNativeMethod = ClassLoader.class.getDeclaredMethod("findNative", ClassLoader.class, String.class);
            findNative = Venuzdonoa.makeMethodAccessor(findNativeMethod);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

}
