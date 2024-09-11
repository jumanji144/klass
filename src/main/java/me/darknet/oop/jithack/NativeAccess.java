package me.darknet.oop.jithack;

import me.darknet.oop.Universe;
import me.darknet.oop.jvm.base.ThreadStrategy;
import me.darknet.oop.klass.InstanceKlass;
import me.darknet.oop.library.Library;
import me.darknet.oop.util.*;

import java.lang.invoke.MethodHandle;
import java.util.HashMap;
import java.util.Map;

public class NativeAccess {
    private static final Unsafe unsafe = UnsafeAccessor.getUnsafe();
    private static final Map<String, MethodHandle> handles = new HashMap<>();

    public static final int PROT_READ = 0x1;
    public static final int PROT_WRITE = 0x2;
    public static final int PROT_EXEC = 0x4;

    public static final int MAP_SHARED = 0x01;
    public static final int MAP_PRIVATE = 0x02;
    public static final int MAP_FIXED = 0x10;
    public static final int MAP_ANONYMOUS = 0x20;

    public static long GetStringUTFChars(String string) {
        byte[] bytes = string.getBytes();
        long address = unsafe.allocateMemory(bytes.length + 1);
        unsafe.copyMemory(bytes, unsafe.arrayBaseOffset(byte[].class), null, address, bytes.length);
        unsafe.putByte(address + bytes.length, (byte) 0);
        return address;
    }

    public static void ReleaseStringUTFChars(long address) {
        if(address != 0)
            unsafe.freeMemory(address);
    }

    public static String GetStringFromUTFChars(long address) {
        if(address == 0) return null;
        long length = 0;
        while (unsafe.getByte(address + length) != 0) {
            length++;
        }
        byte[] bytes = new byte[(int) length];
        unsafe.copyMemory(null, address, bytes, unsafe.arrayBaseOffset(byte[].class), length);
        return new String(bytes);
    }

    public static long mmap(long addr, long length, int prot, int flags, int fd, int offset) throws Throwable {
        MethodHandle handle = handles.get("mmap");
        return (long) handle.invokeExact(addr, length, prot, flags, fd, offset);
    }

    static {
        try {
            Library libC = Libraries.getLibC();

            handles.put("mmap", NativeInstall.install(libC.getExportAddr("mmap"), "(JJIIII)J"));
        } catch (Throwable thr) {
            throw new ExceptionInInitializerError(thr);
        }
    }

}
