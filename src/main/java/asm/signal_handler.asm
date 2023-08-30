; calling parameters:
; rsi - signal number
; rdx - new sigaction
; rcx - old sigaction
; r8 - sigset size

mov rax, 13
mov rdi, rsi
mov rsi, rdx
mov rdx, rcx
mov r10, r8

syscall

ret

; signal handler
; calling parameters come from the kernel now
; rdi - signal number
; rsi - siginfo_t
; rdx - ucontext_t

; jump to next instruction
; (we don't want to

; restore registers from ucontext_t
        mov     rax, rdx
        mov     rdx, QWORD PTR [rdx+160]
        mov     rdx, QWORD PTR [rax+120]
        mov     rdx, QWORD PTR [rax+72]
        mov     rdx, QWORD PTR [rax+80]
        mov     rdx, QWORD PTR [rax+88]
        mov     rdx, QWORD PTR [rax+96]
        mov     rdx, QWORD PTR [rax+104]
        mov     rdx, QWORD PTR [rax+112]
        mov     rdx, QWORD PTR [rax+128]
        mov     rdx, QWORD PTR [rax+136]
        mov     rdx, QWORD PTR [rax+152]
        mov     rdx, QWORD PTR [rax+144]
        mov     rdx, QWORD PTR [rax+40]
        mov     rdx, QWORD PTR [rax+48]
        mov     rdx, QWORD PTR [rax+56]
        mov     rax, QWORD PTR [rax+64]
mov rax, 0x7fff56a034503
jmp rax

ret