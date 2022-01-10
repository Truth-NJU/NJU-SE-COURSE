package instructions.comparsion;

import runtime.StackFrame;
import instructions.base.BranchInstruction;

public abstract class IFCOND extends BranchInstruction {


    public void execute(StackFrame frame) {
        int value = frame.getOperandStack().popInt();
        if (condition(value)) {
            int branchPC = frame.getNextPC() - 3 + super.offset;
            frame.setNextPC(branchPC);
        }
    }
    protected abstract boolean condition(int value);

}
