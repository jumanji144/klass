package me.darknet.oop.klass;

import me.darknet.oop.Oop;
import me.darknet.oop.Structs;
import me.darknet.oop.data.Opcodes;
import me.darknet.oop.data.Struct;

public class ConstantPoolCacheEntry extends Oop {

    private static final int cp_index_bits = 2 * 8;
    private static final int cp_index_mask = (1 << cp_index_bits) - 1;
    private static final int bytecode_1_shift = cp_index_bits;
    private static final int bytecode_1_mask = (1 << 8) - 1;
    private static final int bytecode_2_shift = 2 * cp_index_bits;
    private static final int bytecode_2_mask = (1 << 8) - 1;

    private static final int is_field_entry_shift = 26;
    private static final int has_method_type_shift = 25;
    private static final int has_appendix_shift = 24;
    private static final int is_forced_virtual_shift = 23;
    private static final int is_final_shift = 22;
    private static final int is_volatile_shift = 21;
    private static final int is_vfinal_shift = 20;

    private static final int tos_state_bits = 4;
    private static final int tos_state_mask = (1 << tos_state_bits) - 1;
    private static final int tos_state_shift = (Integer.BYTES * 8) - tos_state_bits;


    public ConstantPoolCacheEntry(long base) {
        super(base, Structs.constantPoolCacheEntry);
    }

    public long getIndecies() {
        return struct.getInt(base, "_indices") & 0xffffffffL;
    }

    public int getCpIndex() {
        return (int) (getIndecies() & cp_index_mask);
    }

    public int getBytecode1() {
        return (int) ((getIndecies() >>> bytecode_1_shift) & bytecode_1_mask);
    }

    public int getBytecode2() {
        return (int) ((getIndecies() >>> bytecode_2_shift) & bytecode_2_mask);
    }

    public long getF1() {
        return struct.getAddress(base, "_f1");
    }

    public long getF2() {
        return struct.getInt(base, "_f2") & 0xffffffffL;
    }

    public boolean isFieldEntry() {
        return (getFlags() & (1 << is_field_entry_shift)) != 0;
    }

    public boolean hasMethodType() {
        return (getFlags() & (1 << has_method_type_shift)) != 0;
    }

    public boolean hasAppendix() {
        return (getFlags() & (1 << has_appendix_shift)) != 0;
    }

    public boolean isForcedVirtual() {
        return (getFlags() & (1 << is_forced_virtual_shift)) != 0;
    }

    public boolean isFinal() {
        return (getFlags() & (1 << is_final_shift)) != 0;
    }

    public boolean isVolatile() {
        return (getFlags() & (1 << is_volatile_shift)) != 0;
    }

    public boolean isVfinal() {
        return (getFlags() & (1 << is_vfinal_shift)) != 0;
    }

    public boolean isMethod() {
        return !isFieldEntry();
    }

    public boolean isInvokeDynamicCallSite() {
        return getBytecode1() == Opcodes.INVOKEDYNAMIC;
    }

    public int getFlags() {
        return struct.getInt(base, "_flags");
    }

    public Metadata getMetadata() {
        return new Metadata(getF1());
    }



}
