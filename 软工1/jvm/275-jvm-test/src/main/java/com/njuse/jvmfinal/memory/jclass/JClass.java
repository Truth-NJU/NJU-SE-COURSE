package com.njuse.jvmfinal.memory.jclass;

import classloader.ClassLoader;
import classloader.classfilereader.classpath.EntryType;
import runtime.JThread;
import runtime.StackFrame;
import runtime.Vars;
import com.njuse.jvmfinal.classloader.classfileparser.ClassFile;
import com.njuse.jvmfinal.classloader.classfileparser.FieldInfo;
import com.njuse.jvmfinal.classloader.classfileparser.MethodInfo;
import com.njuse.jvmfinal.classloader.classfileparser.constantpool.ConstantPool;
import com.njuse.jvmfinal.memory.jclass.runtimeConstantPool.RuntimeConstantPool;

import lombok.Getter;
import lombok.Setter;
import runtime.struct.JObject;
import runtime.struct.NonArrayObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Getter
@Setter
public class JClass {
    private short accessFlags;
    private String name;
    private String superClassName;
    private String[] interfaceNames;
    private RuntimeConstantPool runtimeConstantPool;
    private Field[] fields;
    private Method[] methods;
    private EntryType loadEntryType; //请自行设计是否记录、如何记录加载器
    private JClass superClass;
    private JClass[] interfaces;
    private int instanceSlotCount;
    private int staticSlotCount;
    private Vars staticVars; // 请自行设计数据结构
     private InitState initState; // 请自行设计初始化状态
    private ClassLoader classLoader;
    public static HashMap<String, String> primitiveTypes;

    public JClass(ClassFile classFile) {
        this.accessFlags = classFile.getAccessFlags();
        this.name = classFile.getClassName();
        if (!this.name.equals("java/lang/Object")) {
            // index of super class of java/lang/Object is 0
            this.superClassName = classFile.getSuperClassName();
        } else {
            this.superClassName = "";
        }
        this.interfaceNames = classFile.getInterfaceNames();
        this.fields = parseFields(classFile.getFields());
        this.methods = parseMethods(classFile.getMethods());
        this.runtimeConstantPool = parseRuntimeConstantPool(classFile.getConstantPool());
    }

    public JClass() {
    }

    private Field[] parseFields(FieldInfo[] info) {
        int len = info.length;
        fields = new Field[len];
        for (int i = 0; i < len; i++) {
            fields[i] = new Field(info[i], this);
        }
        return fields;
    }

    private Method[] parseMethods(MethodInfo[] info) {
        int len = info.length;
        methods = new Method[len];
        for (int i = 0; i < len; i++) {
            methods[i] = new Method(info[i], this);
        }
        return methods;
    }

    private RuntimeConstantPool parseRuntimeConstantPool(ConstantPool cp) {
        return new RuntimeConstantPool(cp, this);
    }



    public Optional<Method> resolveMethod(String name, String descriptor) {
        for (Method m : methods) {
            if (m.getDescriptor().equals(descriptor)) {
                if(m.getName().equals(name)) {
                    return Optional.of(m);
                }
            }
        }
        return Optional.empty();
    }

    public NonArrayObject newObject() {
        return new NonArrayObject(this);
    }

    public JClass getComponentClass() {
        if (this.name.charAt(0) != '[') throw new RuntimeException("Invalid Array:" + this.name);
        //获得实例
        ClassLoader loader= ClassLoader.getInstance();
        String componentTypeDescriptor = this.name.substring(1);
        String classToLoad = toName(componentTypeDescriptor);
        //return loader.loadClass(classToLoad, this.loadEntryType);
        return loader.loadClass(classToLoad);
    }

    private  String toName(String descriptor) {
        if (descriptor.charAt(0) == '[') {
            // array
            return descriptor;
        }else if (descriptor.charAt(0) == 'L') {
            // object
            return descriptor.substring(1, descriptor.length() - 1);
        }
        else if(getPrimitiveType(this.name)!=null){
            return getPrimitiveType(this.name);
        }
        throw new RuntimeException("Invalid descriptor: " + descriptor);
    }

    /**
     * @return null if this classname is not a primitive type
     */

    private String getPrimitiveType(String className) {
        //HashMap<String, String> primitiveTypes = new HashMap<>();
        primitiveTypes.put("void", "V");
        primitiveTypes.put("boolean", "Z");
        primitiveTypes.put("byte", "B");
        primitiveTypes.put("short", "S");
        primitiveTypes.put("char", "C");
        primitiveTypes.put("int", "I");
        primitiveTypes.put("long", "J");
        primitiveTypes.put("float", "F");
        primitiveTypes.put("double", "D");
        return primitiveTypes.get(className);
    }

    /**
     * Class Init Methods
     */

    //if in multi-thread, jclass need a initstate lock
    public void initStart(JClass clazz) {
        clazz.initState = InitState.BUSY;
    }

    public void initSucceed(JClass clazz) {
        clazz.initState = InitState.SUCCESS;
    }

    private void initFail() {
        this.initState = InitState.FAIL;
    }

    /**
     * 这个方法初始化了这个类的静态部分
     */
    public void initClass(JThread thread, JClass clazz) {
        //判断有没有被初始化过,若被初始化过则直接返回
        if (clazz.getInitState()!= InitState.PREPARED) {
            return;
        }
        //开始初始化
        initStart(clazz);
        //获取clinit方法
        Method clinit = getMethodInClass("<clinit>", "()V", true);
        if(clinit==null){
            initSucceed(clazz);
            return;
        }
        //初始化所有父类
        if(clazz.getSuperClass()!=null){
            initClass(thread,clazz.getSuperClass());
        }
        //初始化所有接口
        JClass[] classes=clazz.getInterfaces();
        for(int i=0;i<classes.length;i++){
            initClass(thread,classes[i]);
        }

        runtime.StackFrame frame=new StackFrame(thread,clinit,clinit.getMaxStack(),clinit.getMaxLocal());
        thread.pushFrame(frame);
        //初始化成功
        initSucceed(clazz);
    }



    public Method getMethodInClass(String name, String descriptor, boolean isStatic) {
        JClass clazz = this;
        Method[] methods = clazz.getMethods();
        for (Method m : methods) {
            if (m.getDescriptor().equals(descriptor)
                    && m.getName().equals(name)
                    && m.isStatic() == isStatic) {
                return m;
            }
        }
        return null;
    }



    public String getPackageName() {
        int index = name.lastIndexOf('/');
        if (index >= 0) return name.substring(0, index);
        else return "";
    }

    public boolean isPublic() {
        return 0 != (this.accessFlags & AccessFlags.ACC_PUBLIC);
    }

    public boolean isInterface() {
        return 0 != (this.accessFlags & AccessFlags.ACC_INTERFACE);
    }

    public boolean isAbstract() {
        return 0 != (this.accessFlags & AccessFlags.ACC_ABSTRACT);
    }

    public boolean isAccSuper() {
        return 0 != (this.accessFlags & AccessFlags.ACC_SUPER);
    }

    public boolean isArray() {
        return this.name.charAt(0) == '[';
    }

    public boolean isJObjectClass() {
        return this.name.equals("java/lang/Object");
    }

    public boolean isJlCloneable() {
        return this.name.equals("java/lang/Cloneable");
    }

    public boolean isJIOSerializable() {
        return this.name.equals("java/io/Serializable");
    }


    public boolean isAccessibleTo(JClass caller) throws ClassNotFoundException {
        JClass callee= ClassLoader.getInstance().loadClass(name);
        int count=0;
        if(callee != null) {
            if (Objects.equals(caller.getPackageName(), callee.getPackageName())) count++;
            if(callee.isPublic()) count++;
        }
        if(count==2) return true;
        return false;
    }


    //refer to jvm8 6.5 instanceof inst
    public boolean isAssignableFrom(JClass other) throws ClassNotFoundException {
        // source 是否由 target 扩展而来（子类）
        JClass s = other;
        JClass t = this;
        if (s == t) return true;
        if (!s.isArray()) {
            if (!s.isInterface()) {
                if (!t.isInterface()) {
                    return s.isSubClassOf(t);
                } else {
                    // target 是接口
                    return s.isImplementOf(t);
                }
            } else {
                // source 是接口
                if (!t.isInterface()) {
                    return t.isJObjectClass();
                } else {
                    // target 也是接口
                    return t.isSuperInterfaceOf(s);
                }
            }
        } else {
            //source 是数组
            if (!t.isArray()) {
                if (!t.isInterface()) {
                    return t.isJObjectClass();
                } else {
                    // target 是接口
                    // t is interface;数组默认实现了Cloneable和Serializable接口
                    return t.isJIOSerializable() || t.isJlCloneable();
                }
            } else {
                // target 也是数组
                JClass sc = s.getComponentClass();
                JClass tc = t.getComponentClass();
                //return sc == tc || t.isJIOSerializable();
                return sc == tc || tc.isAssignableFrom(s);
            }
        }
    }

    public boolean isSubClassOf(JClass otherClass) {
        JClass superClass = this.getSuperClass();
        while (superClass!= null) {
            if (superClass == otherClass) return true;
            superClass = superClass.getSuperClass();
        }
        return false;
    }


    private boolean isImplementOf(JClass otherInterface) {
        JClass superClass = this;
        while (superClass != null) {
            JClass[] interfaces=this.getInterfaces();
            for(int i=0;i<interfaces.length;i++){
                if(interfaces[i]==otherInterface || interfaces[i].isSubInterfaceOf(otherInterface)) return true;
            }
            superClass = this.getSuperClass();
        }
        return false;
    }

    private boolean isSubInterfaceOf(JClass otherInterface) {
        JClass[] superInterfaces = this.getInterfaces();
        for (JClass i : superInterfaces) {
            if (i == otherInterface || i.isSubInterfaceOf(otherInterface)) return true;
        }
        return false;
    }

    private boolean isSuperInterfaceOf(JClass otherInterface) {
        return otherInterface.isSubInterfaceOf(this);
    }







}