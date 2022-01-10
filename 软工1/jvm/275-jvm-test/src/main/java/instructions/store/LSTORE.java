package instructions.store;

import instructions.base.Index8Instruction;
import runtime.OperandStack;
import runtime.StackFrame;

public class LSTORE extends Index8Instruction {

    public void execute(StackFrame frame) {
        /*OperandStack stack = frame.getOperandStack();
        int val=stack.popInt();
        frame.getLocalVars().setInt(index,val);
        frame.setLocalVars(frame.getLocalVars());*/
        Store.lstore(frame,index);
    }

}
