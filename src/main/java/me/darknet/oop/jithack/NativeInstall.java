package me.darknet.oop.jithack;

import me.darknet.oop.Universe;
import me.darknet.oop.jithack.arch.AMD64LinuxAssembler;
import me.darknet.oop.util.Unsafe;
import me.darknet.oop.util.UnsafeAccessor;
import me.darknet.oop.util.VMUtil;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class NativeInstall {

    private static final Unsafe unsafe = UnsafeAccessor.getUnsafe();

    private static final Map<Long, MethodHandle> installed = new HashMap<>();

    public static void install(long nativeEntryPoint, long nativeJavaMethod, String signature) {
        Assembler assembler = new AMD64LinuxAssembler();
        assembler.emitPrologue();
        assembler.emitConvention(Type.getArgumentTypes(signature));
        assembler.emitCall(nativeEntryPoint);
        assembler.emitEpilogue();
        byte[] code = assembler.assemble();
        StringBuilder hex = new StringBuilder();
        for (byte b : code) {
            hex.append(String.format("%02x", b));
        }
        System.out.println(hex);
        unsafe.writeBytes(code, nativeJavaMethod);
    }

    public static MethodHandle install(long nativeEntryPoint, String signature) throws Throwable {
        if (installed.containsKey(nativeEntryPoint)) {
            return installed.get(nativeEntryPoint);
        }
        // compile class using asm
        String className = "me/darknet/oop/jithack/HotMethod#" + Long.toHexString(nativeEntryPoint);
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        cw.visit(52, Opcodes.ACC_PUBLIC, className, null, "java/lang/Object", null);
        MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, "invoke", signature,
                null, null);
        Type returnType = Type.getReturnType(signature);
        Type[] argumentTypes = Type.getArgumentTypes(signature); // for setting up callframe
        mv.visitCode();
        switch (returnType.getSort()) {
            case Type.VOID:
                mv.visitInsn(Opcodes.RETURN);
                break;
            case Type.BOOLEAN:
            case Type.BYTE:
            case Type.CHAR:
            case Type.SHORT:
            case Type.INT:
                mv.visitInsn(Opcodes.ICONST_0);
                mv.visitInsn(Opcodes.IRETURN);
                break;
            case Type.LONG:
                mv.visitInsn(Opcodes.LCONST_0);
                mv.visitInsn(Opcodes.LRETURN);
                break;
            case Type.FLOAT:
                mv.visitInsn(Opcodes.FCONST_0);
                mv.visitInsn(Opcodes.FRETURN);
                break;
            case Type.DOUBLE:
                mv.visitInsn(Opcodes.DCONST_0);
                mv.visitInsn(Opcodes.DRETURN);
                break;
            case Type.OBJECT:
                mv.visitInsn(Opcodes.ACONST_NULL);
                mv.visitInsn(Opcodes.ARETURN);
                break;
        }
        mv.visitMaxs(0, 0);
        mv.visitEnd();

        byte[] code = cw.toByteArray();
        Class<?> clazz = unsafe.defineAnonymousClass(NativeInstall.class, code, null);

        unsafe.ensureClassInitialized(clazz);

        Class<?>[] types = new Class<?>[argumentTypes.length];
        for (int i = 0; i < argumentTypes.length; i++) {
            types[i] = VMUtil.getMirrorType(argumentTypes[i]);
        }

        Method method = clazz.getMethod("invoke", types);

        install(nativeEntryPoint, VMUtil.makeHot(Universe.getFromClass(clazz).asInstance(),
                method, null).getNativeEntry(), signature);

        MethodHandle handle = MethodHandles.lookup().unreflect(method);

        installed.put(nativeEntryPoint, handle);
        return handle;
    }

}
