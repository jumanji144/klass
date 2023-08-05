package me.darknet.oop.klass;

import me.darknet.oop.Oop;
import me.darknet.oop.Structs;
import me.darknet.oop.Dumpable;
import me.darknet.oop.jvm.Array;
import me.darknet.oop.types.Types;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ConstantPool extends Oop implements Dumpable, ConstantPoolTags {

    private final Array tags;
    protected final long dataBase;
    protected final int elementSize;
    protected Map<String, Integer> utf8SymbolMap;
    protected Map<String, Integer> classSymbolMap;
    protected Map<Integer, Integer> refCache;
    protected Map<Integer, Integer> fieldRefCache;
    protected Map<Integer, Integer> methodRefCache;
    protected Map<Integer, Integer> refString;

    public ConstantPool(long base) {
        super(base, Structs.constantPool);
        this.tags = new Array(struct.getAddress(base, "_tags"), Types.getType("u1"));
        this.dataBase = base + Types.getSize("ConstantPool");
        this.elementSize = Types.getSize("oop");
    }

    long index(int index) {
        return dataBase + (long) index * elementSize;
    }

    public int majorVersion() {
        long version = struct.getOffset("_major_version");
        if(version == 0) return 0;
        return struct.getInt(base, "_major_version");
    }

    public int minorVersion() {
        long version = struct.getOffset("_minor_version");
        if(version == 0) return 0;
        return struct.getInt(base, "_minor_version");
    }

    public int getLength() {
        return struct.getInt(base, "_length");
    }

    public byte getTag(int index) {
        return tags.getByte(index);
    }

    public byte[] getTags() {
        return tags.getBytes();
    }

    public Symbol getSymbol(int index) {
        long address = unsafe.getAddress(index(index));
        return Symbol.of(address);
    }

    public String getString(int index) {
        return getSymbol(index).asString();
    }

    public Klass getKlass(int index) {
        long address = unsafe.getAddress(index(index));
        return Klass.of(address);
    }

    public int getInt(int index) {
        return unsafe.getInt(index(index));
    }

    public long getLong(int index) {
        return unsafe.getLong(index(index));
    }

    public float getFloat(int index) {
        return unsafe.getFloat(index(index));
    }

    public double getDouble(int index) {
        return unsafe.getDouble(index(index));
    }

    public short lowerShort(int value) {
        return (short) ((short) value & 0xffff);
    }

    public short higherShort(int value) {
        return (short) ((short) (value >> 16) & 0xffff);
    }

    public int getUtf8SymbolIndex(String symbol) {
        return utf8SymbolMap.get(symbol);
    }

    public int getClassSymbolIndex(String symbol) {
        return classSymbolMap.get(symbol);
    }

    public ConstantPoolCache getCache() {
        return new ConstantPoolCache(struct.getAddress(base, "_cache"));
    }

    public int getRefIndex(int index) {
        return refCache.get(index);
    }

    public int getStringIndex(int index) {
        return refString.get(index);
    }

    public Map<String, Integer> getUtf8SymbolMap() {
        return utf8SymbolMap;
    }

    public Map<String, Integer> getClassSymbolMap() {
        return classSymbolMap;
    }

    public Map<Integer, Integer> getRefCache() {
        return refCache;
    }

    public Map<Integer, Integer> getRefString() {
        return refString;
    }

    public Map<Integer, Integer> getFieldRefCache() {
        return fieldRefCache;
    }

    public Map<Integer, Integer> getMethodRefCache() {
        return methodRefCache;
    }

    @Override
    public void dump(DataOutputStream out) throws IOException {
        out.writeShort(getLength());

        // initialize utf8SymbolMap
        utf8SymbolMap = new HashMap<>();
        classSymbolMap = new HashMap<>();
        refCache = new HashMap<>();
        refString = new HashMap<>();
        fieldRefCache = new HashMap<>();
        methodRefCache = new HashMap<>();
        int refCount = 0;
        int fieldRefCount = 1;
        int methodRefCount = 1;
        int stringCount = 0;
        for (int i = 1; i < getLength(); i++) {
            byte type = getTag(i);
            switch (type) {
                case JVM_CONSTANT_Utf8: {
                    String symbol = getString(i);
                    utf8SymbolMap.put(symbol, i);
                    break;
                }
                case JVM_CONSTANT_Class:
                case JVM_CONSTANT_UnresolvedClass:
                case JVM_CONSTANT_UnresolvedClassInError: {
                    int klassIndex = getInt(i);
                    String symbol;
                    if (klassIndex < 0 || klassIndex >= 0xFFFFFFF) {
                        // address is cached
                        if (type == JVM_CONSTANT_UnresolvedClass || type == JVM_CONSTANT_UnresolvedClassInError) {
                            long address = unsafe.getAddress(index(i)) - 1;
                            symbol = new Symbol(address).asString();
                        } else {
                            symbol = getKlass(i).getName();
                        }
                    } else {
                        // address is not cached
                        symbol = getString(higherShort(klassIndex));
                    }
                    classSymbolMap.put(symbol, i);
                    break;
                }
                case JVM_CONSTANT_Fieldref:
                case JVM_CONSTANT_Methodref:
                case JVM_CONSTANT_InterfaceMethodref: {
                    switch (type) {
                        case JVM_CONSTANT_Fieldref:
                            fieldRefCache.put(fieldRefCount++ * 256, i);
                            break;
                        case JVM_CONSTANT_Methodref:
                            methodRefCache.put(methodRefCount++ * 256, i);
                            break;
                    }
                    refCache.put(refCount++ * 256, i);
                    break;
                }
                case JVM_CONSTANT_String:
                case JVM_CONSTANT_MethodHandle:
                case JVM_CONSTANT_MethodType:
                    refString.put(stringCount++, i);
                    break;
                case JVM_CONSTANT_Long:
                case JVM_CONSTANT_Double:
                    i++;
                    break;
            }
        }

        System.out.println("utf8SymbolMap: " + Arrays.toString(utf8SymbolMap.entrySet().toArray()));
        System.out.println("classSymbolMap: " + Arrays.toString(classSymbolMap.entrySet().toArray()));
        System.out.println("refCache: " + Arrays.toString(refCache.entrySet().toArray()));
        System.out.println("refString: " + Arrays.toString(refString.entrySet().toArray()));
        System.out.println("fieldRefCache: " + Arrays.toString(fieldRefCache.entrySet().toArray()));
        System.out.println("methodRefCache: " + Arrays.toString(methodRefCache.entrySet().toArray()));

        // write constant pool
        for (int i = 1; i < getLength(); i++) {
            byte tag = getTag(i);
            switch (tag) {
                case JVM_CONSTANT_Utf8: {
                    out.writeByte(tag);
                    Symbol symbol = getSymbol(i);
                    out.writeShort(symbol.getLength());
                    out.write(symbol.asBytes());
                    break;
                }
                case JVM_CONSTANT_Integer: {
                    out.writeByte(tag);
                    out.writeInt(getInt(i));
                    break;
                }
                case JVM_CONSTANT_Float: {
                    out.writeByte(tag);
                    out.writeFloat(getFloat(i));
                    break;
                }
                case JVM_CONSTANT_Long: {
                    out.writeByte(tag);
                    out.writeLong(getLong(i));
                    i++;
                    break;
                }
                case JVM_CONSTANT_Double: {
                    out.writeByte(tag);
                    out.writeDouble(getDouble(i));
                    i++;
                    break;
                }
                case JVM_CONSTANT_Class: {
                    out.writeByte(tag);
                    int klassIndex = getInt(i);
                    int index;
                    if(klassIndex < 0)
                        index = getUtf8SymbolIndex(getKlass(i).getName());
                    else
                        index = higherShort(klassIndex);
                    out.writeShort(index);
                    break;
                }
                case JVM_CONSTANT_UnresolvedClass:
                case JVM_CONSTANT_UnresolvedClassInError: {
                    out.writeByte(JVM_CONSTANT_Class);
                    int klassIndex = getInt(i);
                    if(klassIndex < 0 || klassIndex >= 0xFFFFFFF) {
                        long address = unsafe.getAddress(index(i)) - 1; // ????
                        String symbol = new Symbol(address).asString();
                        out.writeShort(utf8SymbolMap.get(symbol));
                    } else {
                        out.writeShort(higherShort(klassIndex));
                    }
                    break;
                }
                case JVM_CONSTANT_String: {
                    out.writeByte(tag);
                    String string = getString(i);
                    out.writeShort(utf8SymbolMap.get(string));
                    break;
                }
                case JVM_CONSTANT_Fieldref:
                case JVM_CONSTANT_Methodref:
                case JVM_CONSTANT_InterfaceMethodref:
                case JVM_CONSTANT_NameAndType: {
                    out.writeByte(tag);
                    int refIndex = getInt(i);
                    out.writeShort(lowerShort(refIndex));
                    out.writeShort(higherShort(refIndex));
                    break;
                }
            }
        }
    }
}
