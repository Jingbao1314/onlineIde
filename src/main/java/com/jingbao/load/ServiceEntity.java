package com.jingbao.load;

import java.lang.reflect.Method;

/**
 * @author jijngbao
 * @date 19-7-20
 */
public class ServiceEntity {
    private Method method;
    private Class clazz;

    public ServiceEntity(Method method, Class clazz) {
        this.method = method;
        this.clazz = clazz;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Class getClazz() {
        return clazz;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }
}
