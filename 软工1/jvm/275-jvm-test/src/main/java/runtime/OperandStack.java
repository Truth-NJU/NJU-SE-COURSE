package runtime;

import runtime.struct.JObject;
import runtime.struct.Slot;

import java.util.EmptyStackException;

public class OperandStack {
    private int top;
    private int maxStackSize;
    private Slot[] slots;

    public OperandStack(int maxStackSize) {
        if(maxStackSize>=0) {
            this.maxStackSize = maxStackSize;
            slots = new Slot[maxStackSize];
            for (int i = 0; i < maxStackSize; i++) slots[i] = new Slot();
            top = 0;
        }else{
            throw new NullPointerException();
        }
    }

    /**向操作数栈顶端push一个int型变量
     *
     * @param value 变量的值
     */
    public void pushInt(int value) {
        if (top >= maxStackSize) throw new StackOverflowError();
        slots[top].setValue(value);
        top++;
    }

    /**从操作数栈顶端pop一个int型变量
     *
     * @return 返回这个int的值
     */
    public int popInt() {
        top--;
        if (top < 0) throw new EmptyStackException();
        int ret=slots[top].getValue();
        slots[top]=new Slot();
        return ret;
    }

    public void pushFloat(float value) {
        if (top >= maxStackSize) throw new StackOverflowError();
        int num=Float.floatToIntBits(value);
        slots[top].setValue(num);
        top++;
    }

    public float popFloat() {
        top--;
        if (top < 0) throw new EmptyStackException();
        float ret = Float.intBitsToFloat(slots[top].getValue());
        slots[top] = new Slot();
        return ret;
    }

    /**向操作数栈顶push一个 long 类型的变量
     *
     * @param value 变量的值
     */
    public void pushLong(long value) {
        if (top+1 >= maxStackSize) throw new StackOverflowError();
        //高位
        int high= (int) ((0xFFFFFFFF00000000L & value) >> 32);
        //低位
        int low= (int) (0xFFFFFFFFL & value);
        //先低位后高位
        pushInt(low);
        pushInt(high);

    }

    /**从操作数栈顶端pop一个long型变量
     *
     * @return 返回这个long的值
     */
    public long popLong() {
        if (top < 0) throw new EmptyStackException();
        int high=popInt();
        int low=popInt();
        long ret=((long)low & 0xFFFFFFFFL) | (((long)high << 32) & 0xFFFFFFFF00000000L);
        return ret;
    }

    public void pushDouble(double value) {
        long bit=Double.doubleToLongBits(value);
        pushLong(bit);
    }

    /**从操作数栈顶端pop一个double型变量
     *
     * @return 返回这个double的值
     */
    public double popDouble() {
        if (top < 0) throw new EmptyStackException();
        long val=popLong();
        double ret=Double.longBitsToDouble(val);
        slots[top] = new Slot();
        return ret;
    }

    public void pushObjectRef(JObject ref) {
        if (top >= maxStackSize) throw new StackOverflowError();
        slots[top].setObject(ref);
        top++;
    }

    public JObject popObjectRef() {
        top--;
        if (top < 0) throw new EmptyStackException();
        JObject ret = slots[top].getObject();
        slots[top] = new Slot();
        return ret;
    }

    public void pushSlot(Slot slot) {
        if (top >= maxStackSize) throw new StackOverflowError();
        slots[top] = slot;
        top++;
    }

    public Slot popSlot() {
        top--;
        if (top < 0) throw new EmptyStackException();
        Slot ret = slots[top];
        slots[top] = new Slot();
        return ret;
    }
    public JObject getRefFromTop(int n) {
        return slots[maxStackSize - 1 - n].getObject();
    }

    public Slot[] getSlots() {
        return this.slots;
    }

}
