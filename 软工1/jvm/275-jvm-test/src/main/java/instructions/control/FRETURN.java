package instructions.control;

import instructions.base.NoOperandsInstruction;
import runtime.JThread;
import runtime.OperandStack;
import runtime.StackFrame;

public class FRETURN extends NoOperandsInstruction {

    public void execute(StackFrame frame) {
        // 获取当前线程
        JThread thread=frame.getThread();
        //弹出当前帧
        thread.popFrame();
        //获取调用方法帧
        StackFrame invokeFrame=thread.getTopFrame();
        //弹出当前帧的操作数栈顶float类型的值
        OperandStack stack = frame.getOperandStack();
        float val=stack.popFloat();
        // 将返回值推入调用方法帧的操作数栈顶
        invokeFrame.getOperandStack().pushFloat(val);

    }

}
