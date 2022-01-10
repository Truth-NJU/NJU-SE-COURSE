package instructions.load;

import runtime.StackFrame;
import instructions.base.Index8Instruction;

public class ILOAD extends Index8Instruction {

    public ILOAD() {
    }

    public void execute(StackFrame frame) {
        /*int val=frame.getLocalVars().getInt(this.index);
        frame.getOperandStack().pushInt(val);*/
        Load.iload(frame,index);
    }

}
