package me.darknet.oop.jvm.base;

import me.darknet.oop.jvm.BaseObtainStrategy;
import me.darknet.oop.jvm.JavaThread;
import me.darknet.oop.util.*;

public class ThreadStrategy implements BaseObtainStrategy {

    private static final long eeTopOffset;
    private static long base;

    public static JavaThread currentThread() {
        Unsafe unsafe = UnsafeAccessor.getUnsafe();
        long nativeThread = unsafe.getLong(Thread.currentThread(), eeTopOffset);
        return new JavaThread(nativeThread);
    }

    @Override
    public long getBase() {
        if(base != 0) {
            return base;
        }
        Unsafe unsafe = UnsafeAccessor.getUnsafe();

        long nativeThread = unsafe.getLong(Thread.currentThread(), ThreadStrategy.eeTopOffset);
        JavaThread javaThread = new JavaThread(nativeThread);
        long env = javaThread.getJNIEnv();

        // page align
        long page = UnsafeAccessor.getUnsafe().pageSize();
        long ptr = env & -page;

        ptr -= Libraries.getLibJvm().getSection(".rodata").address();

        boolean is64Bit = Util.is64Bit(); // is os 64 bit
        boolean isLittleEndian = Util.isLittleEndian(); // is os little endian
        Os os = Util.getOs(); // os enum: windows, linux, mac

        // walk downwards
        try {

            final long pageSize = unsafe.pageSize();
            int elf64Bit = is64Bit ? 2 : 1;
            int elfLittleEndian = isLittleEndian ? 1 : 2;

            do {
                ptr -= pageSize;
                if (os == Os.LINUX) {
                    // check for elf header
                    if (unsafe.getByte(ptr) != 0x7f
                            || unsafe.getByte(ptr + 1) != 'E'
                            || unsafe.getByte(ptr + 2) != 'L' || unsafe.getByte(ptr + 3) != 'F') {
                        continue;
                    }
                    // check for architecture
                    if (unsafe.getByte(ptr + 4) != elf64Bit) {
                        continue;
                    }
                    // check for little endian
                    if (unsafe.getByte(ptr + 5) != elfLittleEndian) {
                        continue;
                    }
                    break;
                } else if(os == Os.WINDOWS) {
                    // check for MZ header
                    if (unsafe.getByte(ptr) != 'M'
                            || unsafe.getByte(ptr + 1) != 'Z') {
                        continue;
                    }
                    // check for PE header
                    if (unsafe.getByte(ptr + 0x3c) != 'P'
                            || unsafe.getByte(ptr + 0x3d) != 'E') {
                        continue;
                    }
                    break;
                }
            } while (true);

            return base = ptr;
            // now we have the base, we just look up exports
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    static {
        Unsafe unsafe = UnsafeAccessor.getUnsafe();

        try {
            eeTopOffset = unsafe.objectFieldOffset(Thread.class.getDeclaredField("eetop"));
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }
}
