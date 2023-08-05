package me.darknet.oop.data;

import me.darknet.oop.types.Types;

public class Memory {

    public static final int logKlassAlignmentInBytes = 3;
    public static final int klassAlignmentInBytes = 1 << logKlassAlignmentInBytes;

    public static final long narrowKlassShift = Types.getValue("Universe::_narrow_klass._shift");
    public static final long narrowKlassBase = Types.getValue("Universe::_narrow_klass._base");
    public static final long narrowOopShift = Types.getValue("Universe::_narrow_oop._shift");
    public static final long narrowOopBase = Types.getValue("Universe::_narrow_oop._base");

}
