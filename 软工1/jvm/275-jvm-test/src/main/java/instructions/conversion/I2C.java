package instructions.conversion;

import runtime.OperandStack;
import runtime.StackFrame;
import instructions.base.NoOperandsInstruction;

public class I2C extends NoOperandsInstruction {


    public void execute(StackFrame frame) {
        OperandStack stack = frame.getOperandStack();
        int value1=stack.popInt();
        //int value2=Integer.parseInt(String.valueOf((char) value1));
        int value2=(char)value1;
        stack.pushInt(value2);
    }

}
