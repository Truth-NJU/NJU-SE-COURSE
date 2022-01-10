package instructions.store;

import runtime.StackFrame;
import runtime.struct.JObject;


public class ASTORE_N extends STORE_N {
    public ASTORE_N(int index) {
        checkIndex(index);
        this.index = index;
    }

    public void execute(StackFrame frame) {
        /*JObject ref = frame.getOperandStack().popObjectRef();
        frame.getLocalVars().setObjectRef(this.index, ref);*/
        Store.astore(frame,index);
    }


}
