package instructions.store;

import runtime.StackFrame;
import instructions.base.Index8Instruction;
import runtime.OperandStack;

public class ISTORE extends Index8Instruction {


    public void execute(StackFrame frame) {
        /*OperandStack stack = frame.getOperandStack();
        int val=stack.popInt();
        frame.getLocalVars().setInt(index,val);
        frame.setLocalVars(frame.getLocalVars());*/
        Store.istore(frame,index);
    }

}
