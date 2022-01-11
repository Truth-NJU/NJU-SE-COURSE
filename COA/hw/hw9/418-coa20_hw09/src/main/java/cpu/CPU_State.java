package cpu;

import cpu.registers.*;
import cpu.registers.Register;

//维护了一个所有寄存器的列表，注意段寄存器只有16位，其他寄存器都是32位的
public class CPU_State {

    //寄存器列表
    public static Register cs = new CS();
    public static Register ds = new DS();
    public static Register ss = new SS();
    public static Register eflag = new EFlag();
    public static Register eip = new EIP();
    public static Register esp = new ESP();
    public static Register eax = new EAX();
    public static Register ecx = new ECX();
    public static Register edx = new EDX();
    public static Register ebx = new EBX();

    public static int ICC;  //control unit

}
