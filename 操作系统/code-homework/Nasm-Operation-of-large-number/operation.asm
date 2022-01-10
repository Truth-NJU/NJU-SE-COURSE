; 数据段：用于声明初始化数据或常量的数据段
section .data
msg: db "Please input x and y:",0Ah
len: equ $-msg
minus: db "-"
lenMinus: equ $-minus
line: db "", 0Ah
lineLength: equ $-line
zero: db "0"
zeroLen: equ $-zero
SYS_EXIT: equ 1
SYS_READ: equ 3
SYS_WRITE: equ 4
STDIN: equ 0
STDOUT: equ 1

; 声明变量
section .bss
num1Address: resw 20  	; 输入第一个数字的首地址
num2Address: resw 20	; 输入第二个数字的首地址
num4Address: resw 20  	; 输入第一个数字的首地址，用于备份，防止加法影响到乘法
num5Address: resw 20	; 输入第二个数字的首地址，用于备份，防止加法影响到乘法
num1Len: resb 4			; 输入第一个数字的长度
num2Len: resb 4			; 输入第二个数字的长度

flag1: resb 1			; 第一个数字的符号
flag2: resb 1			; 第二个数字的符号

;-------------------
; 加法中需要使用的变量
num1Place: resb 4		; 第一个数字的首地址
num2Place: resb 4		; 第二个数字的首地址
num1Index: resb 4		; 第一个数字的指针，指向当前位
num2Index: resb 4		; 第二个数字的指针，指向当前位
num3Length: resb 4		; 加法结果的长度
num3Address: resw 40	; 加法结果的地址
num3Sign: resb 1		; 加法结果的符号，正为0，负为1, 2代表0
index1: resb 4
index2: resb 4
tempIndex: resb 4
lenDifference: resb 4	; 两数长度之差
tempDifference: resb 4
zeroAfter: resb 4		; 由于加法结果在内存中逆序存放，那么要消除输出的前导0，就要去除内存中的最后几位的0，记录0的个数
zeroIndex: resb 4

;-------------------
; 乘法中使用的变量

mulResultAddress: resw 40	; 乘法结果的首地址
mulResultLen: resb 4		; 乘法结果的长度
temp: resb 4
zeroNum: resb 4				; 每一位后面需要补0的个数
mulNum: resb 1				; 第二个数字当前位的数字
mulCarry: resb 4			; 进位
mulOneResultAddress: resw 40		; 一个数乘一个数字的结果
mulOneLen: resb 4			; 一个数乘一个数字的结果的长度
addMulDigitAddress: resw 40	;一个数乘一个数字的结果的正序的首地址（正序：1120）
temp2: resb 4
cnt1: resb 4				; 第一个数字当前位置的指针
cnt2: resb 4				; 第二个数字当前的指针
mulResultZeroNum: resb 4	; 乘法结果中0的个数

; 输出
printIndex: resb 4
curNum: resb 1
index: resb 4

inputNumByte: resb 1	; 获取的每一位的输入

; 文本段 保存实际的代码
section .text
global _start

_start:
	; 输出Please input x and y:
	mov eax, SYS_WRITE
	mov ebx, STDOUT
	mov ecx, msg
	mov edx, len
	int 80h

	call input


	;两个数字都是负的，flag1=1 flag2=1
	mov dh,byte[flag1]
	mov dl,byte[flag2]
	add dh,dl
	cmp dh,2
	je minusAdd

	jmp positiveAdd

	minusAdd:
		pusha 
		mov dword[num1Place], num1Address
		mov dword[num2Place], num2Address
		mov eax,dword[num1Len]
		mov dword[num1Index],eax
		mov eax,dword[num2Len]
		mov dword[num2Index],eax

		call add

		mov dword[num1Place], 0
		mov dword[num2Place], 0
		mov dword[num1Index],0
		mov dword[num2Index],0

		popa

		; 两个负数相加为负数，输出负号
		mov eax, SYS_WRITE
		mov ebx, STDOUT
		mov ecx, minus
		mov edx, lenMinus
		int 0x80
		jmp out

	; 两个数都是正的
	positiveAdd:
		mov dh,byte[flag1]
		mov dl,byte[flag2]
		add dh,dl
		cmp dh,0
		je Add

		jmp sub

	; 两个数都是正的
	Add:
		pusha 
		mov dword[num1Place], num1Address
		mov dword[num2Place], num2Address
		mov eax,dword[num1Len]
		mov dword[num1Index],eax
		mov eax,dword[num2Len]
		mov dword[num2Index],eax

		call add

		mov dword[num1Place], 0
		mov dword[num2Place], 0
		mov dword[num1Index],0
		mov dword[num2Index],0

		popa
		jmp out

	; 数字一正一负，做减法
	sub:
		pusha 
		mov dword[num1Place], num1Address
		mov dword[num2Place], num2Address
		mov eax,dword[num1Len]
		mov dword[num1Index],eax
		mov eax,dword[num2Len]
		mov dword[num2Index],eax

		; 一个负数加上一个正数
		call minusAddpositive

		mov dword[num1Place], 0
		mov dword[num2Place], 0
		mov dword[num1Index],0
		mov dword[num2Index],0

		popa

		; num3Sign为1，代表负数，需要输出负号
		cmp byte[num3Sign],1
		je outMinus

		cmp byte[num3Sign],2
		; 结果为0 输出0
		je cout0

		jmp out

		outMinus:
			mov eax, SYS_WRITE
			mov ebx, STDOUT
			mov ecx, minus
			mov edx, lenMinus
			int 0x80
			jmp out

		cout0:
			mov eax, SYS_WRITE
			mov ebx, STDOUT
			mov ecx, zero
			mov edx, zeroLen
			int 0x80

			mov eax, SYS_WRITE
			mov ebx, STDOUT
			mov ecx, line
			mov edx, lineLength
			int 0x80
			jmp exit

	out:
		pusha
		mov eax, dword[num3Length]
		mov dword[zeroIndex],eax

		mov dword[printIndex],0

		; 统计num3有多少个前导0
		getPrintIndex:
			; 取出结果最后的值
			mov eax,num3Address
			add eax, dword[zeroIndex]
			mov dl,byte[eax]
			sub dl,48

			cmp dl,0
			;第一次出现不为0，就要停止循环
			jg res

			inc dword[printIndex]
			dec dword[zeroIndex]
			jmp getPrintIndex

		res:
			; 此时printIndex存放的是有多少个前导0，用结果的长度减去它就可以得到结果的有效长度
			mov eax,dword[num3Length]
			mov ebx,dword[printIndex]
			sub eax,ebx
			mov dword[printIndex],eax
			;从高位进行输出
			call backwordOutput
			popa
		


	exit:
		pusha
		mov dword[num1Place], num4Address
		mov dword[num2Place], num5Address
		mov eax,dword[num2Len]
		mov dword[num2Index],eax
		mov eax,dword[num1Len]
		mov dword[num1Index],eax
		popa

		call mul

		pusha

		mov eax,0
		mov dword[index], eax
		;从低位进行输出
		call output
		popa


		mov eax, SYS_EXIT
		xor ebx, ebx 
		int 80h






;--------------------------
; 获取输入
input:
	pusha

	mov dword[num1Len],0
	mov byte[flag1],0
	mov dword[num2Len],0
	mov byte[flag2],0


	; 获取第一个数字的符号
	mov eax, SYS_READ
	mov ebx, STDIN	
	mov ecx, inputNumByte
	mov edx, 1
	int 80h

	mov dh,byte[minus]
	cmp byte[inputNumByte],dh
	je minusOneSign

	; 若第一位不是负号，那就是数字，需要读入
	mov eax, num1Address
	add eax, dword[num1Len]
	mov cl,byte[inputNumByte]
	mov byte[eax], cl

	; 用于备份数字1，供乘法使用
	mov eax, num4Address
	add eax, dword[num1Len]
	mov cl,byte[inputNumByte]
	mov byte[eax], cl

	inc dword[num1Len]
	jmp getNum1Loop

	minusOneSign:
		mov byte[flag1],1

	getNum1Loop:
		; 一位一位获取输入
		mov eax, SYS_READ
		mov ebx, STDIN	
		mov ecx, inputNumByte
		mov edx, 1
		int 80h

		; num1以空格为终止符号,第一个数字读完就去读第二个数字
		cmp byte[inputNumByte]," "
		je initNum2

		; 首地址加上数字的长度即为要将数字放入的位置
		mov eax, num1Address
		add eax, dword[num1Len]
		mov cl,byte[inputNumByte]
		mov byte[eax], cl

		mov eax, num4Address
		add eax, dword[num1Len]
		mov cl,byte[inputNumByte]
		mov byte[eax], cl

		inc dword[num1Len]

		jmp getNum1Loop

	initNum2:
		mov byte[inputNumByte], 0
		; 获取第二个数字的符号
		mov eax, SYS_READ
		mov ebx, STDIN	
		mov ecx, inputNumByte
		mov edx, 1
		int 80h

		mov dh,byte[minus]
		cmp byte[inputNumByte], dh
		; 读取到负号
		je minusTwoSign

		; 第一位不是负号，需要读入数字
		mov eax, num2Address
		add eax, dword[num2Len]
		mov cl,byte[inputNumByte]
		mov byte[eax], cl

		; 用于备份数字1，供乘法使用
		mov eax, num5Address
		add eax, dword[num2Len]
		mov cl,byte[inputNumByte]
		mov byte[eax], cl

		inc dword[num2Len]
		jmp getNum2Loop
		
		minusTwoSign:
			mov byte[flag2],1
			jmp getNum2Loop

	getNum2Loop:
		; 一位一位获取输入
		mov eax, SYS_READ
		mov ebx, STDIN	
		mov ecx, inputNumByte
		mov edx, 1
		int 80h

		; num2以回车为终止符号
		cmp byte[inputNumByte],0Ah
		je inputFinished

		; 首地址加上数字的长度即为要将数字放入的位置
		mov eax, num2Address
		add eax, dword[num2Len]
		mov cl,byte[inputNumByte]
		mov byte[eax], cl

		mov eax, num5Address
		add eax, dword[num2Len]
		mov cl,byte[inputNumByte]
		mov byte[eax], cl

		inc dword[num2Len]

		jmp getNum2Loop

inputFinished:
	popa
	ret



; --------------------------
; 乘法
; 用第二个数字的每一位去乘第一个数字，并将结果相加
mul:
	pusha
	mov eax, dword[num2Len]
	mov dword[cnt2], eax


	mov dword[mulResultLen],0
	mov dword[mulResultAddress],0
	mov dword[zeroNum],0
	mov dword[temp],0
	
	mulLoop:
		cmp dword[cnt2],0
		je  mulFinished

		; 循环取出第二个数字的每一位,从最后一位开始
		mov eax,num5Address
		add eax,dword[cnt2]
		dec eax
		mov dl,byte[eax]
		; 将字符串转为数字
		sub dl,48
		mov byte[mulNum],dl


		; 将第一个数字的长度赋给cnt1，作为初始值
		mov eax, dword[num1Len]
		mov dword[cnt1], eax

		mov dword[mulOneResultAddress],0
		mov dword[mulCarry],0
		mov dword[mulOneLen],0

		; 将需要补充的0个数赋给temp临时变量
		mov eax,dword[zeroNum]
		mov dword[temp],eax

		; 在低地址补zeroNum个0，即在数字的最后补0
		fillZero:
			; 完成补0
			cmp dword[temp],0
			je fillFinished

			; 补0
			mov eax,mulOneResultAddress
			add eax,dword[mulOneLen]
			mov byte[eax],48

			inc dword[mulOneLen]
			dec dword[temp]

			jmp fillZero

		fillFinished:
			inc dword[zeroNum]
			jmp mulFirstNumLoop

		mulFirstNumLoop:
			;第一个数字长度为0，代表已经乘完
			cmp dword[cnt1],0
			je finish

			mov ebx,num4Address
			add ebx,dword[cnt1]	
			dec ebx
			mov eax,0
			mov al,byte[ebx]
			; 将字符串转为数字
			sub eax,48

			; 相乘后结果放在eax中
			mul byte[mulNum]
			; 将eax存放到ecx中
			mov ecx,eax
			add ecx,dword[mulCarry]
			mov dword[mulCarry],0

			; 循环处理进位
			carryBit:
				cmp ecx,10
				; ecx<10,代表没有进位
				jl noCarry

				; 处理进位
				sub ecx,10
				inc dword[mulCarry]

				jmp carryBit

			; 完成进位,将结果存到对应位置，从高往低存，如1120，存为0211
			noCarry:
				mov eax,mulOneResultAddress
				add eax,dword[mulOneLen]
				add cl,48
				mov byte[eax],cl

				inc dword[mulOneLen]
				dec dword[cnt1]
				jmp mulFirstNumLoop

			finish:
				;检查高位进位
				cmp dword[mulCarry], 0
				; 若没有进位则清空进位值
				je multiplyOneDigitReslt

				; 存在进位，将进位值加上，加在内存中的最后面一位，即数字的第一位
				mov eax, mulOneResultAddress
				add eax, dword[mulOneLen]
				mov cl, byte[mulCarry]
				; 将数字转为字符串
				add cl, 48
				mov byte[eax], cl

				inc dword[mulOneLen]

		multiplyOneDigitReslt:
				; 清空进位值
				mov dword[mulCarry], 0


		; 一个数乘一个数字的结果从高往低存，需要反过来
		call reverse

		; 将第二个数字的一位与第一个数字相乘的结果加到乘法的结果当中
		mulAdd:
			; 执行加法
			pusha
			mov dword[num1Place], addMulDigitAddress	
			mov dword[num2Place], mulResultAddress

			mov eax, dword[mulOneLen]
			; 第一个加数的长度
			mov dword[num1Index], eax

			; 第二个加数的长度
			mov eax, dword[mulResultLen]
			mov dword[num2Index], eax

			call add

			popa
			
			; 加法结果的长度
			mov eax, dword[num3Length]
			mov dword[mulResultLen], eax
			mov dword[temp2],eax
			
		; 将加法结果赋值给乘法结果
		assignVal:
			cmp dword[temp2],-1
			je assignFinished

			mov ebx,num3Address
			add ebx,dword[temp2]


			mov eax,mulResultAddress
			sub eax,dword[temp2]
			add eax, dword[mulResultLen]
			dec eax

			mov cl,byte[ebx]
			mov byte[eax],cl

			dec dword[temp2]
			jmp assignVal

		assignFinished:
			; 计算第二个数字前一位乘以第一个数字
			dec dword[cnt2]
			; 临时变量清0
			mov dword[temp2],0
			mov dword[mulOneLen],0
			jmp mulLoop

mulFinished:
	popa
	ret


reverse:
	pusha
	mov eax, 0


	reverseLoop:
		cmp eax, dword[mulOneLen]
		; 若eax的长度等于一个数乘一个数字的乘法结果的长度，就结束
		je reverseFinished

		; 将一个数乘一个数字的乘法结果的首地址给ebx
		mov ebx, mulOneResultAddress
		add ebx, eax
		
		mov ecx, addMulDigitAddress
		add ecx, dword[mulOneLen]
		sub ecx, eax
		dec ecx

		; 将ebx的值给ecx，即ebx-eax=ecx+eax，即将一个数乘一个数字的乘法结果反过来存储，一开始为0121，调整后为1210
		mov dl, byte[ebx]
		mov byte[ecx], dl

		inc eax

		jmp reverseLoop
		
reverseFinished:
	popa
	ret





;-----------------------	
; 负数加正数
minusAddpositive:
	mov dword[index1],0
	mov dword[index2],0

	; 比较两个数的大小，确定最后的符号
	mov eax,dword[num1Len]
	; 两个数长度相等需要进一步判断
	cmp dword[num2Len],eax
	je checkSign

	cmp dword[num2Len],eax
	jl decideSign

	;num2Len>num1Len
	mov cl,byte[flag2]
	mov byte[num3Sign],cl
	jmp sub1

	; num2Len<num1Len
	decideSign:
		mov cl,byte[flag1]
		mov byte[num3Sign],cl
		jmp sub2

	; 将两个数字从前往后一位一位的比较
	checkSign:
		mov eax,dword[num1Len]
		; num1=num2 输出0
		cmp dword[index1],eax
		je out0


		mov eax,dword[num1Place]
		add eax, dword[index1]
		; 将数字存到dh寄存器
		mov dh, byte[eax]
		; 将字符串转为数字
		sub dh,48

		; 得到num2的当前位的地址
		mov eax,dword[num2Place]
		add eax, dword[index2]
		; 将数字存到dh寄存器
		mov dl, byte[eax]
		; 将字符串转为数字
		sub dl,48

		cmp dh,dl
		; dh,dl内值相等，就判断下一位
		je nextCheck

		cmp dh,dl
		jl num2Bigger

		; num1更大
		mov cl,byte[flag1]
		mov byte[num3Sign],cl
		jmp sub2

		num2Bigger:
			mov cl,byte[flag2]
			mov byte[num3Sign],cl
			jmp sub1

		; 继续判断下一位
		nextCheck:
			inc dword[index1]
			inc dword[index2]
			jmp checkSign


	out0:
		; 输出0
		mov byte[num3Sign],2
		ret


	;num2-num1
	sub1:
		mov dword[num3Length],0

		dec dword[num1Index]
		dec dword[num2Index]

		sub1Loop:
			cmp dword[num1Index],-1
			je finishSub

			; 得到num1的最后一位的地址
			mov eax,dword[num1Place]
			add eax, dword[num1Index]
			; 将数字存到dh寄存器
			mov dh, byte[eax]
			; 将字符串转为数字
			sub dh,48

			; 得到num2的最后一位的地址
			mov eax,dword[num2Place]
			add eax, dword[num2Index]
			; 将数字存到dh寄存器
			mov dl, byte[eax]
			; 将字符串转为数字
			sub dl,48

			; dl<dh,即数字2的当前位小于数字1的当前位，需要借位
			cmp dl,dh
			jl needBorrow

			; 不需要借位,数字2的当前位大于于数字1的当前位
			sub dl,dh
			jmp subOnefinished

			needBorrow:
				furtherBorrow:
					; 向当前位的前一位借位
					mov ecx,dword[num2Index]
					mov dword[tempIndex], ecx
					
					
					furtherBorrowLoop:
						dec dword[tempIndex]
						mov eax,dword[num2Place]
						add eax,dword[tempIndex]
						mov cl,byte[eax]
						sub cl,48
	
						; 当前位为0，需要继续向上借位
						cmp cl, 0
						je change

						; 当前位不为0
						dec cl
						add cl,48
						mov byte[eax],cl
						jmp subOnefinishedWithBorrow

						change:
							; 将当前为0的位置置为9
							mov byte[eax],57
							; 继续向上借位
							jmp furtherBorrowLoop


				; 不需要借位，直接将减了的结果赋值给num3
				subOnefinished:
					mov eax,num3Address
					add eax,dword[num3Length]
					add dl,48
					mov byte[eax],dl
					inc dword[num3Length]

					dec dword[num1Index]
					dec dword[num2Index]

					jmp sub1Loop

				; 需要借位
				subOnefinishedWithBorrow:
					add dl,10
					sub dl,dh
					mov eax,num3Address
					add eax,dword[num3Length]
					add dl,48
					mov byte[eax],dl
					inc dword[num3Length]

					dec dword[num1Index]
					dec dword[num2Index]

					jmp sub1Loop


	finishSub:
		cmp dword[num2Index],0
		jg num2Longer

		;将num2剩余的位放到对应位置上
		num2Longer:
			cmp dword[num2Index],-1
			je finishAll
			; 得到num2的最后一位的地址
			mov eax,dword[num2Place]
			add eax, dword[num2Index]
			; 将数字存到dh寄存器
			mov dl, byte[eax]

			mov eax,num3Address
			add eax,dword[num3Length]

			mov byte[eax],dl
			inc dword[num3Length]
			dec dword[num2Index]

			jmp num2Longer


		
	;num1-num2
	sub2:
		mov dword[num3Length],0

		dec dword[num1Index]
		dec dword[num2Index]

		sub2Loop:
			cmp dword[num2Index],-1
			je finishSub2

			; 得到num1的最后一位的地址
			mov eax,dword[num1Place]
			add eax, dword[num1Index]
			; 将数字存到dh寄存器
			mov dh, byte[eax]
			; 将字符串转为数字
			sub dh,48

			; 得到num2的最后一位的地址
			mov eax,dword[num2Place]
			add eax, dword[num2Index]
			; 将数字存到dh寄存器
			mov dl, byte[eax]
			; 将字符串转为数字
			sub dl,48

			; dh<dl,即数字1的当前位小于数字2的当前位，需要借位
			cmp dh,dl
			jl needBorrow2

			; 不需要借位,数字2的当前位大于于数字1的当前位
			sub dh,dl
			jmp subOnefinished2

			needBorrow2:
				cmp dh,0
				; dh=0,需要继续向上借位
				je furtherBorrow2

				furtherBorrow2:
					; 向当前位的前一位
					mov ecx,dword[num1Index]
					mov dword[tempIndex], ecx

					furtherBorrowLoop2:
						dec dword[tempIndex]
						mov eax,dword[num1Place]
						add eax,dword[tempIndex]
						mov cl,byte[eax]
						sub cl,48
	
						; 当前位为0，需要继续向上借位
						cmp cl, 0
						je change2

						; 当前位不为0
						dec cl
						add cl,48
						mov byte[eax],cl
						jmp subOnefinishedWithBorrow2

						change2:
							; 将当前为0的位置置为9
							mov byte[eax],57
							jmp furtherBorrowLoop2



				subOnefinished2:
					mov eax,num3Address
					add eax,dword[num3Length]
					add dh,48
					mov byte[eax],dh
					inc dword[num3Length]

					dec dword[num1Index]
					dec dword[num2Index]

					jmp sub2Loop

				subOnefinishedWithBorrow2:
					add dh,10
					sub dh,dl
					mov eax,num3Address
					add eax,dword[num3Length]
					add dh,48
					mov byte[eax],dh
					inc dword[num3Length]

					dec dword[num1Index]
					dec dword[num2Index]

					jmp sub2Loop

	finishSub2:
		cmp dword[num1Index],0
		jg num1Longer

		;将num1剩余的位放到对应位置上
		num1Longer:
			cmp dword[num1Index],-1
			je finishAll
			; 得到num2的最后一位的地址
			mov eax,dword[num1Place]
			add eax, dword[num1Index]
			; 将数字存到dh寄存器
			mov dl, byte[eax]

			mov eax,num3Address
			add eax,dword[num3Length]

			mov byte[eax],dl
			inc dword[num3Length]
			dec dword[num1Index]

			jmp num1Longer


	finishAll:
		ret




;----------------------------
; 加法
add:
	mov ebx,0 	;存储加法的进位
	mov dword[num3Length],0

	dec dword[num1Index]
	dec dword[num2Index]

	addLoop:
		cmp dword[num2Index],-1
		; 数字1的长度比数字2长,数字1有剩余位
		je  num1Left
		cmp dword[num1Index],-1
		; 数字2的长度比数字1长,数字2有剩余位
		je	num2Left

		; 得到num1的最后一位的地址
		mov eax,dword[num1Place]
		add eax, dword[num1Index]
		; 将数字存到dh寄存器
		mov dh, byte[eax]
		; 将字符串转为数字
		sub dh,48

		; 得到num2的最后一位的地址
		mov eax,dword[num2Place]
		add eax, dword[num2Index]
		; 将数字存到dh寄存器
		mov dl, byte[eax]
		; 将字符串转为数字
		sub dl,48

		cmp ebx, 0
		; 如果ebx=0，则不需要加1
		je compare

		; ebx=1，需要加1，并且置空ebx
		inc dh
		mov ebx, 0

		compare:
			; 进行两位的加法
			add dh, dl

			; 检查是否产生进位
			cmp dh, 10
			; 如果dh<10则执行jl后面的内容
			jl noEbx
			; 如果产生进位，进位减10
			mov ebx, 1
			sub dh, 10

		noEbx:
			add dh,48

			; 得到结果位存放的位置, 从低到高存放，即数字1120被存放位0211
			mov eax,num3Address
			add eax,dword[num3Length]
			; 将结果存放到对应的位置
			mov byte[eax],dh
			inc dword[num3Length]

			; 计算前一位
			dec dword[num1Index]
			dec dword[num2Index]

			jmp addLoop

	num1Left:
		cmp dword[num1Index],-1
		; 第1个数字较长且计算完成
		je addFinished

		; 取出数字1剩余的对应的位置的值并放入dl寄存器
		mov eax,dword[num1Place]
		add eax,dword[num1Index]
		mov dl,byte[eax]
		; 将地址中存放的字符串转为数字
		sub dl,48

		cmp ebx, 0
		; 如果ebx=0，则不需要加1
		je no1Ebx

		; ebx=1，需要加1，并且置空ebx
		inc dl
		mov ebx, 0

		cmp dl, 10
		jl no1Ebx

		; dl>10，产生进位
		sub dl, 10
		mov ebx, 1


	no1Ebx:
		add dl,48
		; 将当前dl中的数字放到对应位置上
		mov eax, num3Address 
		add eax, dword[num3Length] 
		mov byte[eax], dl

		inc dword[num3Length]
		dec dword[num1Index]
		jmp num1Left

	num2Left:
		cmp dword[num2Index],-1
		; 第2个数字较长且计算完成
		je addFinished


		; 取出数字2剩余的对应的位置的值并放入dl寄存器
		mov eax,dword[num2Place]
		add eax,dword[num2Index]
		mov dl,byte[eax]
		; 将地址中存放的字符串转为数字
		sub dl,48

		cmp ebx, 0
		; 如果ebx=0，则不需要加1
		je no2Ebx

		; ebx=1，需要加1，并且置空ebx
		inc dl
		mov ebx, 0

		cmp dl, 10
		jl no2Ebx

		; dl>10，产生进位
		sub dl, 10
		mov ebx, 1

	no2Ebx:
		add dl,48
		; 将当前dl中的数字放到对应位置上
		mov eax, num3Address 
		add eax, dword[num3Length] 
		mov byte[eax], dl

		inc dword[num3Length]
		dec dword[num2Index]
		jmp num2Left


	addFinished:
		; 检查最后一位是否产生进位
		cmp ebx,0
		je notOverFlow

		; 产生进位，向上溢出
		mov eax,num3Address
		add eax,dword[num3Length]
		mov dl,31h
		mov byte[eax],dl
		inc dword[num3Length]

		notOverFlow:
			mov dword[num1Place], 0
			mov dword[num2Place], 0
			ret






;---------------------
; 从低往高输出
output:
	mov dword[mulResultZeroNum],0
	; 解决结果为0 输出多个0的问题
	zeroNum2:
		mov eax,dword[mulResultLen]
		cmp dword[index],eax
		je check

		; 取得最后位置的数字
		mov eax,mulResultAddress
		add eax,dword[index]
		mov dl,byte[eax]
		sub dl,48
		cmp dl,0
		je addNum

		jmp nextStep

		addNum:
			inc dword[mulResultZeroNum]

		nextStep:
			inc dword[index]

		jmp zeroNum2

	check:
		mov eax,dword[mulResultLen]
		cmp dword[mulResultZeroNum],eax
		je output0

		mov eax,0
		mov dword[index], eax

		jmp outputMin

	output0:
		mov eax, SYS_WRITE
		mov ebx, STDOUT
		mov ecx, zero
		mov edx, zeroLen
		int 0x80
		jmp outputFinished
		ret

	outputMin:
		mov dh,byte[flag1]
		mov dl,byte[flag2]
		add dh,dl
		cmp dh,1
		je outputMinus

		jmp outputLoop

		; 输出负号
		outputMinus:
			mov eax, SYS_WRITE
			mov ebx, STDOUT
			mov ecx, minus
			mov edx, lenMinus
			int 0x80

	outputLoop:
		mov eax,dword[mulResultLen]
		cmp dword[index],eax
		je outputFinished

		; 取得最后位置的数字
		mov eax,mulResultAddress
		add eax,dword[index]
		mov dl,byte[eax]
		mov byte[curNum],dl


		inc dword[index]

		; 输出当前位的数字
		mov eax, SYS_WRITE
		mov ebx, STDOUT
		mov ecx, curNum
		mov edx, 1
		int 0x80

		jmp outputLoop






;------------------------------
; 从高到低输出，即1120在内存中存为0211,输出时要从后往前输出
; 由于是从后往前输出，因此需要消除最后面的0
backwordOutput:
	cmp dword[printIndex],-1
	je outputFinished
	
	; 取得最后位置的数字
	mov eax,num3Address
	add eax,dword[printIndex]
	mov dl,byte[eax]
	mov byte[curNum],dl

	dec dword[printIndex]

	; 输出当前位的数字
	mov eax, SYS_WRITE
	mov ebx, STDOUT
	mov ecx, curNum
	mov edx, 1
	int 0x80

	jmp backwordOutput


outputFinished:
	; 输出回车，用于换行
	mov eax, SYS_WRITE
	mov ebx, STDOUT
	mov ecx, line
	mov edx, lineLength
	int 0x80
	ret
