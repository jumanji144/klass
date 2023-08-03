package me.darknet.oop.klass;

import me.darknet.oop.util.Util;

import java.util.EnumSet;

public enum InstanceKlassFlag {

    REWRITTEN(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
    HAS_NONSTATIC_FIELDS(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1),
    SHOULD_VERIFY_CLASS(2, 4, 4, 4, 4, 4, 4, 2, 2, 2, 2, 2, 2),
    IS_ANONYMOUS(3, 3, -1, -1, -1, -1, -1, -1, -1, -1, -1),
    IS_CONTENDED(4, 5, 5, 5, 5, 5, 5, 3, 3, 3, 3),
    HAS_DEFAULT_METHODS,
    DECLARES_DEFAULT_METHODS,
    HAS_BEEN_REDEFINED,
    HAS_NONSTATIC_CONCRETE_METHODS,
    DECLARES_NONSTATIC_CONCRETE_METHODS,
    SHARED_LOADING_FAILED,
    IS_SHARED_BOOT_CLASS,
    IS_SHARED_PLATFORM_CLASS,
    IS_SHARED_APP_CLASS,
    HAS_CONTENDED_ANNOTATIONS,
    HAS_LOCAL_VARIABLE_TABLE,
    HAS_MIRANDA_METHODS,
    HAS_VANILLA_CONSTRUCTOR,
    HAS_FINAL_METHOD;

    private final int[] bits;

    InstanceKlassFlag() {
        bits = new int[1];
        bits[0] = 1 << ordinal();
    }

    InstanceKlassFlag(int... bits) {
        this.bits = bits;
    }

    public int getBit() {
        if(bits.length == 1) return bits[0];
        return 1 << bits[Util.getJavaVersion() - 8];
    }

    public static EnumSet<InstanceKlassFlag> getFrom(short flags) {
        EnumSet<InstanceKlassFlag> result = EnumSet.noneOf(InstanceKlassFlag.class);
        for (InstanceKlassFlag flag : values()) {
            if ((flags & flag.getBit()) != 0) {
                result.add(flag);
            }
        }
        return result;
    }

}
