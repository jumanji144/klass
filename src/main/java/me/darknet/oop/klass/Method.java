package me.darknet.oop.klass;

import me.darknet.oop.Oop;
import me.darknet.oop.OopCache;
import me.darknet.oop.Structs;
import me.darknet.oop.Dumpable;
import me.darknet.oop.code.NMethod;
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

    public MethodCounters getCounters() {
        return new MethodCounters(struct.getAddress(base, "_method_counters"));
    }

    public boolean isCompiled() {
        return struct.getAddress(base, "_code") != 0;
    }

    public NMethod getNMethod() {
        return NMethod.of(struct.getAddress(base, "_code"));
    }

    public long getNativeEntry() {
        return struct.getAddress(base, "_from_compiled_entry");
    }

    @Override
    public void dump(DataOutputStream out) throws IOException {
        ConstMethod constMethod = getConstMethod();
        ConstantPool pool = constMethod.getConstPool();
        out.writeShort(getAccessFlags());
        out.writeShort(constMethod.getNameIndex());
        out.writeShort(constMethod.getSignatureIndex());
        int attributeCount = 0;
        boolean shouldWriteCode = false;
        if((getAccessFlags() & Opcodes.ACC_ABSTRACT) == 0) {
            attributeCount++; // code
            pool.addStringIfAbsent("Code");
            shouldWriteCode = true;
        }
        out.writeShort(attributeCount);
        if(attributeCount == 0) return;
        byte[] code = constMethod.getCode();
        if (rewritten) CodeTransformer.transform(pool, code);
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
        return getConstMethod().getName() + getConstMethod().getDescriptor();
    }
}
