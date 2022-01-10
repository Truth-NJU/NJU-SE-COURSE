package instructions.math;

import runtime.OperandStack;
import runtime.StackFrame;
import instructions.base.NoOperandsInstruction;

public class ISUB extends NoOperandsInstruction {

    public void execute(StackFrame frame) {
        OperandStack stack = frame.getOperandStack();
        int val2 = stack.popInt();
        int val1 = stack.popInt();
        int res=val1-val2;
        stack.pushInt(res);
    }

}
