package instructions.comparsion;

import instructions.base.NoOperandsInstruction;
import runtime.OperandStack;
import runtime.StackFrame;

import java.math.BigDecimal;

public class FCMPL extends NoOperandsInstruction {


    public void execute(StackFrame frame) {
        OperandStack stack = frame.getOperandStack();
        float value2=stack.popFloat();
        float value1=stack.popFloat();
        if(Float.isNaN(value1)||Float.isNaN(value2)){
            stack.pushInt(-1);
        }else {
            stack.pushInt(Float.compare(value1,value2));
        }
    }

}