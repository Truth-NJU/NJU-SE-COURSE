package instructions.base;

import java.nio.ByteBuffer;

// 表示没有操作数的指令
public abstract class NoOperandsInstruction extends Instruction {
    public void fetchOperands(ByteBuffer reader) {
        //do nothing
    }

    public String toString() {
        return this.getClass().getSimpleName();
    }
}
