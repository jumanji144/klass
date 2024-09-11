package me.darknet.oop.code;

import me.darknet.oop.Oop;
import me.darknet.oop.OopCache;
import me.darknet.oop.Structs;
import me.darknet.oop.jvm.NativeOopHandle;
import me.darknet.oop.klass.Method;
import me.darknet.oop.types.Types;

public class NMethod extends Oop {

    NMethod(long base) {
        super(base, Structs.nMethod);
    }

    public static NMethod of(long base) {
        return OopCache.getOrPut(base, NMethod::new);
    }

    public Method getMethod() {
        return Method.of(struct.getAddress(base, "_method"));
    }

    public int getEntryBci() {
        return struct.getInt(base, "_entry_bci");
    }

    public NMethod getOsrLink() {
        return NMethod.of(struct.getAddress(base, "_osr_link"));
    }

    public long getEntry() {
        return struct.getAddress(base, "_entry_point");
    }

    public long getVerifiedEntry() {
        return struct.getAddress(base, "_verified_entry_point");
    }

    public long getOsrEntry() {
        return struct.getAddress(base, "_osr_entry_point");
    }

    public CompLevel getCompilationLevel() {
        return CompLevel.values()[struct.getInt(base, "_comp_level")+1];
    }

    public int getCompilationId() {
        return struct.getInt(base, "_compilation_id");
    }

    private int getOopsOffset() {
        return struct.getInt(base, "_oops_offset");
    }

    private int getMetadataOffset() {
        return struct.getInt(base, "_metadata_offset");
    }

    public int getOopsLength() {
        return getMetadataOffset() - getOopsOffset();
    }

    private long getInstsBegin() {
        return struct.getAddress(base, "_code_begin");
    }

    private long getInstsEnd() {
        return base + struct.getInt(base, "_stub_offset");
    }

    public long getInstsSize() {
        return getInstsEnd() - getInstsBegin();
    }

    public NativeOopHandle getOop(int index) {
        return new NativeOopHandle(base + getOopsOffset() + ((long) index * Types.getSize("oop*")));
    }



}
