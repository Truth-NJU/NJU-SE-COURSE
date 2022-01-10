package instructions.references;

import com.njuse.jvmfinal.memory.jclass.JClass;
import com.njuse.jvmfinal.memory.jclass.runtimeConstantPool.RuntimeConstantPool;
import com.njuse.jvmfinal.memory.jclass.runtimeConstantPool.constant.ref.ClassRef;
import instructions.base.Index16Instruction;
import runtime.OperandStack;
import runtime.StackFrame;
import runtime.struct.JObject;

public class INSTANCE_OF extends Index16Instruction {
    @Override
    public void execute(StackFrame frame) throws ClassNotFoundException {
    }
}
