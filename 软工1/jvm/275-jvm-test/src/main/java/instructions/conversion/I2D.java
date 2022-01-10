package instructions.conversion;

import runtime.OperandStack;
import runtime.StackFrame;
import instructions.base.NoOperandsInstruction;

public class I2D extends NoOperandsInstruction {

    public void execute(StackFrame frame) {
        OperandStack stack = frame.getOperandStack();
        int value1 = stack.popInt();
        double value2 = (double) value1;
        stack.pushDouble(value2);
    }

}
