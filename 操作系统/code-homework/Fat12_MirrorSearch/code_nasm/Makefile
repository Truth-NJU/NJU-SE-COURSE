all:
	nasm -f elf -o print.o my_print.asm
	g++ -m32 -c -o main.o main.cpp
	g++ -m32 -o fat main.o print.o
	./fat
