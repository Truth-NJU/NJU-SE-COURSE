package instructions.constant;

import instructions.base.Instruction;
import runtime.StackFrame;

import java.nio.ByteBuffer;

//bipush指令从操作数中获取一个byte型整数，扩展成int型，然后推入栈顶
public class BIPUSH extends Instruction {
    private byte val;

    public BIPUSH() {
    }


    public void fetchOperands(ByteBuffer reader) {
        this.val = reader.get();
    }

    public void execute(StackFrame frame) {
        frame.getOperandStack().pushInt(this.val);
    }

    public String toString() {
        return this.getClass().getSimpleName() + " value : " + this.val;
    }
}
