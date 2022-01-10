# Nasm-Operation-of-large-number
采用Nasm汇编语言编写实现大整数（超过64位）的加法和乘法
 
在linux环境下进入项目目录下采用如下三条命令进行编译和运行：
1. nasm -f elf main.asm
2. ld -o main main.o -melf_i386
3. ./main


Nasm语法详见[nasm](http://www.bytekits.com/nasm/intro.html)
