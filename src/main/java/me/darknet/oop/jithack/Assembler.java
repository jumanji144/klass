package me.darknet.oop.jithack;

import org.objectweb.asm.Type;

public interface Assembler {

    void emitConvention(Type[] args);

    void emitPrologue();

    void emitCall(long address);

    void emitEpilogue();

    byte[] assemble();

}
