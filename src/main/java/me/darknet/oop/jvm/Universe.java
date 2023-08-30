package me.darknet.oop.jvm;

import me.darknet.oop.data.Field;
import me.darknet.oop.data.Struct;

public class Universe {

    private static final Struct universe = new Struct("Universe");

    private static final Field narrowKlassBase = universe.getStaticField("_narrow_klass._base");
    private static final Field narrowKlassShift = universe.getStaticField("_narrow_klass._shift");

    private static final Field narrowOopBase = universe.getStaticField("_narrow_oop._base");
    private static final Field narrowOopShift = universe.getStaticField("_narrow_oop._shift");

    public static long getNarrowKlassBase() {
        return narrowKlassBase.getAddress();
    }

    public static int getNarrowKlassShift() {
        return narrowKlassShift.getInt();
    }

    public static long getNarrowOopBase() {
        return narrowOopBase.getAddress();
    }

    public static int getNarrowOopShift() {
        return narrowOopShift.getInt();
    }

}
