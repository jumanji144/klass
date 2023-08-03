package me.darknet.oop.klass;

import me.darknet.oop.Oop;
import me.darknet.oop.Structs;
import me.darknet.oop.data.Struct;

public class Klass extends Oop {

    public Klass(long base) {
        super(base, Structs.klass);
    }

    public Klass(long base, Struct struct) {
        super(base, struct);
    }

    public String getName() {
        return new Symbol(struct.getAddress(base, "_name")).asString();
    }

    public Klass getSuperKlass() {
        return new Klass(struct.getAddress(base, "_super"));
    }

    public Klass getNextSibling() {
        return new Klass(struct.getAddress(base, "_next_sibling"));
    }

    public Klass getNextLink() {
        return new Klass(struct.getAddress(base, "_next_link"));
    }

    public long getMirror() {
        return struct.getAddress(base, "_java_mirror");
    }

    public int getAccessFlags() {
        return struct.getInt(base, "_access_flags");
    }
}
