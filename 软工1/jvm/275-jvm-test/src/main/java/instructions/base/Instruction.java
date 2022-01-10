package instructions.base;

import runtime.StackFrame;

import java.nio.ByteBuffer;

public abstract class Instruction {
    public abstract void execute(StackFrame frame) throws ClassNotFoundException;

    //从字节码中提取操作数
    public abstract void fetchOperands(ByteBuffer reader);

    public String getName() {
        return this.getClass().toString().substring(this.getClass().toString().lastIndexOf(".")+1);}
}
