package instructions.constant;

import instructions.base.Instruction;
import runtime.StackFrame;

import java.nio.ByteBuffer;

//sipush指令从操作数中获取一个short型整数，扩展成int型，然后推入栈顶
public class SIPUSH extends Instruction {
    private short val;

    public SIPUSH() {
    }


    public void fetchOperands(ByteBuffer reader) {
        this.val = reader.getShort();
    }

    public void execute(StackFrame frame) {
        frame.getOperandStack().pushInt(this.val);
    }

    public String toString() {
        return this.getClass().getSimpleName() + " value : " + this.val;
    }
}
