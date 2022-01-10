package instructions.conversion;

import runtime.OperandStack;
import runtime.StackFrame;
import instructions.base.NoOperandsInstruction;

public class I2L extends NoOperandsInstruction {


    public void execute(StackFrame frame) {
        OperandStack stack = frame.getOperandStack();
        int value1=stack.popInt();
        //long value2= Long.parseLong(String.valueOf(value1));
        long value2=(long) value1;
        stack.pushLong(value2);
    }
}
