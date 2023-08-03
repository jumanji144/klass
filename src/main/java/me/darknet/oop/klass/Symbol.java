package me.darknet.oop.klass;

import me.darknet.oop.DataOop;
import me.darknet.oop.Structs;
import me.darknet.oop.data.Struct;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

public class Symbol extends DataOop<Byte> {

    public Symbol(long base) {
        super(base, base + Structs.symbol.getOffset("_body"), Structs.symbol);
    }

    @Override
    public int getLength() {
        return struct.getShort(base, "_length") & 0xffff;
    }

    @Override
    public Byte get(int index) {
        return unsafe.getByte(dataBase + (long) index * unsafe.addressSize());
    }

    @Override
    public void set(int index, Byte value) {
        unsafe.putByte(dataBase + (long) index * unsafe.addressSize(), value);
    }

    public String asString() {
        byte[] bytes = new byte[length + 2];
        long arrayBase = unsafe.arrayBaseOffset(byte[].class);
        unsafe.copyMemory(null, dataBase, bytes, arrayBase + 2, length);

        bytes[0] = (byte) ((length >>> 8) & 0xff);
        bytes[1] = (byte) (length & 0xff);

        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(bytes));
        try {
            return dis.readUTF();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public byte[] asBytes() {
        byte[] bytes = new byte[length];
        long arrayBase = unsafe.arrayBaseOffset(byte[].class);
        unsafe.copyMemory(null, dataBase, bytes, arrayBase, length);
        return bytes;
    }

}
