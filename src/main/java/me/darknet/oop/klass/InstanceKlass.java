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
    private final Array interfaces;
    private List<Klass> interfaceList;
    private List<Method> methodList;
    private List<Field> fieldList;
    private final ConstantPool constantPool;

    public InstanceKlass(long base) {
        super(base, Structs.instanceKlass);
        this.methods = new Array(struct.getAddress(base, "_methods"), Types.getType("Method*"));
        this.fields = new Array(struct.getAddress(base, "_fields"), Types.getType("u2"));
        this.interfaces = new Array(struct.getAddress(base, "_local_interfaces"), Types.getType("Klass*"));
        this.constantPool = ConstantPool.of(struct.getAddress(base, "_constants"));
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

    public List<Klass> getInterfaces() {
        if(interfaceList == null) {
            interfaceList = new ArrayList<>();
            for (int i = 0; i < this.interfaces.length(); i++) {
                interfaceList.add(Klass.of(this.interfaces.getAddress(i)));
            }
        }
        return interfaceList;
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
                Method method = Method.of(this.methods.getAddress(i));
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
            int fieldCount = this.fields.length();
            for (int i = 0; i < this.fields.length(); i += 6) {
                short accessFlags = this.fields.getShort(i);

                if((accessFlags & 0x00000800) != 0) {
                    genericSignatureCount++;
                    fieldCount--;
                }
            }

            int genericSignatureIndex = fieldCount;
            fieldCount = fieldCount / 6;

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
        if(version == -1) {
            return getConstantPool().majorVersion();
        }
        return struct.getInt(base, "_major_version");
    }

    public int minorVersion() {
        long version = struct.getOffset("_minor_version");
        if(version == -1) {
            return getConstantPool().minorVersion();
        }
        return struct.getInt(base, "_minor_version");
    }

    @Override
    public void dump(DataOutputStream out) throws IOException {
        ConstantPool pool = getConstantPool();
        out.writeInt(0xCAFEBABE);
        out.writeShort(minorVersion());
        out.writeShort(majorVersion());
        constantPool.buildIndexes();
        constantPool.dump(out);
        out.writeShort(getAccessFlags() & 0xFFFF);
        out.writeShort(constantPool.getClassSymbolIndex(getName()));
        out.writeShort(getSuperKlass().getBase() == 0 ? 0 : constantPool.getClassSymbolIndex(
                getSuperKlass().getName()));
        out.writeShort(getInterfaces().size());
        for (Klass iface : getInterfaces()) {
            out.writeShort(constantPool.getClassSymbolIndex(iface.getName()));
        }
        out.writeShort(getFields().size());
        for (Field field : getFields()) {
            field.dump(out);
        }
        out.writeShort(getMethods().size());
        for (Method method : getMethods()) {
            method.dump(out);
        }
        out.writeShort(constantPool.operandArrayLength() > 0 ? 1 : 0); // attributes count
        if(constantPool.operandArrayLength() != 0) {
            pool.addStringIfAbsent("BootstrapMethods");
            out.writeShort(pool.getUtf8SymbolIndex("BootstrapMethods"));

            int length = 2;
            int numBootstrapMethods = pool.operandArrayLength();
            for (int i = 0; i < numBootstrapMethods; i++) {
                int numBootstrapArguments = pool.operandArgumentCount(i);
                length += 2 + 2 + (2 * numBootstrapArguments);
            }

            out.writeInt(length);

            out.writeShort(numBootstrapMethods);
            for (int i = 0; i < numBootstrapMethods; i++) {
                int bsmRef = pool.operandBootstrapMethodRefIndex(i);
                int numArgs = pool.operandArgumentCount(i);
                out.writeShort(bsmRef);
                out.writeShort(numArgs);
                for (int i1 = 0; i1 < numArgs; i1++) {
                    int arg = pool.operandArgumentIndex(i, i1);
                    out.writeShort(arg);
                }
            }
        }
    }
}
