global print


section .text
print:
	mov ecx,[esp+4]
	mov edx,[esp+8]
	mov eax,4
	mov ebx,1
	int 80h
	ret

