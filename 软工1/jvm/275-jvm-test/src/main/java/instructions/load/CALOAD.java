package instructions.load;



import instructions.base.NoOperandsInstruction;
import runtime.OperandStack;
import runtime.StackFrame;
import runtime.struct.JObject;

public class CALOAD extends NoOperandsInstruction {

    public void execute(StackFrame frame) {
        OperandStack operandStack = frame.getOperandStack();
        //数组元素的索引值
        int index = operandStack.popInt();
        //数组对象的引用
        JObject arrRef = operandStack.popObjectRef();
        if (arrRef == null) {
            throw new NullPointerException();
        }
        //得到数组对象
        char[] refs = arrRef.getChars();
        if (index < 0 || index >= refs.length) {
            throw new ArrayIndexOutOfBoundsException();
        }
        //将数组的 index 的值压栈
        operandStack.pushInt(refs[index]);
    }

}
