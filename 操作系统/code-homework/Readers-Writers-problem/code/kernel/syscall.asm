
; ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
;                               syscall.asm
; ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
;                                                     Forrest Yu, 2005
; ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

%include "sconst.inc"

extern disp_str

_NR_get_ticks       equ 0 ; 要跟 global.c 中 sys_call_table 的定义相对应！
_NR_disp_str        equ 1
_NR_p               equ 2
_NR_v               equ 3
_NR_delay           equ 4
INT_VECTOR_SYS_CALL equ 0x90

; 导出符号
global	get_ticks
global  disp_str_own
global  sys_disp_str
global  delay_own

global 	p
global 	v

bits 32
[section .text]

; ====================================================================
;                              get_ticks
;                      得到当前总共发生了多少时钟中断
; ====================================================================
get_ticks:
	mov	eax, _NR_get_ticks
	int	INT_VECTOR_SYS_CALL
	ret

; 不分配时间片的延迟函数
delay_own:
   mov	eax, _NR_delay
   push ebx
   ; 传递参数
   mov ebx,[esp+8]
   int INT_VECTOR_SYS_CALL
   pop ebx
   ret


; 打印字符串,调用sys_disp_str函数
disp_str_own:
    mov	eax, _NR_disp_str
    ; 将参数压栈到ebx中，方便后续调用
    push ebx
    mov ebx, [esp+8]
    int	INT_VECTOR_SYS_CALL
    pop ebx
    ret

sys_disp_str:
    pusha
    ; 传递参数
    push ebx
    call disp_str
    pop ebx
    popa
    ret


p:
	mov eax, _NR_p
	; 传递参数
	push ebx
	mov ebx, [esp+8]
	; 调用sys_p
	int INT_VECTOR_SYS_CALL
	pop ebx
	ret


v:
	mov eax, _NR_v
	; 传递参数
	push ebx
	mov ebx, [esp+8]
	; 调用sys_v
	int INT_VECTOR_SYS_CALL
	pop ebx
	ret
