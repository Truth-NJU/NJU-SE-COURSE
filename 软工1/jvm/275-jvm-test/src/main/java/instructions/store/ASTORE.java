package instructions.store;

import instructions.base.Index8Instruction;
import runtime.StackFrame;
import runtime.struct.JObject;


public class ASTORE extends Index8Instruction {
    public ASTORE() {
    }

    public void execute(StackFrame frame) {
        /*JObject ref = frame.getOperandStack().popObjectRef();
        frame.getLocalVars().setObjectRef(this.index, ref);*/
        Store.astore(frame,index);
    }
}
