package me.darknet.oop.jithack.arch;

import me.darknet.oop.jithack.Assembler;
import org.objectweb.asm.Type;

public class AMD64LinuxAssembler extends AbstractAssembler {

    // x64 calling convention (Linux, macOS):
    //     Java: rsi, rdx, rcx,  r8,  r9, rdi, stack
    //   Native: rdi, rsi, rdx, rcx,  r8,  r9, stack

    public AMD64LinuxAssembler() {
        super(
                new int[] { // 32 bit mov
                    0x89f7, // mov edi, esi
                    0x89d6, // mov esi, edx
                    0x89ca, // mov edx, ecx
                    0x4489c1, // mov ecx, r8d
                    0x4589c8, // mov eax, r9d
                    0x4189c1, // mov r9d, eax
                },
                new int[] { // 64 bit mov
                    0x4889f7, // mov rdi, rsi
                    0x4889d6, // mov rsi, rdx
                    0x4889ca, // mov rdx, rcx
                    0x4c89c1, // mov rcx, r8
                    0x4d89c8, // mov r8, r9
                    0x4989c1, // mov r9, rax
                });
    }

    @Override
    public void emitConvention(Type[] args) {
        if(args.length >= 6) { // rdi clashes with rsi
            put(0x48, 0x89, 0xF8); // mov rax, rdi
        }
        super.emitConvention(args);
    }

    @Override
    public void emitPrologue() {
    }

    @Override
    public void emitCall(long address) {
        // movabs rax, address
        put(0x48, 0xB8);
        code.putLong(address);
        // jmp rax
        put(0xFF, 0xE0);
    }

    @Override
    public void emitEpilogue() {
    }
}
