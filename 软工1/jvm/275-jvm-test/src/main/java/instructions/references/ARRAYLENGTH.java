package instructions.references;

import instructions.base.Index16Instruction;
import instructions.base.Instruction;
import runtime.OperandStack;
import runtime.StackFrame;
import runtime.struct.JObject;

import java.nio.ByteBuffer;

public class ARRAYLENGTH extends Index16Instruction {
    public void execute(StackFrame frame) {
        OperandStack operandStack = frame.getOperandStack();
        //数组对象的引用
        JObject ref = operandStack.popObjectRef();
        if(ref==null){
            throw new NullPointerException();
        }
        //得到数组对象
        int[] array=ref.getInts();
        //将数组的长度压栈
        operandStack.pushInt(array.length);
    }
}
