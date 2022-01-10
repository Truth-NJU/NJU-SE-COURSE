package instructions.base;

import java.nio.ByteBuffer;

// 表示跳转指令，Offset 字段存放跳转偏移量。FetchOperands（）方法从字节码中读取一个 uint16 整数，转成 int 后赋给 Offset 字段。
public abstract class BranchInstruction extends Instruction {
    protected int offset;//type of offset is signed short

    public void fetchOperands(ByteBuffer reader) {
        offset = reader.getShort();
    }

    public String toString() {
        return this.getClass().getSimpleName() + " offset: " + offset;
    }
}
