package com.njuse.jvmfinal.memory.jclass.runtimeConstantPool.constant.ref;

import classloader.ClassLoader;
import com.njuse.jvmfinal.memory.jclass.JClass;
import com.njuse.jvmfinal.memory.jclass.runtimeConstantPool.RuntimeConstantPool;
import com.njuse.jvmfinal.memory.jclass.runtimeConstantPool.constant.Constant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class SymRef implements Constant {
    public RuntimeConstantPool runtimeConstantPool;
    public String className;    //format : java/lang/Object
    public JClass clazz;

    public JClass getResolvedClass() throws ClassNotFoundException {
        if (clazz == null) {
            resolveClassRef();
        }
        return clazz;
    }

    public void resolveClassRef() throws ClassNotFoundException {
        JClass D = runtimeConstantPool.getClazz();
        JClass C = ClassLoader.getInstance().loadClass(className);

        if (!C.isAccessibleTo(D)) {
            try {
                throw new IllegalAccessException();
                //输出信息className + " is not accessible to " + D.getName()
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        clazz = C;
    }
}
