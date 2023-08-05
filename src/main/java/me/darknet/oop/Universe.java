package me.darknet.oop;

import me.darknet.oop.klass.ClassLoaderData;
import me.darknet.oop.klass.InstanceKlass;
import me.darknet.oop.klass.Klass;

import java.util.ArrayList;
import java.util.List;

public class Universe {

    private final List<Klass> klasses;

    private Universe(Class<?> clazz) {
        this.klasses = getLoadedKlassesFromBase(InstanceKlass.of(clazz));
    }

    public static Universe obtainFrom(Class<?> clazz) {
        return new Universe(clazz);
    }

    public static List<Klass> getLoadedKlassesFromBase(InstanceKlass instanceKlass) {
        List<Klass> result = new ArrayList<>();

        for(ClassLoaderData cld = instanceKlass.getClassLoaderData(); cld.getBase() != 0L; cld = cld.getNext()) {
            for(Klass klass = cld.getKlasses(); klass.getBase() != 0L; klass = klass.getNextLink()) {
                result.add(klass);
            }
        }

        return result;
    }

    public List<Klass> getKlasses() {
        return klasses;
    }

    public Klass findKlass(String name) {
        for (Klass klass : klasses) {
            if (klass.getName().equals(name)) {
                return klass;
            }
        }
        return null;
    }

}
