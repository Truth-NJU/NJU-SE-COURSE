package instructions.conversion;

import runtime.OperandStack;
import runtime.StackFrame;
import instructions.base.NoOperandsInstruction;

public class I2F extends NoOperandsInstruction {


    public void execute(StackFrame frame) {
        OperandStack stack = frame.getOperandStack();
        int value1=stack.popInt();
        float value2= (float) value1;
        stack.pushFloat(value2);
    }
}

