package instructions.conversion;

import instructions.base.NoOperandsInstruction;
import runtime.OperandStack;
import runtime.StackFrame;

public class L2F extends NoOperandsInstruction {


    public void execute(StackFrame frame) {
        OperandStack stack = frame.getOperandStack();
        long value1=stack.popLong();
        float value2= (float) value1;
        stack.pushFloat(value2);
    }

}

