package instructions.store;

import instructions.base.Index8Instruction;
import runtime.StackFrame;

public class FSTORE extends Index8Instruction {

    @Override
    public void execute(StackFrame frame) {
        Store.fstore(frame,index);
    }
}
