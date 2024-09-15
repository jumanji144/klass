package me.darknet.oop;

import me.darknet.oop.util.Unsafe;
import me.darknet.oop.util.UnsafeAccessor;
import me.darknet.oop.util.Util;
import me.darknet.oop.util.Venuzdonoa;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.*;

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

    public static class Type {
        public final String name;
        public final String parent;
        public final long size;
        public final boolean isOop;
        public final boolean isInteger;
        public final boolean isUnsigned;

        public Type(String name, String parent, long size, boolean isOop, boolean isInteger, boolean isUnsigned) {
            this.name = name;
            this.parent = parent;
            this.size = size;
            this.isOop = isOop;
            this.isInteger = isInteger;
            this.isUnsigned = isUnsigned;
        }
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

    public static Map<String, Field> readVMStructs() {
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

        Map<String, Field> gHotSpotVMTypes = new TreeMap<>();

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

    public static Map<String, Type> readVMTypes() {
        Unsafe unsafe = UnsafeAccessor.getUnsafe();

        long typeEntryTypeNameOffset;
        long typeEntryParentNameOffset;
        long typeEntrySizeOffset;
        long typeEntryIsOopTypeOffset;
        long typeEntryIsIntegerTypeOffset;
        long typeEntryIsUnsignedOffset;
        long typeEntryArrayStride;

        typeEntryTypeNameOffset         = unsafe.getLong(getOffset("gHotSpotVMTypeEntryTypeNameOffset"));
        typeEntryParentNameOffset       = unsafe.getLong(getOffset("gHotSpotVMTypeEntrySuperclassNameOffset"));
        typeEntrySizeOffset             = unsafe.getLong(getOffset("gHotSpotVMTypeEntrySizeOffset"));
        typeEntryIsOopTypeOffset        = unsafe.getLong(getOffset("gHotSpotVMTypeEntryIsOopTypeOffset"));
        typeEntryIsIntegerTypeOffset    = unsafe.getLong(getOffset("gHotSpotVMTypeEntryIsIntegerTypeOffset"));
        typeEntryIsUnsignedOffset       = unsafe.getLong(getOffset("gHotSpotVMTypeEntryIsUnsignedOffset"));
        typeEntryArrayStride            = unsafe.getLong(getOffset("gHotSpotVMTypeEntryArrayStride"));

        long entryAddress = getOffset("gHotSpotVMTypes");

        entryAddress = unsafe.getAddress(entryAddress);

        Map<String, Type> gHotSpotVMTypes = new TreeMap<>();

        long typeNamesAddress = 0;
        do {
            typeNamesAddress = unsafe.getAddress(entryAddress + typeEntryTypeNameOffset);
            if(typeNamesAddress != 0) {
                String name = getString(typeNamesAddress);
                String parentName = null;
                long parentNameAddress = unsafe.getAddress(entryAddress + typeEntryParentNameOffset);
                if(parentNameAddress != 0) {
                    parentName = getString(parentNameAddress);
                }
                long size = unsafe.getLong(entryAddress + typeEntrySizeOffset);
                boolean isOop = unsafe.getInt(entryAddress + typeEntryIsOopTypeOffset) != 0;
                boolean isInteger = unsafe.getInt(entryAddress + typeEntryIsIntegerTypeOffset) != 0;
                boolean isUnsigned = unsafe.getInt(entryAddress + typeEntryIsUnsignedOffset) != 0;
                gHotSpotVMTypes.put(name, new Type(name, parentName, size, isOop, isInteger, isUnsigned));

                // add pointer type
                gHotSpotVMTypes.put(name + "*", new Type(name + "*", null, unsafe.addressSize(),
                        true, false, false));
            }
            entryAddress += typeEntryArrayStride;
        } while (typeNamesAddress != 0);

        return gHotSpotVMTypes;
    }

    public static Map<String, Long> readVMLongConstants() {
        Map<String, Long> gHotSpotVMLongConstants = new TreeMap<>();

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

    public static Map<String, Long> readVMIntConstants() {
        Map<String, Long> gHotSpotVMIntConstants = new TreeMap<>();

        Unsafe unsafe = UnsafeAccessor.getUnsafe();

        long intConstantEntryNameOffset;
        long intConstantEntryValueOffset;
        long intConstantEntryArrayStride;

        intConstantEntryNameOffset = unsafe.getLong(getOffset("gHotSpotVMIntConstantEntryNameOffset"));
        intConstantEntryValueOffset = unsafe.getLong(getOffset("gHotSpotVMIntConstantEntryValueOffset"));
        intConstantEntryArrayStride = unsafe.getAddress(getOffset("gHotSpotVMIntConstantEntryArrayStride"));

        long entryAddress = getOffset("gHotSpotVMIntConstants");

        entryAddress = unsafe.getAddress(entryAddress);

        long nameAddress = 0;
        do {
            nameAddress = unsafe.getAddress(entryAddress + intConstantEntryNameOffset);
            if(nameAddress != 0) {
                String name = getString(nameAddress);
                long value = unsafe.getInt(entryAddress + intConstantEntryValueOffset);
                gHotSpotVMIntConstants.put(name, value);
            }
            entryAddress += intConstantEntryArrayStride;
        } while (nameAddress != 0);

        return gHotSpotVMIntConstants;
    }

    private static String printType(List<String> printedTypes, Map<String, Type> gHotSpotVMTypes, Type value) {
        if(printedTypes.contains(value.name)) return "";
        printedTypes.add(value.name);

        StringBuilder builder = new StringBuilder();
        if(value.parent != null
                && !printedTypes.contains(value.parent)
                && gHotSpotVMTypes.containsKey(value.parent)) {
            builder.append(printType(printedTypes, gHotSpotVMTypes, gHotSpotVMTypes.get(value.parent)));
        }
        String name = value.name;
        if(name.contains(" ")) name = '"' + name + '"'; // 'type "Foo Bar" ...'
        return builder.append("type ")
                .append(name).append(" ")
                .append(value.parent).append(" ")
                .append(value.isOop).append(" ")
                .append(value.isInteger).append(" ")
                .append(value.isUnsigned).append(" ")
                .append(value.size)
                .append("\n").toString();
    }

    public static String dumpVMStaticOffsets(long baseAddress) {
        Unsafe unsafe = UnsafeAccessor.getUnsafe();
        Map<String, Field> gHotSpotVMFields = readVMStructs();

        StringBuilder builder = new StringBuilder();
        for (Field value : gHotSpotVMFields.values()) {
            if(!value.isStatic) continue;
            long offset = value.address - baseAddress;

            String type = value.type;
            if(type.contains(" ")) type = '"' + type + '"'; // 'type "Foo Bar" ...'

            builder.append("field ")
                    .append(value.parent).append(" ")
                    .append(value.name).append(" ")
                    .append(type).append(" ")
                    .append("true ")
                    .append(0).append(" ")
                    .append("0x").append(Long.toHexString(offset))
                    .append("\n");
        }

        return builder.toString();
    }

    public static String dumpVMStructs() {
        Unsafe unsafe = UnsafeAccessor.getUnsafe();
        Map<String, Type> gHotSpotVMTypes = readVMTypes();
        Map<String, Field> gHotSpotVMFields = readVMStructs();
        Map<String, Long> gHotSpotVMIntConstants = readVMIntConstants();
        Map<String, Long> gHotSpotVMLongConstants = readVMLongConstants();

        List<String> printedTypes = new ArrayList<>();

        StringBuilder builder = new StringBuilder();
        for (Type value : gHotSpotVMTypes.values()) {
            if(printedTypes.contains(value.name)) continue;
            builder.append(printType(printedTypes, gHotSpotVMTypes, value));
        }
        for (Field value : gHotSpotVMFields.values()) {
            if(value.isStatic) continue;
            String type = value.type;
            if(type.contains(" ")) type = '"' + type + '"'; // 'type "Foo Bar" ...'
            builder.append("field ")
                    .append(value.parent).append(" ")
                    .append(value.name).append(" ")
                    .append(type).append(" ")
                    .append("false ")
                    .append(value.offset).append(" ")
                    .append("0x0")
            .append("\n");
        }
        for (Field value : gHotSpotVMFields.values()) {
            Type type = gHotSpotVMTypes.get(value.type);
            if(type == null
                    || (type.size != 1 && type.size != 2 && type.size != 4 && type.size != 8)
                    || value.address == 0) continue;
            builder.append("constant ")
                    .append(value.parent).append(" ")
                    .append(value.name).append(" ")
                    .append(value.type).append(" ");
            // get the value
            long val = 0;
            switch ((int) type.size) {
                case 1:
                    val = unsafe.getByte(value.address);
                    if(type.isUnsigned) val &= 0xFF;
                    break;
                case 2:
                    val = unsafe.getShort(value.address);
                    if(type.isUnsigned) val &= 0xFFFF;
                    break;
                case 4:
                    val = unsafe.getInt(value.address);
                    if(type.isUnsigned) val &= 0xFFFFFFFFL;
                    break;
                case 8:
                    val = unsafe.getLong(value.address);
                    break;
            }
            builder.append("0x").append(Long.toHexString(val)).append("\n");
        }
        for (Map.Entry<String, Long> entry : gHotSpotVMIntConstants.entrySet()) {
            builder.append("constant ")
                    .append("null").append(" ")
                    .append(entry.getKey()).append(" ")
                    .append("int").append(" ")
                    .append("0x").append(Long.toHexString(entry.getValue()))
            .append("\n");
        }
        for (Map.Entry<String, Long> entry : gHotSpotVMLongConstants.entrySet()) {
            builder.append("constant ")
                    .append("null").append(" ")
                    .append(entry.getKey()).append(" ")
                    .append("long").append(" ")
                    .append("0x").append(Long.toHexString(entry.getValue()))
            .append("\n");
        }
        return builder.toString();
    }

    public static void main(String[] args) {
        System.out.println("Generating vm struct dump...");

        String dump = dumpVMStructs();

        System.out.println("Saving vm struct dump...");
        File file = new File("vmstructs_" + Util.getVMType() + "_" + Util.getJavaVersion() + ".structs");
        try {
            Files.write(file.toPath(), dump.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
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
