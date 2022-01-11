package cpu.instr.all_instrs;

import cpu.CPU_State;
import cpu.alu.ALU;
import cpu.registers.EIP;
import memory.Memory;
import transformer.Transformer;

public class Pop implements Instruction {
    Transformer t=new Transformer();
    @Override
    public int exec(String eip, int opcode) {
        //先做操作再加4 再write
        String esp=CPU_State.esp.read();
        String val= Memory.getMemory().topOfStack(esp);
        if(opcode==0x58){
            CPU_State.eax.write(val);
        }
        if(opcode==0x59){
            CPU_State.ecx.write(val);
        }
        if(opcode==0x5a){
            CPU_State.edx.write(val);
        }
        //new EIP().plus(8);
        String newEsp=new ALU().add("00000000000000000000000000000100",esp);
        CPU_State.esp.write(newEsp);
        ((EIP)CPU_State.eip).plus(8);
        return 8;
    }
}
