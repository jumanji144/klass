; sys_write example
push    0x0a646c72
push    0x6f57206f
push    0x6c6c6548
mov rdi, 0x1
mov rsi, rsp
mov rdx, 0x0C
mov rax, 0x1

syscall

ret