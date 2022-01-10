package instructions.store;

import jdk.nashorn.internal.scripts.JO;
import runtime.StackFrame;
import runtime.struct.JObject;


public class Store {
    public static void astore(StackFrame frame, int index) {
        JObject ref = frame.getOperandStack().popObjectRef();
        frame.getLocalVars().setObjectRef(index, ref);
    }

    public static void istore(StackFrame frame, int index) {
        int val = frame.getOperandStack().popInt();
        frame.getLocalVars().setInt(index, val);
    }

    public static void lstore(StackFrame frame, int index) {
        long val = frame.getOperandStack().popLong();
        frame.getLocalVars().setLong(index, val);
    }

    public static void fstore(StackFrame frame, int index) {
        float val = frame.getOperandStack().popFloat();
        frame.getLocalVars().setFloat(index, val);
    }

    public static void dstote(StackFrame frame, int index) {
        double val = frame.getOperandStack().popDouble();
        frame.getLocalVars().setDouble(index, val);
    }

    //用在 store 数组元素时，检测数组是否为 null
    public static void checkNotNull(JObject arrRef) {
        if (arrRef == null) {
            throw new NullPointerException();
        }
    }

    public static void checkIndex(int count, int index) {
        if (index < 0 || index >= count) {
            throw new ArrayIndexOutOfBoundsException();
        }
    }
}
