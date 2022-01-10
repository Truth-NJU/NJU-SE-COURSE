package instructions.conversion;

import instructions.base.NoOperandsInstruction;
import runtime.OperandStack;
import runtime.StackFrame;

public class F2I extends NoOperandsInstruction {

    public void execute(StackFrame frame) {
        OperandStack stack = frame.getOperandStack();
        float value1 = stack.popFloat();
        int value2 = (int) value1;
        stack.pushInt(value2);
    }

}