package instructions.store;

import instructions.base.Index8Instruction;
import runtime.OperandStack;
import runtime.StackFrame;


public class DSTORE extends Index8Instruction {


    public void execute(StackFrame frame) {
        /*OperandStack stack = frame.getOperandStack();
        double val=stack.popDouble();
        frame.getLocalVars().setDouble(index,val);
        frame.setLocalVars(frame.getLocalVars());*/
        Store.dstote(frame,index);
    }

}
