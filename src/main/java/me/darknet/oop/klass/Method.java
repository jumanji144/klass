package me.darknet.oop.klass;

import me.darknet.oop.Oop;
import me.darknet.oop.OopCache;
import me.darknet.oop.Structs;
import me.darknet.oop.Dumpable;
import me.darknet.oop.data.CodeTransformer;
import org.objectweb.asm.Opcodes;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.EnumSet;

public class Method extends Oop implements Dumpable {

    protected boolean rewritten = false;

    Method(long base) {
        super(base, Structs.method);
    }

    public static Method of(long base) {
        return OopCache.getOrPut(base, Method::new);
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

    public EnumSet<AccessFlags> getFlags() {
        return AccessFlags.getFlags(getAccessFlags(), AccessFlags.Scope.METHOD);
    }

    @Override
    public void dump(DataOutputStream out) throws IOException {
        ConstMethod constMethod = getConstMethod();
        System.out.println("Dumping method: " + constMethod.getName());
        ConstantPool pool = OopCache.get(constMethod.getConstPool().getBase());
        out.writeShort(getAccessFlags());
        out.writeShort(constMethod.getNameIndex());
        out.writeShort(constMethod.getSignatureIndex());
        for (AccessFlags flag : getFlags()) {
            System.out.println("Flag: " + flag);
        }
        if((getAccessFlags() & Opcodes.ACC_ABSTRACT) != 0) {
            out.writeShort(0); // Attribute count
            return;
        }
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

    @Override
    public String toString() {
        return getConstMethod().getName();
    }
}
