package instructions.references;

import classloader.ClassLoader;
import com.njuse.jvmfinal.memory.jclass.InitState;
import com.njuse.jvmfinal.memory.jclass.JClass;
import com.njuse.jvmfinal.memory.jclass.runtimeConstantPool.constant.ref.ClassRef;
import instructions.base.Index16Instruction;
import instructions.base.Index8Instruction;
import jdk.nashorn.internal.scripts.JO;
import runtime.OperandStack;
import runtime.StackFrame;
import runtime.struct.JObject;

public class NEWARRAY extends Index8Instruction {
    //Array Type  atype
    private final int AT_BOOLEAN = 4;
    private final int AT_CHAR = 5;
    private final int AT_FLOAT = 6;
    private final int AT_DOUBLE = 7;
    private final int AT_BYTE = 8;
    private final int AT_SHORT = 9;
    private final int AT_INT = 10;
    private final int AT_LONG = 11;

    //获取基本类型数组的class;如果没有加载过,需要加载进JVM
    private JClass getPrimitiveArrayClass(ClassLoader loader) {
        //从字节码中获取到的 index 表明的是哪种类型的数组
        switch (this.index) {
            case AT_BOOLEAN:
                return loader.loadClass("[Z");
            case AT_BYTE:
                return loader.loadClass("[B");
            case AT_CHAR:
                return loader.loadClass("[C");
            case AT_SHORT:
                return loader.loadClass("[S");
            case AT_INT:
                return loader.loadClass("[I");
            case AT_LONG:
                return loader.loadClass("[J");
            case AT_FLOAT:
                return loader.loadClass("[F");
            case AT_DOUBLE:
                return loader.loadClass("[D");
            default:
                throw new RuntimeException("Invalid atype!");
        }
    }

    @Override
    public void execute(StackFrame frame) {
    }
}
