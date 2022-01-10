package instructions.load;

import runtime.StackFrame;
import runtime.struct.JObject;

public class Load {
    public static void aload(StackFrame frame, int index) {
        JObject ref = frame.getLocalVars().getObjectRef(index);
        frame.getOperandStack().pushObjectRef(ref);
    }

    public static void dload(StackFrame frame, int index) {
        double val = frame.getLocalVars().getDouble(index);
        frame.getOperandStack().pushDouble(val);
    }

    public static void fload(StackFrame frame, int index) {
        float val = frame.getLocalVars().getFloat(index);
        frame.getOperandStack().pushFloat(val);
    }

    public static void iload(StackFrame frame, int index) {
        int val = frame.getLocalVars().getInt(index);
        frame.getOperandStack().pushInt(val);
    }

    public static void lload(StackFrame frame, int index) {
        long val = frame.getLocalVars().getLong(index);
        frame.getOperandStack().pushLong(val);
    }

    //用在 load 数组元素时，检测数组是否为 null
    public static void checkNotNull(JObject arrRef) {
        if (arrRef == null) {
            throw new NullPointerException();
        }
    }

    public static void checkIndex(int count, int index) {
        if (index < 0 || index >= count) {
            throw new ArrayIndexOutOfBoundsException("index: " + index + " array's count: " + count);
        }
    }
}
