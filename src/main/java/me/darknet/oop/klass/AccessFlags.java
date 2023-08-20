package me.darknet.oop.klass;

import java.util.Arrays;
import java.util.EnumSet;

public enum AccessFlags {

    ACC_PUBLIC(0x0001, Scope.CLASS, Scope.FIELD, Scope.METHOD),
    ACC_PRIVATE(0x0002, Scope.FIELD, Scope.METHOD),
    ACC_PROTECTED(0x0004, Scope.FIELD, Scope.METHOD),
    ACC_STATIC(0x0008, Scope.FIELD, Scope.METHOD),
    ACC_FINAL(0x0010, Scope.CLASS, Scope.FIELD, Scope.METHOD),
    ACC_SUPER(0x0020, Scope.CLASS),
    ACC_SYNCHRONIZED(0x0020, Scope.METHOD),
    ACC_VOLATILE(0x0040, Scope.FIELD),
    ACC_BRIDGE(0x0040, Scope.METHOD),
    ACC_TRANSIENT(0x0080, Scope.FIELD),
    ACC_VARARGS(0x0080, Scope.METHOD),
    ACC_NATIVE(0x0100, Scope.METHOD),
    ACC_INTERFACE(0x0200, Scope.CLASS),
    ACC_ABSTRACT(0x0400, Scope.CLASS, Scope.METHOD),
    ACC_STRICT(0x0800, Scope.METHOD),
    ACC_SYNTHETIC(0x1000, Scope.CLASS, Scope.FIELD, Scope.METHOD),
    ACC_ANNOTATION(0x2000, Scope.CLASS),
    ACC_ENUM(0x4000, Scope.CLASS, Scope.FIELD),
    ACC_MANDATED(0x8000, Scope.FIELD, Scope.METHOD),
    ACC_MODULE(0x8000, Scope.CLASS),

    ACC_MONITOR_MATCH(0x10000000, Scope.METHOD),
    ACC_HAS_MONITOR_BYTECODE(0x20000000, Scope.METHOD),
    ACC_HAS_LOOPS(0x40000000, Scope.METHOD),
    ACC_LOOPS_FLAG_INIT(0x80000000, Scope.METHOD),
    ACC_QUEUED(0x01000000, Scope.METHOD),
    ACC_NOT_OSR_COMPILABLE(0x08000000, Scope.METHOD),
    ACC_HAS_LINE_NUMBER_TABLE(0x00100000, Scope.METHOD),
    ACC_HAS_CHECKED_EXCEPTIONS(0x00200000, Scope.METHOD),
    ACC_HAS_JSRS(0x00400000, Scope.METHOD),
    ACC_IS_OBSOLETE(0x00010000, Scope.METHOD),
    ACC_HAS_MIRANDA_METHODS(0x10000000, Scope.CLASS),
    ACC_HAS_VANILLA_CONSTRUCTOR(0x20000000, Scope.CLASS),
    ACC_HAS_FINALIZER(0x40000000, Scope.CLASS),
    ACC_IS_CLONEABLE(0x80000000, Scope.CLASS),
    ACC_HAS_LOCAL_VARIABLE_TABLE(0x00200000, Scope.CLASS, Scope.METHOD),
    ACC_PROMOTED_FLAGS(0x00200000, Scope.CLASS, Scope.METHOD),
    ACC_FIELD_ACCESS_WATCHED(0x00002000, Scope.FIELD),
    ACC_FIELD_MODIFICATION_WATCHED(0x00008000, Scope.FIELD),
    ACC_FIELD_HAS_GENERIC_SIGNATURE(0x00000800, Scope.FIELD),
    ACC_FIELD_IS_INTERNAL(0x00000400, Scope.FIELD);

    private final int i;
    private final Scope[] scopes;

    AccessFlags(int i, Scope... scopes) {
        this.i = i;
        this.scopes = scopes;
    }

    public static EnumSet<AccessFlags> getFlags(int i, Scope scope) {
        EnumSet<AccessFlags> flags = EnumSet.noneOf(AccessFlags.class);
        for (AccessFlags flag : values()) {
            if (flag.scopes.length == 0 || Arrays.asList(flag.scopes).contains(scope)) {
                if ((i & flag.i) != 0) {
                    flags.add(flag);
                }
            }
        }
        return flags;
    }

    public enum Scope {
        CLASS, FIELD, METHOD
    }

}
