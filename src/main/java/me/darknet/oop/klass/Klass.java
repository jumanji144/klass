package me.darknet.oop.klass;

import me.darknet.oop.Oop;
import me.darknet.oop.OopCache;
import me.darknet.oop.Structs;
import me.darknet.oop.data.Struct;

public class Klass extends Oop {

    private static final int _lh_array_tag_type_value = ~0;
    private static final int _lh_array_tag_shift = (Integer.BYTES * 8) - 2;

    protected Klass(long base) {
        super(base, Structs.klass);
    }

    protected Klass(long base, Struct struct) {
        super(base, struct);
    }

    public static Klass of(long base) {
        if(base == 0)
            return new Klass(base);
        // get type
        Struct struct = Structs.klass;
        int layout = struct.getInt(base, "_layout_helper");
        KlassType klassType = getType(layout);
        if(klassType == KlassType.InstanceKlass)
            return InstanceKlass.of(base);
        return OopCache.getOrPut(base, Klass::new);
    }

    static KlassType getType(int layout) {
        if (layout > 0) {
            return KlassType.InstanceKlass;
        } else if(layout < 0) {
            int typeTag = _lh_array_tag_type_value << _lh_array_tag_shift;
            if(layout >= typeTag) {
                return KlassType.TypeArrayKlass;
            } else {
                return KlassType.ObjArrayKlass;
            }
        }
        return KlassType.ArrayKlass;
    }

    public KlassType getType() {
        return getType(struct.getInt(base, "_layout_helper"));
    }

    public String getName() {
        return Symbol.of(struct.getAddress(base, "_name")).asString();
    }

    public InstanceKlass getSuperKlass() {
        return InstanceKlass.of(struct.getAddress(base, "_super"));
    }

    public Klass getNextSibling() {
        return of(struct.getAddress(base, "_next_sibling"));
    }

    public Klass getNextLink() {
        return of(struct.getAddress(base, "_next_link"));
    }

    public long getMirror() {
        return struct.getAddress(base, "_java_mirror");
    }

    public int getAccessFlags() {
        return struct.getInt(base, "_access_flags");
    }
}
