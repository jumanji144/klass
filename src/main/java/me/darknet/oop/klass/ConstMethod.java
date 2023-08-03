package me.darknet.oop.klass;

import me.darknet.oop.DataOop;
import me.darknet.oop.Structs;
import me.darknet.oop.types.Types;

public class ConstMethod extends DataOop<Byte> {

    private final ConstantPool constantPool;

    public ConstMethod(long base) {
        super(base, base + Types.getSize("ConstMethod"), Structs.constMethod);
        this.constantPool = new ConstantPool(struct.getAddress(base, "_constants"));
    }

    @Override
    public int getLength() {
        return getCodeSize();
    }

    public short getCodeSize() {
        return struct.getShort(base, "_code_size");
    }

    public ConstantPool getConstPool() {
        return constantPool;
    }

    public String getName() {
        return constantPool.getString(struct.getShort(base, "_name_index"));
    }

    public short getNameIndex() {
        return struct.getShort(base, "_name_index");
    }

    public String getDescriptor() {
        return constantPool.getString(struct.getShort(base, "_signature_index"));
    }

    public short getSignatureIndex() {
        return struct.getShort(base, "_signature_index");
    }

    public short maxStack() {
        return struct.getShort(base, "_max_stack");
    }

    public short maxLocals() {
        return struct.getShort(base, "_max_locals");
    }

    @Override
    public Byte get(int index) {
        return unsafe.getByte(dataBase + index);
    }

    @Override
    public void set(int index, Byte value) {
        unsafe.putByte(dataBase + index, value);
    }

    public byte[] getCode() {
        byte[] bytes = new byte[getCodeSize()];
        long arrayBase = unsafe.arrayBaseOffset(byte[].class);
        unsafe.copyMemory(null, dataBase, bytes, arrayBase, getCodeSize());
        return bytes;
    }


}
