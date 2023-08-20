package me.darknet.oop.klass;

import me.darknet.oop.Dumpable;

import java.io.DataOutputStream;
import java.io.IOException;

public class Field implements Dumpable {

    private final ConstantPool pool;
    private final short accessFlags;
    private final short nameIndex;
    private final short descriptorIndex;
    private short initialValIndex;
    private final int offset;

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
        if((accessFlags & 0x00000400) != 0) {

        }
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


    @Override
    public void dump(DataOutputStream out) throws IOException {
        out.writeShort(accessFlags);
        out.writeShort(nameIndex);
        out.writeShort(descriptorIndex);
        out.writeShort(0); // No attributes
    }

    public long getNameIndex() {
        return nameIndex;
    }
}
