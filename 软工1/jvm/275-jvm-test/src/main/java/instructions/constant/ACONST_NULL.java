package instructions.constant;

import runtime.StackFrame;
import instructions.base.NoOperandsInstruction;
import runtime.struct.NullObject;


public class ACONST_NULL extends NoOperandsInstruction {
    public void execute(StackFrame frame) {
        frame.getOperandStack().pushObjectRef(null);
    }

}
