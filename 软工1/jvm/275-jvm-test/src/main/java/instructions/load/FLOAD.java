package instructions.load;

import instructions.base.Index8Instruction;
import runtime.StackFrame;

/*//从局部变量表读取对应元素
  float val = frame.getLocalVars().getFloat(this.index);
  //将这个元素压入操作数栈
  frame.getOperandStack().pushFloat(val);
   */

public class FLOAD extends Index8Instruction {

    public FLOAD() {
    }

    public void execute(StackFrame frame) {
        /*float value=frame.getLocalVars().getFloat(this.index);
        frame.getOperandStack().pushFloat(value);*/
        Load.fload(frame,index);
    }

}
