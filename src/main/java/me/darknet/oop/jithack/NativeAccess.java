package me.darknet.oop.jithack;

import me.darknet.oop.Universe;
import me.darknet.oop.klass.Method;
import me.darknet.oop.util.Unsafe;
import me.darknet.oop.util.UnsafeAccessor;
import me.darknet.oop.util.VMUtil;

public class NativeAccess {

    private static final long proxyEntry;
    private static final long proxyPage;
    private static final Unsafe unsafe = UnsafeAccessor.getUnsafe();

    /// Hot methods
    static Object convert0(Object object) { return object; }
    static Object convert1(long a) { return a; }
    static void nativeptr0(Object o) {}

    public static long oopToHandle(Object o) {
        nativeptr0(o);

        return unsafe.getAddress(proxyPage);
    }

    public static Object handleToOop(long a) {
        int offset = 2; // 2 bytes for the REX + MOVABS

        unsafe.putAddress(proxyEntry + offset, a);

        return convert1(a);
    }

    static {
        try {
            Universe universe = Universe.obtainFrom(NativeAccess.class);

            // calling convention:
            // pure: rsi, rdx, rcx, r8, r9, rax
            // stack: rsi, rbx, r11, r13
            // this: rsi (first argument)
            // non-void: rdx / rsi (return value)

            Method convertMethod = VMUtil.makeHot(universe,
                    "me.darknet.oop.jithack.NativeAccess.convert0(Ljava/lang/Object;)Ljava/lang/Object;",
                    null);
            Method proxyMethod = VMUtil.install(universe,
                    "me.darknet.oop.jithack.NativeAccess.convert1(J)Ljava/lang/Object;",
                    null,
                    "48BEE8E6CAACDF7F000048B8E8E6CAACDF7F0000FFE0");
            Method nativeptrMethod = VMUtil.install(universe,
                    "me.darknet.oop.jithack.NativeAccess.nativeptr0(Ljava/lang/Object;)V",
                    null,
                    "48B800000000F07F0000488930C3");

            proxyEntry = proxyMethod.getNativeEntry();
            proxyPage = unsafe.allocateMemory(unsafe.pageSize());

            int off = "48BEE8E6CAACDF7F0000".length() / 2 + 2;

            long entry = proxyMethod.getNativeEntry();
            unsafe.putLong(entry + off,     convertMethod.getNativeEntry());

            unsafe.putAddress(nativeptrMethod.getNativeEntry() + 2, proxyPage);
        } catch (Throwable thr) {
            throw new ExceptionInInitializerError(thr);
        }
    }

}
