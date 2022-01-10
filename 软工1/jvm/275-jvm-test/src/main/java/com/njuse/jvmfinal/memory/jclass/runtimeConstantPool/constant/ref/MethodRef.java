package com.njuse.jvmfinal.memory.jclass.runtimeConstantPool.constant.ref;

import com.njuse.jvmfinal.classloader.classfileparser.constantpool.info.MethodrefInfo;
import com.njuse.jvmfinal.memory.jclass.JClass;
import com.njuse.jvmfinal.memory.jclass.Method;
import com.njuse.jvmfinal.memory.jclass.runtimeConstantPool.RuntimeConstantPool;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MethodRef extends MemberRef {
    private Method method;

    public MethodRef(RuntimeConstantPool runtimeConstantPool, MethodrefInfo methodrefInfo) {
        super(runtimeConstantPool, methodrefInfo);
    }

    public Method resolveMethodRef(JClass clazz) throws ClassNotFoundException {
        return resolve(clazz);
    }


    public Method resolveMethodRef() {
        if (method == null) {
            try {
                resolveClassRef();
                return resolve(clazz);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    @Override
    public String toString() {
        return "MethodRef to " + className;
    }


    public Method resolve(JClass clazz) throws ClassNotFoundException {
        Method method = lookupMethodInClass(clazz, name, descriptor);
        return method;
    }

    public static Method lookupMethodInClass(JClass clazz, String name, String descriptor) {
        JClass c = clazz;
        while (c != null) {
            for (Method method : c.getMethods()) {
                if (method.name.equals(name) && method.descriptor.equals(descriptor)) {
                    return method;
                }
            }
            c = c.getSuperClass();
        }
        return null;
    }

}
