package me.darknet.oop.jithack.arch;

import me.darknet.oop.jithack.Assembler;
import org.objectweb.asm.Type;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public abstract class AbstractAssembler implements Assembler {

    private final int[] mapIntArgs;
    private final int[] mapLongArgs;

    protected ByteBuffer code;

    protected void put(byte... bytes) {
        code.put(bytes);
    }

    protected void put(int... ints) {
        for (int i : ints) {
            code.put((byte) i);
        }
    }

    protected void emit(int i) {
        if ((i >>> 24) != 0)    code.put((byte) (i >>> 24));
        if ((i >>> 16) != 0)    code.put((byte) (i >>> 16));
        if ((i >>> 8) != 0)     code.put((byte) (i >>> 8));
        if ((i != 0))           code.put((byte) (i));
    }

    protected AbstractAssembler(int[] mapIntArgs, int[] mapLongArgs) {
        this.mapIntArgs = mapIntArgs;
        this.mapLongArgs = mapLongArgs;
        this.code = ByteBuffer.allocate(100).order(ByteOrder.nativeOrder());
    }

    @Override
    public void emitConvention(Type[] args) {
        for (int i = 0; i < args.length; i++) {
            Type type = args[i];
            int sort = type.getSort();
            if (sort == Type.LONG || sort == Type.DOUBLE || sort == Type.OBJECT) {
                emit(mapLongArgs[i]);
            } else  {
                emit(mapIntArgs[i]);
            }
        }
    }

    @Override
    public byte[] assemble() {
        byte[] array = new byte[code.position()];
        System.arraycopy(code.array(), 0, array, 0, array.length);
        return array;
    }
}
