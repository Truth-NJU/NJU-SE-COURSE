package instructions.constant;

import com.njuse.jvmfinal.memory.jclass.runtimeConstantPool.constant.Constant;
import com.njuse.jvmfinal.memory.jclass.runtimeConstantPool.constant.wrapper.FloatWrapper;
import com.njuse.jvmfinal.memory.jclass.runtimeConstantPool.constant.wrapper.IntWrapper;
import com.njuse.jvmfinal.memory.jclass.runtimeConstantPool.constant.wrapper.StringWrapper;
import instructions.base.Index8Instruction;
import runtime.OperandStack;
import runtime.StackFrame;
import runtime.struct.JObject;


public class LDC extends Index8Instruction {

    public LDC() {
    }

    public void execute(StackFrame frame) {
        loadConstant(frame, this.index);
    }

    public static void loadConstant(StackFrame frame, int index) {
        //当前操作数栈
        OperandStack stack = frame.getOperandStack();
        //运行时常量池中对应的元素
        Constant constant = frame.getMethod().getClazz().getRuntimeConstantPool().getConstant(index);
        if (constant instanceof IntWrapper) {
            // 如果这个元素是IntWrapper， insert your code here
            stack.pushInt(((IntWrapper) constant).getValue());
        } else if (constant instanceof FloatWrapper) {
            stack.pushFloat(((FloatWrapper)constant).getValue());
        }
        else throw new ClassFormatError();
    }

}
