package instructions.control;

import runtime.JThread;
import runtime.OperandStack;
import runtime.StackFrame;
import instructions.base.NoOperandsInstruction;

/*返回值为 int 的 return 指令
 * 执行方法在执行结束后，如果有返回值，其返回值会放在该方法的操作数栈
 * 执行方法的外部——调用方法，需要将执行方法的返回值，压入调用方法的操作数栈*/
public class  IRETURN extends NoOperandsInstruction {

    public void execute(StackFrame frame) {
        // 获取当前线程
        JThread thread=frame.getThread();
        //弹出当前帧
        thread.popFrame();
        //获取调用方法帧
        StackFrame invokeFrame=thread.getTopFrame();
        //弹出当前帧的操作数栈顶int类型的值
        OperandStack stack = frame.getOperandStack();
        int val=stack.popInt();
        // 将返回值推入调用方法帧的操作数栈顶
        invokeFrame.getOperandStack().pushInt(val);

    }
}
