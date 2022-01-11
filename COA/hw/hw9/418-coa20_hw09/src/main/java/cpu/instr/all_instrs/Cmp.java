package cpu.instr.all_instrs;

import cpu.CPU_State;
import cpu.MMU;
import cpu.alu.ALU;
import cpu.registers.EFlag;
import cpu.registers.EIP;
import transformer.Transformer;

public class Cmp implements Instruction{
    MMU mmu=MMU.getMMU();
    Transformer t=new Transformer();

    @Override
    public int exec(String eip, int opcode) {
        String cs= CPU_State.cs.read();
        String logicAddr=cs+eip;
        char[] instr=mmu.read(logicAddr,40);
        String imm=String.valueOf(instr).substring(8);
        String reg=CPU_State.eax.read();
        int op1=Integer.parseInt(t.binaryToInt(reg));
        int op2=Integer.parseInt(t.binaryToInt(imm));
        //reg-imm，即操作数1-操作数2
        String res=new ALU().sub(imm,reg);
        String add=new ALU().add(imm,reg);
        int result=Integer.parseInt(t.binaryToInt(res));
        int addSign=Integer.parseInt(t.binaryToInt(add));
        EFlag eFlag=((EFlag)CPU_State.eflag);
        //判断是否溢chu
        boolean whetherOver=(op1>0 && op2>0 && addSign<0) ||  (op1<0 && op2<0 && addSign>0);
        if(result==0)  eFlag.setZF(true);
        if(result<0) eFlag.setCF(true);
        if(result>0) eFlag.setCF(false);
        if(result>0 && whetherOver){
            eFlag.setSF(true);
            eFlag.setOF(true);
        }
        if(result<0 && whetherOver){
            eFlag.setSF(false);
            eFlag.setOF(true);
        }
        if(result>0 && !whetherOver){
            eFlag.setSF(false);
            eFlag.setOF(false);
        }
        if(result<0 && !whetherOver){
            eFlag.setSF(true);
            eFlag.setOF(false);
        }
        //new EIP().plus(40);
        ((EIP)CPU_State.eip).plus(40);
        return 40;
    }



}
