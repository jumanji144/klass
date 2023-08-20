package me.darknet.oop;

import me.darknet.oop.jvm.ProxyOopHandle;
import me.darknet.oop.klass.ClassLoaderData;
import me.darknet.oop.klass.InstanceKlass;
import me.darknet.oop.klass.Klass;
import me.darknet.oop.klass.KlassType;
import me.darknet.oop.types.Types;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Universe {

    private final List<Klass> klasses;

    private Universe(Class<?> clazz) {
        ProxyOopHandle proxyOopHandle = new ProxyOopHandle(clazz);
        long klassAddress = proxyOopHandle.getLong(Types.getValue("java_lang_Class::_klass_offset"));
        this.klasses = getLoadedKlassesFromBase(InstanceKlass.of(klassAddress));
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

        result.add(instanceKlass);

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

    public List<Klass> fromPackage(String packageName) {
        List<Klass> result = new ArrayList<>();
        for (Klass klass : klasses) {
            if (klass.getName().startsWith(packageName)) {
                result.add(klass);
            }
        }
        return result;
    }

    public void dumpKlass(String name, Path path) throws IOException {
        Klass klass = findKlass(name);
        if (klass == null)
            throw new IllegalArgumentException("Klass not found, klass=" + name);
        if (klass.getType() != KlassType.InstanceKlass)
            throw new IllegalArgumentException("Klass must be an instance klass, type=" + klass.getType());
        ((InstanceKlass) klass).dump(new DataOutputStream(Files.newOutputStream(path)));
    }

}
