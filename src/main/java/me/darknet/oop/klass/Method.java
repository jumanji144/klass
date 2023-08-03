package me.darknet.oop.klass;

import me.darknet.oop.Oop;
import me.darknet.oop.OopCache;
import me.darknet.oop.Structs;
import me.darknet.oop.Dumpable;
import me.darknet.oop.data.CodeTransformer;

import java.io.DataOutputStream;
import java.io.IOException;

public class Method extends Oop implements Dumpable {

    protected boolean rewritten = false;

    public Method(long base) {
        super(base, Structs.method);
    }

    public void setRewritten(boolean rewritten) {
        this.rewritten = rewritten;
    }

    public ConstMethod getConstMethod() {
        return new ConstMethod(struct.getAddress(base, "_constMethod"));
    }

    public short getAccessFlags() {
        return (short) struct.getInt(base, "_access_flags");
    }

    @Override
    public void dump(DataOutputStream out) throws IOException {
        ConstMethod constMethod = getConstMethod();
        ConstantPool pool = OopCache.get(constMethod.getConstPool().getBase());
        out.writeShort(getAccessFlags());
        out.writeShort(constMethod.getNameIndex());
        out.writeShort(constMethod.getSignatureIndex());
        out.writeShort(1); // Code
        byte[] code = constMethod.getCode();
        if(rewritten) CodeTransformer.transform(pool, code);
        // write code attribute
        out.writeShort(pool.getUtf8SymbolIndex("Code"));
        out.writeInt(2 + 2 + 4 + code.length + 2 + 2);
        out.writeShort(constMethod.maxStack());
        out.writeShort(constMethod.maxLocals());
        out.writeInt(code.length);
        out.write(code);
        out.writeShort(0); // Exception table length
        out.writeShort(0); // Attributes count
    }
}
