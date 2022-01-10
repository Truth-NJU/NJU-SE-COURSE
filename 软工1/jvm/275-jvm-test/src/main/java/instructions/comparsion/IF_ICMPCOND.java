package instructions.comparsion;

import runtime.OperandStack;
import runtime.StackFrame;
import instructions.base.BranchInstruction;


public abstract class IF_ICMPCOND extends BranchInstruction {

    public void execute(StackFrame frame) {
        OperandStack stack = frame.getOperandStack();
        int value2=stack.popInt();
        int value1=stack.popInt();
        if(condition(value1,value2)){
            int branchPC = frame.getNextPC() -3 + super.offset;
            frame.setNextPC(branchPC);
        }
    }

    protected abstract boolean condition(int v1, int v2);
}
