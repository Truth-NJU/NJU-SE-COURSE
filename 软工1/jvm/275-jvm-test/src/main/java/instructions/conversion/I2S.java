package instructions.conversion;

import runtime.OperandStack;
import runtime.StackFrame;
import instructions.base.NoOperandsInstruction;

public class I2S extends NoOperandsInstruction {


    public void execute(StackFrame frame) {
        OperandStack stack = frame.getOperandStack();
        int value1 = stack.popInt();
        int value2 = (short) value1;
        stack.pushInt(value2);
    }
}
