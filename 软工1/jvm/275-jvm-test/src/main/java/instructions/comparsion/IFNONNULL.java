package instructions.comparsion;

import runtime.StackFrame;
import instructions.base.BranchInstruction;
import runtime.struct.JObject;

public class IFNONNULL extends BranchInstruction {


    public void execute(StackFrame frame) {
        JObject value = frame.getOperandStack().popObjectRef();
        if(value!=null){
            int branchPC = frame.getNextPC() - 3 + super.offset;
            frame.setNextPC(branchPC);
        }
    }
}
