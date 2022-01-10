package instructions.control;

import runtime.JThread;
import runtime.StackFrame;
import instructions.base.NoOperandsInstruction;

public class RETURN extends NoOperandsInstruction {

    public void execute(StackFrame frame) {
        frame.getThread().popFrame();
    }

}
