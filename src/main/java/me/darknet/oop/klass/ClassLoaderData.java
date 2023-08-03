package me.darknet.oop.klass;

import me.darknet.oop.Oop;
import me.darknet.oop.Structs;
import me.darknet.oop.data.Struct;

public class ClassLoaderData extends Oop {

    public ClassLoaderData(long base) {
        super(base, Structs.classLoaderData);
    }

    public ClassLoaderData getNext() {
        return new ClassLoaderData(struct.getAddress(base, "_next"));
    }

    public Klass getKlasses() {
        return new Klass(struct.getAddress(base, "_klasses"));
    }

    public long getClassLoader() {
        return struct.getAddress(base, "_class_loader");
    }

}
