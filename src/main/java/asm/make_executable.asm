; GCC linux
push rax
push rdi
push rsi
push rdx
push rcx
push r8
push r9
mov     rax, rcx
mov     rdi, rsi
mov     rsi, rdx
lea     edx, [4*rcx + 3]
mov     ecx, 50
mov     r8d, -1
xor     r9d, r9d
call    mmap@PLT
pop    r9
pop    r8
pop    rcx
pop    rdx
pop    rsi
pop    rdi
pop    rax
ret


; MSVC x64
sub     rsp, 40
xor     r9d, r9d
mov     r8, rcx
mov     rcx, rsi
call    QWORD PTR __imp_VirtualProtect
xor     eax, eax
add     rsp, 40
ret     0