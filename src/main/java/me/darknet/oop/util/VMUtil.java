package me.darknet.oop.util;

import me.darknet.oop.Universe;
import me.darknet.oop.klass.InstanceKlass;
import org.objectweb.asm.Type;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Arrays;

public class VMUtil {

    public static String getSignature(Method method) {
        return Type.getType(method).getDescriptor();
    }

    public static me.darknet.oop.klass.Method makeHot(InstanceKlass klass, Method method, Object instance) throws Throwable {
        method.setAccessible(true);
        // obtain a method handle for the method
        MethodHandle methodHandle = MethodHandles.lookup().unreflect(method);

        // make the method hot
        me.darknet.oop.klass.Method methodOop = klass
                .findMethod(method.getName(), getSignature(method));

        boolean isStatic = (method.getModifiers() & java.lang.reflect.Modifier.STATIC) != 0;

        int params = method.getParameterCount() + (isStatic ? 0 : 1);
        Object[] args = new Object[params];
        int i = 0;
        if(!isStatic) args[i++] = instance;
        for (; i < params; i++) {
            Class<?> type = method.getParameterTypes()[i - (isStatic ? 0 : 1)];
            if(type.isPrimitive()) {
                if(type == Boolean.TYPE)
                    args[i] = false;
                else
                    args[i] = 0;
            }
            else args[i] = null;
        }

        while (!methodOop.isCompiled()) {
            try {
                methodHandle.invokeWithArguments(args);
            } catch (Throwable ignored) {
                ignored.printStackTrace();
            }
        }

        return methodOop;
    }

    public static me.darknet.oop.klass.Method makeHot(InstanceKlass klass, String path, Object instance) throws Throwable {
        // path is <class>.<name><signature>
        String[] split = path.split("\\.");
        String clazz = String.join(".", Arrays.copyOfRange(split, 0, split.length - 1));
        String name = split[split.length - 1];

        String signature = name.substring(name.indexOf('('));

        name = name.substring(0, name.indexOf('('));

        // find the java reflection method
        Type[] asmTypes = Type.getArgumentTypes(signature);
        Class<?>[] types = new Class<?>[asmTypes.length];
        for (int i = 0; i < asmTypes.length; i++) {
            types[i] = getMirrorType(asmTypes[i]);
        }
        Method method = Class.forName(clazz).getDeclaredMethod(name, types);

        return makeHot(klass, method, instance);
    }

    public static void install(me.darknet.oop.klass.Method method, byte[] code) {
        Unsafe unsafe = UnsafeAccessor.getUnsafe();
        long address = method.getNativeEntry();

        unsafe.writeBytes(code, address);
    }

    public static Class<?> getMirrorType(Type type) throws ClassNotFoundException {
        switch (type.getSort()) {
            case Type.BOOLEAN:
                return boolean.class;
            case Type.BYTE:
                return byte.class;
            case Type.CHAR:
                return char.class;
            case Type.SHORT:
                return short.class;
            case Type.INT:
                return int.class;
            case Type.FLOAT:
                return float.class;
            case Type.LONG:
                return long.class;
            case Type.DOUBLE:
                return double.class;
            case Type.ARRAY:
                return Array.newInstance(getMirrorType(type.getElementType()), 0).getClass();
            case Type.OBJECT:
                return Class.forName(type.getClassName());
        }
        return null;
    }

    public static me.darknet.oop.klass.Method install(InstanceKlass klass, String path, Object instance, byte[] payload) throws Throwable {
        // path is <class>.<name><signature>
        String[] split = path.split("\\.");
        String clazz = String.join(".", Arrays.copyOfRange(split, 0, split.length - 1));
        String name = split[split.length - 1];

        String signature = name.substring(name.indexOf('('));

        name = name.substring(0, name.indexOf('('));

        // find the java reflection method
        Type[] asmTypes = Type.getArgumentTypes(signature);
        Class<?>[] types = new Class<?>[asmTypes.length];
        for (int i = 0; i < asmTypes.length; i++) {
            types[i] = getMirrorType(asmTypes[i]);
        }
        Method method = Class.forName(clazz).getDeclaredMethod(name, types);

        // find universe method
        me.darknet.oop.klass.Method methodOop = klass
                .findMethod(name, signature);

        if(!methodOop.isCompiled()) makeHot(klass, method, instance);

        // install the method
        install(methodOop, payload);

        return methodOop;
    }

    public static me.darknet.oop.klass.Method install(InstanceKlass klass, String path, Object instance, String hexString) throws Throwable {
        // string is like <byte><byte>...
        byte[] bytes = new byte[hexString.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(hexString.substring(i * 2, i * 2 + 2), 16);
        }

        return install(klass, path, instance, bytes);
    }

    public static me.darknet.oop.klass.Method install(InstanceKlass klass, String path, Object instance, long entryPoint) throws Throwable {
        Unsafe unsafe = UnsafeAccessor.getUnsafe();

        // call rel32
        me.darknet.oop.klass.Method entry = install(klass, path, instance, "E800000000C3");

        long nativeEntry = entry.getNativeEntry();
        int rel32 = (int) (entryPoint - nativeEntry - 5);

        unsafe.putInt(entry.getNativeEntry() + 1, rel32);

        return entry;
    }

    public static me.darknet.oop.klass.Method install(String path, Object instance, long entryPoint) throws Throwable {
        // path is <class>.<name><signature>
        String[] split = path.split("\\.");
        String klass = String.join(".", Arrays.copyOfRange(split, 0, split.length - 1));

        Unsafe unsafe = UnsafeAccessor.getUnsafe();

        // call rel32, ret
        me.darknet.oop.klass.Method entry = install(Universe.getFromClass(Class.forName(klass)).asInstance(),
                path, instance, "E800000000C3");

        long nativeEntry = entry.getNativeEntry();
        int rel32 = (int) (entryPoint - nativeEntry - 5);

        unsafe.putInt(entry.getNativeEntry() + 1, rel32);

        return entry;
    }

}
