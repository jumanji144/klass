package me.darknet.oop;

import me.darknet.oop.util.Util;

public class Offsets {

    // Class oop offsets
    public static final long _klass_offset; // InstanceKlass*

    // Klass offsets
    public static final long _misc_flags_offset; // u2
    public static final long _methods_offset; // Array<Method*>*

    private static final long[] _klass_offsets = new long[] {
            0x48, 0x50, 0x48, 0x50, 0x50, 0x50, 0x50, 0x10, 0x10, 0x10, 0x10, 0x10, 0x10
    };

    private static final long[] _misc_flags_offsets = new long[] {
            0x110, 0x112, 0x110, 0x13A, 0x13A, 0x13A, 0x13A, 0x13E, 0x13E, 0x13E, 0x126, 0x126, 0x126
    };

    private static final long[] _methods_offsets = new long[] {
            0x118, 0x11A, 0x118, 0x144, 0x144, 0x144, 0x144, 0x148, 0x148, 0x148, 0x12E, 0x12E, 0x12E
    };

    static {
        int version = Util.getJavaVersion();
        _klass_offset = _klass_offsets[version - 8];
        _misc_flags_offset = _misc_flags_offsets[version - 8];
        _methods_offset = _methods_offsets[version - 8];
    }

}
