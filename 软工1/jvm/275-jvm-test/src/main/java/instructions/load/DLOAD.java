package instructions.load;

import runtime.StackFrame;
import instructions.base.Index8Instruction;

public class DLOAD extends Index8Instruction {


     /*//从局部变量表读取对应元素
    float val = frame.getLocalVars().getFloat(this.index);
    //将这个元素压入操作数栈
    frame.getOperandStack().pushFloat(val);
     */
    public void execute(StackFrame frame) {
        /*double value=frame.getLocalVars().getDouble(this.index);
        frame.getOperandStack().pushDouble(value);*/
        Load.dload(frame,index);
    }


}
