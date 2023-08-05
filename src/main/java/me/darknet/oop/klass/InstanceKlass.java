package me.darknet.oop.klass;

import me.darknet.oop.OopCache;
import me.darknet.oop.Structs;
import me.darknet.oop.Dumpable;
import me.darknet.oop.jvm.Array;
import me.darknet.oop.jvm.ProxyOopHandle;
import me.darknet.oop.types.Types;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class InstanceKlass extends Klass implements Dumpable {

    private final Array methods;
    private final Array fields;
    private List<Method> methodList;
    private List<Field> fieldList;
    private final ConstantPool constantPool;

    public InstanceKlass(long base) {
        super(base, Structs.instanceKlass);
        this.methods = new Array(struct.getAddress(base, "_methods"), Types.getType("Method*"));
        this.fields = new Array(struct.getAddress(base, "_fields"), Types.getType("u2"));
        this.constantPool = new ConstantPool(struct.getAddress(base, "_constants"));
    }

    public static InstanceKlass of(Object oop) {
        return (InstanceKlass) new ProxyOopHandle(oop).getKlass();
    }

    public static InstanceKlass of(long base) {
        return OopCache.getOrPut(base, InstanceKlass::new);
    }

    public static InstanceKlass cast(Klass klass) {
        return new InstanceKlass(klass.getBase());
    }

    public boolean isFlagSet(InstanceKlassFlag flag) {
        return (struct.getShort(base, "_misc_flags") & flag.getBit()) != 0;
    }

    public void setMiscFlag(InstanceKlassFlag flag, boolean value) {
        short flags = struct.getShort(base, "_misc_flags");
        if (value) {
            flags |= (short) flag.getBit();
        } else {
            flags &= (short) ~flag.getBit();
        }
        struct.putShort(base, "_misc_flags", flags);
    }

    public EnumSet<InstanceKlassFlag> getFlags() {
        return InstanceKlassFlag.getFrom(struct.getShort(base, "_misc_flags"));
    }

    public List<Method> getMethods() {
        boolean rewritten = isFlagSet(InstanceKlassFlag.REWRITTEN);
        if(methodList == null) {
            methodList = new ArrayList<>();
            for (int i = 0; i < this.methods.length(); i++) {
                Method method = new Method(this.methods.getAddress(i));
                method.setRewritten(rewritten);
                methodList.add(method);
            }
        }
        return methodList;
    }

    public List<Field> getFields() {
        if(fieldList == null) {
            fieldList = new ArrayList<>();
            // fields are stored in tuples of 6 u2 values
            int genericSignatureCount = 0;
            int fieldCount = this.fields.length() / 6;
            for (int i = 0; i < this.fields.length(); i += 6) {
                short accessFlags = this.fields.getShort(i);

                if((accessFlags & 0x00000800) != 0) {
                    genericSignatureCount++;
                    fieldCount--;
                }
            }

            int genericSignatureIndex = fieldCount * 6;

            for (int i = 0; i < fieldCount; i++) {
                short accessFlags = this.fields.getShort(i * 6);
                short nameIndex = this.fields.getShort(i * 6 + 1);
                short signatureIndex = this.fields.getShort(i * 6 + 2);
                short initialValIndex = this.fields.getShort(i * 6 + 3);
                short lowOffset = this.fields.getShort(i * 6 + 4);
                short highOffset = this.fields.getShort(i * 6 + 5);

                Field field = new Field(constantPool, accessFlags, nameIndex, signatureIndex, initialValIndex,
                        lowOffset, highOffset);
                fieldList.add(field);
            }
        }
        return fieldList;
    }

    public Method findMethod(String name, String signature) {
        for (Method method : getMethods()) {
            if(method.getConstMethod().getName().equals(name)
                    && method.getConstMethod().getDescriptor().equals(signature)) {
                return method;
            }
        }
        return null;
    }

    public ConstantPool getConstantPool() {
        return constantPool;
    }

    public ClassLoaderData getClassLoaderData() {
        return new ClassLoaderData(struct.getAddress(base, "_class_loader_data"));
    }

    public int majorVersion() {
        long version = struct.getOffset("_major_version");
        if(version == 0) {
            return getConstantPool().majorVersion();
        }
        return struct.getInt(base, "_major_version");
    }

    public int minorVersion() {
        long version = struct.getOffset("_minor_version");
        if(version == 0) {
            return getConstantPool().minorVersion();
        }
        return struct.getInt(base, "_minor_version");
    }

    @Override
    public void dump(DataOutputStream out) throws IOException {
        out.writeInt(0xCAFEBABE);
        out.writeShort(minorVersion());
        out.writeShort(majorVersion());
        constantPool.dump(out);
        out.writeShort(getAccessFlags() & 0xFFFF);
        out.writeShort(constantPool.getClassSymbolIndex(getName()));
        out.writeShort(constantPool.getClassSymbolIndex(getSuperKlass().getName()));
        out.writeShort(0); // interfaces count
        out.writeShort(0); // fields count
        out.writeShort(getMethods().size());
        for (Method method : getMethods()) {
            method.dump(out);
        }
        out.writeShort(0); // attributes count
    }
}
