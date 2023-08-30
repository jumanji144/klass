package me.darknet.oop.klass;

import me.darknet.oop.Oop;
import me.darknet.oop.OopCache;
import me.darknet.oop.Structs;
import me.darknet.oop.Dumpable;
import me.darknet.oop.jvm.Array;
import me.darknet.oop.types.Types;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;

public class ConstantPool extends Oop implements Dumpable, ConstantPoolTags {

    private final int _indy_bsm_offset = 0;
    private final int _indy_argc_offset = 1;
    private final int _indy_argv_offset = 2;

    private final Array tags;
    private final Array operands;
    protected final long dataBase;
    protected final int elementSize;
    protected Map<String, Integer> utf8SymbolMap;
    protected Map<String, Integer> classSymbolMap;
    protected List<Short> cachedIndyEntries = new ArrayList<>();
    protected List<Short> refEntries = new ArrayList<>();
    protected Map<Short, Short> refObject;
    protected List<Object> virtualEntries = new ArrayList<>();

    public static class CPSlot {
        private final long addr;

        public CPSlot(long addr) {
            this.addr = addr;
        }

        public boolean isResolved() {
            return (this.addr & 1L) == 0L;
        }

        public Symbol getSymbol() {
            return Symbol.of(this.addr & ~1L);
        }

        public Klass getKlass() {
            return Klass.of(this.addr);
        }
    }

    ConstantPool(long base) {
        super(base, Structs.constantPool);
        this.tags = new Array(struct.getAddress(base, "_tags"), Types.getType("u1"));
        this.operands = new Array(struct.getAddress(base, "_operands"), Types.getType("u2"));
        this.dataBase = base + Types.getSize("ConstantPool");
        this.elementSize = Types.getSize("oop");
    }

    public static ConstantPool of(long base) {
        return OopCache.getOrPut(base, ConstantPool::new);
    }

    long index(int index) {
        return dataBase + (long) index * elementSize;
    }

    public int majorVersion() {
        long version = struct.getOffset("_major_version");
        if(version == -1) return 0;
        return struct.getInt(base, "_major_version");
    }

    public int minorVersion() {
        long version = struct.getOffset("_minor_version");
        if(version == -1) return 0;
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
        return utf8SymbolMap.getOrDefault(symbol, -1);
    }

    public int getClassSymbolIndex(String symbol) {
        return classSymbolMap.get(symbol);
    }

    public ConstantPoolCache getCache() {
        return new ConstantPoolCache(struct.getAddress(base, "_cache"));
    }

    public static int buildIntFromShorts(int low, int high) {
        return ((high & 0xffff) << 16) | (low & 0xffff);
    }

    public int operandOffset(int bootstrapSpecifierIndex) {
        int n = bootstrapSpecifierIndex * 2;

        if(!(n >= 0 && n+2 <= operands.length())) {
            throw new IllegalArgumentException("BSI out of range (1): " + bootstrapSpecifierIndex);
        }

        int secondPart = buildIntFromShorts(operands.getShort(n),
                                            operands.getShort(n + 1));

        if(!(secondPart == 0 || n+2 <= secondPart)) {
            throw new IllegalArgumentException("BSI out of range (2): " + bootstrapSpecifierIndex);
        }

        int offset = buildIntFromShorts(operands.getShort(n),
                operands.getShort(n + 1));

        if(!(offset == 0 || offset >= secondPart && offset <= operands.length())) {
            throw new IllegalArgumentException("BSI out of range (3): " + bootstrapSpecifierIndex);
        }

        return offset;
    }

    public int operandArrayLength() {
        if(operands.isNull() || operands.length() == 0) {
            return 0;
        }
        return (operandOffset(0) / 2);
    }

    public int operandArgumentCount(int bootstrapSpecifierIndex) {
        int offset = operandOffset(bootstrapSpecifierIndex);
        return operands.getShort(offset + _indy_argc_offset);
    }

    public int operandArgumentIndex(int bootstrapSpecifierIndex, int argumentIndex) {
        int offset = operandOffset(bootstrapSpecifierIndex);
        return operands.getShort(offset + _indy_argv_offset + argumentIndex);
    }

    public int operandNextOffset(int bootstrapSpecifierIndex) {
        int offset = operandOffset(bootstrapSpecifierIndex) + _indy_argv_offset;
        return offset + operandArgumentCount(bootstrapSpecifierIndex);
    }

    public int operandBootstrapMethodRefIndex(int bootstrapSpecifierIndex) {
        int offset = operandOffset(bootstrapSpecifierIndex);
        return operands.getShort(offset + _indy_bsm_offset);
    }

    public short getRefIndex(short index) {
        return refEntries.get(index);
    }

    public short getStringIndex(short index) {
        return refObject.get(index);
    }

    public void addVirtualEntry(Object entry) {
        virtualEntries.add(entry);
    }

    public CPSlot getSlot(int index) {
        return new CPSlot(unsafe.getAddress(index(index)));
    }

    public Symbol getKlassNameAt(int index) {
        CPSlot slot = getSlot(index);
        if(slot.isResolved()) {
            return slot.getKlass().getNameSymbol();
        } else {
            return slot.getSymbol();
        }
    }

    public Klass getKlassAt(int index) {
        return Klass.of(unsafe.getAddress(index(index)));
    }

    public void addStringIfAbsent(String string) {
        if(!utf8SymbolMap.containsKey(string)) {
            addVirtualEntry(string);
        }
    }

    public void buildIndexes() {
        // initialize utf8SymbolMap
        utf8SymbolMap = new HashMap<>();
        classSymbolMap = new HashMap<>();
        refObject = new HashMap<>();
        refEntries = new ArrayList<>();
        cachedIndyEntries = new ArrayList<>();

        List<Short> mhEntries = new ArrayList<>();

        short stringCount = 0;

        for (int i = 1; i < getLength(); i++) {
            byte type = getTag(i);
            switch (type) {
                case JVM_CONSTANT_Utf8: {
                    String symbol = getString(i);
                    utf8SymbolMap.put(symbol, i);
                    break;
                }
                case JVM_CONSTANT_Class: {
                    Klass klass = getKlass(i);
                    String klassName = klass.getName();

                    classSymbolMap.put(klassName, i);
                    break;
                }
                case JVM_CONSTANT_UnresolvedClass: {
                    String klassName = getKlassNameAt(i).asString();
                    classSymbolMap.put(klassName, i);
                    break;
                }
                case JVM_CONSTANT_UnresolvedClassInError: {
                    String klassName = getSymbol(i).asString();
                    classSymbolMap.put(klassName, i);
                    break;
                }
                case JVM_CONSTANT_Fieldref:
                case JVM_CONSTANT_Methodref:
                case JVM_CONSTANT_InterfaceMethodref: {
                    refEntries.add((short) i);
                    break;
                }
                case JVM_CONSTANT_String:
                case JVM_CONSTANT_MethodHandle: {
                    // record all method handle references they use
                    int value = getInt(i);
                    short refIndex = higherShort(value);
                    cachedIndyEntries.add(refIndex);
                }
                case JVM_CONSTANT_MethodType:
                    refObject.put(stringCount++, (short) i);
                    break;
                case JVM_CONSTANT_Long:
                case JVM_CONSTANT_Double:
                    i++;
                    break;
            }
        }
    }

    @Override
    public void dump(DataOutputStream out) throws IOException {
        out.writeShort(getLength() + virtualEntries.size());

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
                    Klass klass = getKlass(i);
                    String klassName = klass.getName();
                    short index = (short) (getUtf8SymbolIndex(klassName));
                    out.writeShort(index);
                    break;
                }
                case JVM_CONSTANT_UnresolvedClass: {
                    out.writeByte(JVM_CONSTANT_Class);
                    String klassName = getKlassNameAt(i).asString();
                    short index = (short) (getUtf8SymbolIndex(klassName));
                    out.writeShort(index);
                    break;
                }
                case JVM_CONSTANT_UnresolvedClassInError: {
                    out.writeByte(JVM_CONSTANT_Class);
                    String klassName = getSymbol(i).asString();
                    short index = (short) (getUtf8SymbolIndex(klassName));
                    out.writeShort(index);
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
                case JVM_CONSTANT_NameAndType:
                case JVM_CONSTANT_InvokeDynamic: {
                    out.writeByte(tag);
                    int refIndex = getInt(i);
                    out.writeShort(lowerShort(refIndex));
                    out.writeShort(higherShort(refIndex));
                    break;
                }
                case JVM_CONSTANT_MethodHandle: {
                    out.writeByte(tag);
                    int value = getInt(i);
                    byte refKind = (byte) lowerShort(value);
                    short refIndex = higherShort(value);
                    out.writeByte(refKind);
                    out.writeShort(refIndex);
                    break;
                }
                case JVM_CONSTANT_MethodType: {
                    out.writeByte(tag);
                    int descriptorIndex = getInt(i);
                    out.writeShort((short) descriptorIndex);
                    break;
                }
                case JVM_CONSTANT_ClassIndex: {
                    out.writeByte(JVM_CONSTANT_Class);
                    out.writeShort(getInt(i));
                    break;
                }
                case JVM_CONSTANT_StringIndex: {
                    out.writeByte(JVM_CONSTANT_String);
                    out.writeShort(getInt(i));
                    break;
                }
                default: {
                    throw new RuntimeException("Unknown constant pool tag: " + tag);
                }
            }
        }
        int currentIndex = getLength();
        for (Object virtualEntry : virtualEntries) {
            if(virtualEntry instanceof String) {
                String string = (String) virtualEntry;
                out.writeByte(JVM_CONSTANT_Utf8);
                out.writeShort(string.length());
                out.write(string.getBytes());
                utf8SymbolMap.put(string, currentIndex++);
            }
        }
    }
}
