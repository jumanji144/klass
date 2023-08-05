package me.darknet.oop.klass;

public class Field {

    private ConstantPool pool;
    private short accessFlags;
    private short nameIndex;
    private short descriptorIndex;
    private short initialValIndex;
    private int offset;

    public Field(ConstantPool pool, short accessFlags, short nameIndex, short descriptorIndex, short initialValIndex,
                 short lowOffset, short highOffset) {
        this.pool = pool;
        this.accessFlags = accessFlags;
        this.nameIndex = nameIndex;
        this.descriptorIndex = descriptorIndex;
        this.initialValIndex = initialValIndex;
        this.offset = (((highOffset & 0xFF) << 8) | (lowOffset & 0xFF)) >> 2;
    }

    public String getName() {
        return pool.getSymbol(nameIndex).asString();
    }

    public String getDescriptor() {
        return pool.getSymbol(descriptorIndex).asString();
    }

    public int getOffset() {
        return offset;
    }

    public int getAccessFlags() {
        return accessFlags;
    }



}
