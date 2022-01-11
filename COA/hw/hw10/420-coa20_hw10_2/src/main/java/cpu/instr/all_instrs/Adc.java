package cpu.instr.all_instrs;

import cpu.CPU_State;
import cpu.MMU;
import cpu.alu.ALU;
import cpu.registers.EFlag;
import cpu.registers.EIP;
import transformer.Transformer;

public class Adc implements Instruction {
    MMU mmu = MMU.getMMU();
    Transformer t=new Transformer();

    @Override
    public int exec(String eip, int opcode) {
        String cs = CPU_State.cs.read();
        String logicAddr = cs + eip;
        char[] instr = mmu.read(logicAddr, 40);
        String imm = String.valueOf(instr).substring(8);
        String reg = CPU_State.eax.read();
        ALU alu = new ALU();
        //先判断cf德状态再进行add，因为add会改变cf的状态
        String cf;
        if(((EFlag)CPU_State.eflag).getCF()){
            cf="1";
        }else cf="0";
        cf=t.intToBinary(cf);
        String result = alu.add(imm, reg);
        CPU_State.eax.write(alu.add(result,cf));
        ((EIP) CPU_State.eip).plus(40);
        return 40;
    }
}
