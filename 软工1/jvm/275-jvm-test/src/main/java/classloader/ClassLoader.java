package classloader;

import classloader.classfilereader.ClassFileReader;
import classloader.classfilereader.classpath.EntryType;
import com.njuse.jvmfinal.classloader.classfileparser.ClassFile;
import com.njuse.jvmfinal.memory.MethodArea;
import com.njuse.jvmfinal.memory.jclass.AccessFlags;
import com.njuse.jvmfinal.memory.jclass.Field;
import com.njuse.jvmfinal.memory.jclass.InitState;
import com.njuse.jvmfinal.memory.jclass.JClass;
import com.njuse.jvmfinal.memory.jclass.runtimeConstantPool.RuntimeConstantPool;
import com.njuse.jvmfinal.memory.jclass.runtimeConstantPool.constant.wrapper.DoubleWrapper;
import com.njuse.jvmfinal.memory.jclass.runtimeConstantPool.constant.wrapper.FloatWrapper;
import com.njuse.jvmfinal.memory.jclass.runtimeConstantPool.constant.wrapper.IntWrapper;
import com.njuse.jvmfinal.memory.jclass.runtimeConstantPool.constant.wrapper.LongWrapper;
import org.apache.commons.lang3.tuple.Pair;
import runtime.Vars;
import runtime.struct.JObject;
import runtime.struct.NullObject;

import java.io.IOException;

public class ClassLoader {
    private static ClassLoader classLoader = new ClassLoader();
    private ClassFileReader classFileReader = ClassFileReader.getInstance();
    private MethodArea methodArea=MethodArea.getInstance();

    public ClassLoader() {
    }

    public static ClassLoader getInstance() {
        return classLoader;
    }

    //加载类
    public JClass loadClass(String className) {
        JClass ret;
        if ((ret=methodArea.findClass(className)) == null && className.charAt(0)!='[') {
            return loadNonArrayClass(className);
        }
        else if((ret=methodArea.findClass(className))==null && className.charAt(0)=='['){
            return loadArrayClass(className);
        }
        return ret;
    }

    //非数组类
    private JClass loadNonArrayClass(String className) {
        try {
            Pair<byte[], Integer> res = this.classFileReader.readClassFile(className);
            byte[] data = res.getKey();
            EntryType definingEntry = new EntryType(res.getValue());
            JClass clazz = defineClass(data,definingEntry);
            this.linkClass(clazz);
            return clazz;
        }catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    //数组类的字节码不是从 class 文件中获取的，而是在加载了基本类型之后，在 JVM 中动态创建的
    //数组类
    private JClass loadArrayClass(String className) {
       /* JClass ret = new JClass();
        ret.setAccessFlags((short)1);
        ret.setName(className);
        ret.setInitState(InitState.SUCCESS);
        this.methodArea.addClass(ret.getName(), ret);

        try {
            ret.setSuperClass(this.tryLoad("java/lang/Object"));
            JClass[] interfaces = new JClass[]{this.tryLoad("java/lang/Cloneable"), this.tryLoad("java/io/Serializable")};
            ret.setInterfaces(interfaces);
        } catch (ClassNotFoundException var5) {
            var5.printStackTrace();
        }

        return ret;*/
        JClass ret = new JClass();
        return ret;
    }


    /**
     *
     * define class
     * @param data binary of class file
     * @param definingEntry defining loader of class
     */
    private JClass defineClass(byte[] data, EntryType definingEntry) throws ClassNotFoundException {
        ClassFile classFile = new ClassFile(data);
        JClass clazz = new JClass(classFile);
        //update load entry of the class
        clazz.setLoadEntryType(definingEntry);
        //load superclass recursively
        resolveSuperClass(clazz);
        //load interfaces of this class
        resolveInterfaces(clazz);
        methodArea.addClass(clazz.getName(),clazz);
        //add to method area
        return clazz;
    }

    /**
     * load superclass before add to method area
     */
    private void resolveSuperClass(JClass clazz) {
        if (!clazz.getName().equals("java/lang/Object")) {
            String superClassName = clazz.getSuperClassName();
            clazz.setSuperClass(this.loadClass(superClassName));
        }

    }

    /**
     * load interfaces before add to method area
     */
    private void resolveInterfaces(JClass clazz) {

        String[] interfaceNames=clazz.getInterfaceNames();
        int length=interfaceNames.length;
        //所有interface的JClass数组
        JClass[] interfaces=new JClass[length];
        clazz.setInterfaces(interfaces);
        for(int i=0; i<length; i++){
            interfaces[i]=loadClass(interfaceNames[i]);
        }
    }


    private void linkClass(JClass clazz) {
        verify(clazz);
        prepare(clazz);
    }


    private void verify(JClass clazz) {
    }

    private void prepare(JClass clazz) {
        calInstanceFieldSlotIDs(clazz);
        calStaticFieldSlotIDs(clazz);
        allocAndInitStaticVars(clazz);
        clazz.setInitState(InitState.PREPARED);
    }


    // 计算new一个对象所需的空间,单位是clazz.instanceSlotCount,主要包含了类的非静态成员变量(包含父类的)
    // 但是这里并没有真正的申请空间，只是计算大小，同时为每个非静态变量关联 slotId
    private void calInstanceFieldSlotIDs(JClass clazz) {
        int slotID = 0;
        if (clazz.getSuperClass() != null) {
            slotID = clazz.getSuperClass().getInstanceSlotCount();
        }
        Field[] fields = clazz.getFields();
        for (Field f : fields) {
            if (!f.isStatic()) {
                f.setSlotID(slotID);
                slotID++;
                if (f.isLongOrDouble()) slotID++;
            }
        }
        clazz.setInstanceSlotCount(slotID);
    }


    //计算类的静态成员变量所需的空间，不包含父类，同样也只是计算和分配 slotId，不申请空间
    private void calStaticFieldSlotIDs(JClass clazz) {
        int slotID = 0;
        Field[] fields = clazz.getFields();
        for (Field f : fields) {
            if (f.isStatic()) {
                f.setSlotID(slotID);
                slotID++;
                if (f.isLongOrDouble()) slotID++;
            }
        }
        clazz.setStaticSlotCount(slotID);

    }

    /**
     * primitive type is set to 0
     * ref type is set to null
     */
    private void initDefaultValue(JClass clazz, Field field) {

        if (clazz != null) {
            Vars vars = clazz.getStaticVars();
            if (vars != null) {
                int slotID = field.getSlotID();
                switch (field.getDescriptor()) {
                    case "Z":
                    case "S":
                    case "B":
                    case "C":
                    case "I":
                        vars.setInt(slotID, 0);
                        break;
                    case "J":
                        long longVal = 0;
                        vars.setLong(slotID, longVal);
                        break;
                    case "D":
                        double doubleVal = 0;
                        vars.setDouble(slotID, doubleVal);
                        break;
                    case "F":
                        float floatVal = 0;
                        vars.setFloat(slotID, floatVal);
                        break;
                    case "reference":
                        JObject reference = new NullObject();
                        vars.setObjectRef(slotID, reference);
                        break;
                    default:
                        break;
                }
            }
        }
    }



    private void loadValueFromRTCP(JClass clazz, Field field) {

        if (clazz != null) {
            Vars staticVars = clazz.getStaticVars();
            if (staticVars != null) {
                RuntimeConstantPool runtimeConstantPool = clazz.getRuntimeConstantPool();
                int slotID = field.getSlotID();
                int constantPoolIndex = field.getConstValueIndex();
                switch (field.getDescriptor()) {
                    case "Z":
                    case "B":
                    case "C":
                    case "S":
                    case "I":
                        int intVal = ((IntWrapper) runtimeConstantPool.getConstant(constantPoolIndex)).getValue();
                        staticVars.setInt(slotID, intVal);
                        break;
                    case "J":
                        long longVal = ((LongWrapper) runtimeConstantPool.getConstant(constantPoolIndex)).getValue();
                        staticVars.setLong(slotID, longVal);
                        break;
                    case "D":
                        double doubleVal = ((DoubleWrapper) runtimeConstantPool.getConstant(constantPoolIndex)).getValue();
                        staticVars.setDouble(slotID, doubleVal);
                        break;
                    case "F":
                        float floatVal = ((FloatWrapper) runtimeConstantPool.getConstant(constantPoolIndex)).getValue();
                        staticVars.setFloat(slotID, floatVal);
                        break;
                    default:
                        break;
                }
            }
        }

    }

    // 为静态变量申请空间,注意:这个申请空间的过程,就是将所有的静态变量赋值为0或者null;
    // 如果是 static final 的基本类型或者 String，其值会保存在ConstantValueAttribute属性中
    private void allocAndInitStaticVars(JClass clazz) {
        clazz.setStaticVars(new Vars(clazz.getStaticSlotCount()));
        Field[] fields = clazz.getFields();
        for (Field f : fields) {
            // 静态变量 初始化为 0系
            if(f.isStatic()){
                initDefaultValue(clazz,f);
            }
            // 常量 从常量池加载
            if(f.isFinal() && f.isStatic()) {
                loadValueFromRTCP(clazz, f);
            }
        }
    }
}
