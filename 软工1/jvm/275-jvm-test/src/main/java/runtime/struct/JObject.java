package runtime.struct;

import com.njuse.jvmfinal.memory.jclass.JClass;
import runtime.Vars;

public class JObject {
    protected static int numInHeap;
    protected int id;
    protected JClass clazz;
    protected boolean isNull = false;

    //存放的是非静态成员变量,包含父类+ 自己的；或者存放数组
    private Object data;

    static {
        numInHeap = 0;
    }

    public JObject() {
        id = numInHeap;
    }


    public boolean isInstanceOf(JClass clazz) throws ClassNotFoundException {
        return this.clazz.isAssignableFrom(clazz);
    }

    public JClass getClazz() {
        return this.clazz;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public JObject(JClass clazz, Object data) {
        this.clazz = clazz;
        this.data = data;
    }
    //为数组添加一些特有的方法：
    public byte[] getBytes() {
        return (byte[]) data;
    }

    public char[] getChars() {
        return (char[]) data;
    }

    public short[] getShorts() {
        return (short[]) data;
    }

    public int[] getInts() {
        return (int[]) data;
    }

    public long[] getLongs() {
        return (long[]) data;
    }

    public float[] getFloats() {
        return (float[]) data;
    }

    public double[] getDoubles() {
        return (double[]) data;
    }

    public JObject[] getRefs() {
        return (JObject[]) data;
    }

    public int getArrayLen() {
        switch (data.getClass().getSimpleName()) {
            case "byte[]":
                return getBytes().length;
            case "short[]":
                return getShorts().length;
            case "char[]":
                return getChars().length;
            case "int[]":
                return getInts().length;
            case "long[]":
                return getLongs().length;
            case "float[]":
                return getFloats().length;
            case "double[]":
                return getDoubles().length;
            case "Zobject[]":
                return getRefs().length;
            default:
                throw new RuntimeException();
        }
    }
}
