package me.darknet.oop.klass;

import me.darknet.oop.Offsets;
import me.darknet.oop.Structs;
import me.darknet.oop.Dumpable;
import me.darknet.oop.jvm.Array;
import me.darknet.oop.types.Types;
import me.darknet.oop.util.OopUtil;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class InstanceKlass extends Klass implements Dumpable {

    private final Array methods;
    private List<Method> methodList;
    private ConstantPool constantPool;

    public InstanceKlass(Class<?> clazz) {
        this(unsafe.getLong(clazz, Offsets._klass_offset));
    }

    public InstanceKlass(long base) {
        super(base, Structs.instanceKlass);
        this.methods = new Array(struct.getAddress(base, "_methods"), Types.getType("Method*"));
        this.constantPool = new ConstantPool(struct.getAddress(base, "_constants"));
    }

    public static InstanceKlass fromOop(long oop) {
        long address = oop + Types.getOffset("oopDesc::_metadata._compressed_klass");
        long decompAddress = OopUtil.readCompKlassAddress(address);
        return new InstanceKlass(decompAddress);
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
